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

	public final static String CUSTOM_BANK_STRING = "_" + BankPlugin.getNamed();
	protected final static String WARN_PREFIX = "&8&l[&4&l\u2715&8&l]&c ";
	protected final static String SUCCESS_PREFIX = "&8&l[&2&l\u2714&8&l]&7 ";

	private static final String lore = "\n"
			+ "This is a certificate of"
			+ "\nownership of a bank."
			+ "\n"
			+ "\n&f&lCERTIFICATE";


	private boolean createBank(String bankName) {
		Economy economy = BankPlugin.getEconomy();
		if (economy.hasAccount(bankName + "_" + BankPlugin.getInstance())) {
			return false;
		}
		economy.createPlayerAccount(bankName + CUSTOM_BANK_STRING);
		System.out.println(bankName + "_" + BankPlugin.getInstance());
		return true;
	}


	public ItemStack getItem(OfflinePlayer player, String bankName, Player sender) {
		if (!createBank(bankName)) {
			Common.tellNoPrefix(sender, WARN_PREFIX + "This bank account already exists.");
			return null;
		}
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
			Common.tellNoPrefix(player, WARN_PREFIX + "You need to hold your Bank Certificate");
			return false;
		}
		if (CompMetadata.getMetadata(item, "playerName") == null) {
			Common.tellNoPrefix(player, WARN_PREFIX + "Your Bank Certificate is not valid.");
			return false;
		}
		if (!CompMetadata.getMetadata(item, "playerName").equals(player.getName())) {
			Common.tellNoPrefix(player, WARN_PREFIX + "This bank certificate belongs to " + CompMetadata.getMetadata(item, "playerName"));
			return false;
		}
		return true;
	}


	private static String getLore() {
		return lore;
	}

	public static void clearAccount(String bankName) {

		Economy economy = BankPlugin.getEconomy();
		Double bankBalance = economy.getBalance(bankName + CUSTOM_BANK_STRING);
		economy.withdrawPlayer(bankName + CUSTOM_BANK_STRING, bankBalance);

	}

	public static void withdrawFromBank(String bankName, Player receivingPlayer, Double amount) {
		Economy economy = BankPlugin.getEconomy();
		if (economy.getBalance(bankName + CUSTOM_BANK_STRING) < amount) {
			Common.tellNoPrefix(receivingPlayer, WARN_PREFIX + "The bank does not have enough money.");
			return;
		}
		if (amount == 0) {
			Common.tellNoPrefix(receivingPlayer, WARN_PREFIX + "You cannot withdraw 0.0 Krunas.");
			return;
		}
		economy.withdrawPlayer(bankName + CUSTOM_BANK_STRING, amount);
		economy.depositPlayer(receivingPlayer, amount);

		Common.tellNoPrefix(receivingPlayer, SUCCESS_PREFIX + "You have withdrawn &l" + amount + "kr &7from the bank!");

	}


	public static void depositToBank(String bankName, Player sendingPlayer, Double amount) {
		Economy economy = BankPlugin.getEconomy();
		if (economy.getBalance(sendingPlayer) <= amount) {
			Common.tellNoPrefix(sendingPlayer, WARN_PREFIX + "You do not have enough money.");
			return;
		}
		if (amount == 0) {
			Common.tellNoPrefix(sendingPlayer, WARN_PREFIX + "You cannot deposit 0.0 Krunas.");
			return;
		}
		economy.withdrawPlayer(sendingPlayer, amount);
		economy.depositPlayer(bankName + CUSTOM_BANK_STRING, amount);

		Common.tellNoPrefix(sendingPlayer, SUCCESS_PREFIX + "You have deposited &l" + amount + "kr &7into the bank!");
	}

	public static void transferToPlayer(String bankName, Player sendingPlayer, Player receivingPlayer, Double amount) {
		Economy economy = BankPlugin.getEconomy();
		if (economy.getBalance(bankName + CUSTOM_BANK_STRING) < amount) {
			Common.tellNoPrefix(receivingPlayer, WARN_PREFIX + "The bank does not have enough money.");
			return;
		}
		if (amount == 0) {
			Common.tellNoPrefix(sendingPlayer, WARN_PREFIX + "You cannot transfer 0.0 Krunas.");
			return;
		}
		economy.withdrawPlayer(bankName + CUSTOM_BANK_STRING, amount);
		economy.depositPlayer(receivingPlayer, amount);
		Common.tellNoPrefix(sendingPlayer, SUCCESS_PREFIX + "You have sent &l" + amount + "kr &7to " + receivingPlayer);
	}
}
