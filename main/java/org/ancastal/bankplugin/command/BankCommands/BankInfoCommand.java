package org.ancastal.bankplugin.command.BankCommands;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.bankplugin.BankPlugin;
import org.ancastal.bankplugin.model.BankCertificate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.CompMetadata;

public final class BankInfoCommand extends SimpleSubCommand {
	public BankInfoCommand(SimpleCommandGroup parent) {
		super(parent, "info");
		setUsage("");
		setDescription("Gets information about the certificate in main hand");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Economy economy = BankPlugin.getEconomy();
		ItemStack item = getPlayer().getItemInHand();

		if (item.getType() != Material.PAPER) {
			tellError("You need to hold your Bank Certificate");
			return;
		}
		if (CompMetadata.getMetadata(item, "playerName") == null) {
			tellError("This Bank Certificate is not valid.");
		}

		tellSuccess("This is a valid Bank Certificate.");
		tellInfo("Owner: &l" + CompMetadata.getMetadata(item, "playerName"));
		tellInfo("Balance: &l" + economy.getBalance(Common.stripColors(item.getItemMeta().getDisplayName()) + BankCertificate.CUSTOM_BANK_STRING));
		tellInfo("Bank Name: &l" + Common.stripColors(item.getItemMeta().getDisplayName()) + BankCertificate.CUSTOM_BANK_STRING);

	}
}
