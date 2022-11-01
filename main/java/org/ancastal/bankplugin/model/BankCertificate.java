package org.ancastal.bankplugin.model;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.ancastal.bankplugin.BankPlugin;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

@Getter
public class BankCertificate {

	protected final static String warnPrefix = "&8&l[&4&l\u2715&8&l]&c ";
	protected final static String successPrefix = "&8&l[&2&l\u2714&8&l]&7 ";

	private final static String lore = "\n"
			+ "This is a certificate of"
			+ "\nownership of a bank."
			+ "\n"
			+ "\n&f&lCERTIFICATE";


	private void createBank(String bankName) {
		Economy economy = BankPlugin.getEconomy();
		economy.createPlayerAccount(bankName);
	}


	public ItemStack getItem(OfflinePlayer player, String bankName) {
		createBank(bankName);
		ItemStack certificate = new ItemStack(
				ItemCreator.of(
								CompMaterial.PAPER,
								"&b" + bankName,
								getLore()
						).hideTags(true)
						.glow(true)
						.make()
		);


		return certificate;
	}

	public static boolean checkCertificate(Player player, ItemStack item) {
		if (item.getType() != Material.PAPER) {
			Common.tellNoPrefix(player, warnPrefix + "You need to hold your Bank Certificate");
			return false;
		}
		if (CompMetadata.getMetadata(item, "playerName") == null) {
			Common.tellNoPrefix(player, warnPrefix + "Your Bank Certificate is not valid.");
			return false;
		}
		if (!CompMetadata.getMetadata(item, "playerName").equals(player.getName())) {
			Common.tellNoPrefix(player, warnPrefix + "This bank certificate belongs to " + CompMetadata.getMetadata(item, "playerName"));
			return false;
		}
		return true;
		/*
		checkBoolean(item.getType() != Material.AIR, "You need to hold your Bank Certificate.");
		checkBoolean(CompMetadata.getMetadata(item, "playerName") != null, "Your Bank Certificate is not valid.");
		checkBoolean(CompMetadata.getMetadata(item, "playerName").equals(player.getName()), "This bank certificate belongs to " + CompMetadata.getMetadata(item, "playerName"));
		 */
	}


	private static String getLore() {
		return lore;
	}

	public static void withdrawFromBank(String bankName, Player receivingPlayer, Double amount) {
		Economy economy = BankPlugin.getEconomy();
		if (economy.getBalance(bankName) < amount) {
			Common.tellNoPrefix(receivingPlayer, warnPrefix + "The bank does not have enough money.");
			return;
		}
		economy.withdrawPlayer(bankName, amount);
		economy.depositPlayer(receivingPlayer, amount);

		Common.tellNoPrefix(receivingPlayer, successPrefix + "You have withdrawn &l" + amount + "kr &7from the bank!");

	}


	public static void depositToBank(String bankName, Player sendingPlayer, Double amount) {
		Economy economy = BankPlugin.getEconomy();
		if (economy.getBalance(sendingPlayer) <= amount) {
			Common.tellNoPrefix(sendingPlayer, warnPrefix + "You do not have enough money.");
			return;
		}
		economy.withdrawPlayer(sendingPlayer, amount);
		economy.depositPlayer(bankName, amount);

		Common.tellNoPrefix(sendingPlayer, successPrefix + "You have deposited &l" + amount + "kr &7into the bank!");
	}

	public static void transferToPlayer(String bankName, Player sendingPlayer, Player receivingPlayer, Double amount) {
		Economy economy = BankPlugin.getEconomy();
		if (economy.getBalance(bankName) < amount) {
			Common.tellNoPrefix(receivingPlayer, warnPrefix + "The bank does not have enough money.");
			return;
		}
		economy.withdrawPlayer(bankName, amount);
		economy.depositPlayer(receivingPlayer, amount);
		Common.tellNoPrefix(sendingPlayer, successPrefix + "You have sent &l" + amount + "kr &7to " + receivingPlayer);
	}
}
