package org.ancastal.banknative.listeners;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.milkbowl.vault.economy.Economy;
import org.ancastal.banknative.BankNative;
import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.gui.AccountMenu;
import org.ancastal.banknative.models.Account;
import org.ancastal.banknative.models.Bank;
import org.ancastal.banknative.settings.Settings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.remain.CompMetadata;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ATM_Listener implements Listener {

	private final Database database;
	private final BankNative plugin;


	public ATM_Listener(Database database, BankNative plugin) {
		this.database = database;
		this.plugin = plugin;
	}


	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		Block block = e.getBlock();

		if (!Objects.equals(e.getLine(0), "[ATM]") || !isWallSign(block)) {
			return;
		}

		Player player = e.getPlayer();
		Bank bank = null;
		try {
			bank = database.getBankByOwner(player.getUniqueId().toString());
			if (bank == null) {
				Common.tell(player, "&cYou do not own any bank.");
				return;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return;
		}

		int allowedATMs = 1;
		if (PlayerUtil.hasPerm(player, "PrivateBanks.atm.5")) {
			allowedATMs = 5;
		} else if (PlayerUtil.hasPerm(player, "PrivateBanks.atm.4")) {
			allowedATMs = 4;
		} else if (PlayerUtil.hasPerm(player, "PrivateBanks.atm.3")) {
			allowedATMs = 3;
		} else if (PlayerUtil.hasPerm(player, "PrivateBanks.atm.2")) {
			allowedATMs = 2;
		}

		if (bank.getActiveATMs() >= allowedATMs) {
			return;
		}

		block.setType(Material.PLAYER_WALL_HEAD);
		Skull skull = (Skull) block.getState();
		skull.setOwningPlayer(Bukkit.getOfflinePlayer("zasf"));
		skull.setRotation(e.getPlayer().getFacing().getOppositeFace());
		skull.update();
		CompMetadata.setMetadata(skull, "ATM", String.valueOf(player.getUniqueId()));
		database.increaseActiveATMs(bank);

		Location location = block.getLocation().add(0.5, 2, 0.5);
		List<String> lines = List.of(ChatColor.AQUA + "[ATM]", bank.getBankName());

		int randomNumber = RandomUtil.nextInt(1000000);
		String hologramName = "ATM" + BankNative.getNamed() + randomNumber;

		Hologram hologram = HolographicDisplaysAPI.get(plugin).createHologram(location);
		for (String line : lines) {
			hologram.getLines().appendText(line);
		}
		Common.log("Created hologram [" + hologramName + "] by " + player.getName());
	}


	private boolean isWallSign(Block block) {
		return block.getType() == Material.LEGACY_WALL_SIGN || block.getType() == Material.OAK_WALL_SIGN;
	}


	@EventHandler
	public void onBlockClick(PlayerInteractEvent e) throws SQLException {
		if (e.getHand() != EquipmentSlot.HAND || e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		Block block = e.getClickedBlock();
		if (block == null || !block.getType().equals(Material.PLAYER_WALL_HEAD)) {
			return;
		}

		Player sender = e.getPlayer();

		// Check if sign is an ATM sign
		String atmMetadata = CompMetadata.getMetadata(block.getState(), "ATM");

		if (atmMetadata == null) return;

		UUID bankOwnerUUID = UUID.fromString(atmMetadata);
		OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(bankOwnerUUID);

		if (!bankOwner.hasPlayedBefore()) {
			return;
		}

		// Get player's bank account
		Bank bank = database.getBankByOwner(bankOwner.getUniqueId().toString());
		if (bank == null) {
			sender.sendMessage(ChatColor.RED + "This ATM does not belong to a valid bank.");
			return;
		}

		List<Account> accounts = database.getAccountsList(bank.getBankName());
		if (accounts == null || accounts.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "Your bank has no active deposit accounts.");
			return;
		}

		Account depositAccount = database.getAccountHolder(sender.getUniqueId().toString());
		if (depositAccount == null) {
			sender.sendMessage(ChatColor.RED + "You do not own a deposit account.");
			return;
		}

		boolean accountFound = false;
		for (Account account : accounts) {
			if (account.getUuid().equals(depositAccount.getUuid())) {
				new AccountMenu(sender, account, database).displayTo(sender);
				accountFound = true;
				break;
			}
		}

		if (!accountFound) {
			sender.sendMessage(ChatColor.YELLOW + "This ATM does not belong to your bank.\nYou will be charged 10 " + Settings.getCurrency() + " to use it.");
			Economy economy = BankNative.getEconomy();
			economy.withdrawPlayer(sender, 10);
			database.give(bank, 10D);
			new AccountMenu(sender, depositAccount, database).displayTo(sender);
		}
	}


	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) throws SQLException {
		Block block = e.getBlock();
		if (isATM(block)) {
			if (doesPlayerOwnATM(block, e.getPlayer()) || e.getPlayer().isOp()) {
				HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin);
				Hologram hologram = findClosestHologram(e.getPlayer().getLocation(), api);
				if (hologram != null) {
					hologram.delete();
					Bank bank = database.getBankByOwner(e.getPlayer().getUniqueId().toString());
					if (bank != null) {
						database.decreaseActiveATMs(bank);
					}
				}
			} else {
				e.getPlayer().sendMessage(ChatColor.RED + "You cannot delete an ATM that does not belong to you.");
				e.setCancelled(true);
			}
		}
	}

	private boolean isATM(Block block) {
		if (block.getType().equals(Material.PLAYER_WALL_HEAD)) {
			String ATM_Metadata = CompMetadata.getMetadata(block.getState(), "ATM");
			return ATM_Metadata != null;
		}
		return false;
	}

	private boolean doesPlayerOwnATM(Block block, Player player) {
		if (isATM(block)) {
			String ATM_Metadata = CompMetadata.getMetadata(block.getState(), "ATM");
			UUID uuid = UUID.fromString(ATM_Metadata);
			return uuid.equals(player.getUniqueId());
		}
		return false;
	}

	private Hologram findClosestHologram(Location location, HolographicDisplaysAPI api) {
		int range = 10;
		double minDistance = Double.MAX_VALUE;
		Hologram closestHologram = null;
		for (int x = location.getBlockX() - range; x <= location.getBlockX() + range; x++) {
			for (int y = location.getBlockY() - range; y <= location.getBlockY() + range; y++) {
				for (int z = location.getBlockZ() - range; z <= location.getBlockZ() + range; z++) {
					Location blockLocation = new Location(location.getWorld(), x, y, z);
					for (Hologram hologram : api.getHolograms()) {
						if (hologram.getPosition().distance(blockLocation) < minDistance) {
							minDistance = hologram.getPosition().distance(blockLocation);
							closestHologram = hologram;
							return closestHologram;
						}
					}
				}
			}
		}
		return null;
	}


	// show menu to sender
}


