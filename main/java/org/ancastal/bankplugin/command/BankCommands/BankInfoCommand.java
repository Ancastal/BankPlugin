package org.ancastal.bankplugin.command.BankCommands;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.bankplugin.BankPlugin;
import org.ancastal.bankplugin.model.BankCertificate;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.CompMetadata;

public final class BankInfoCommand extends SimpleSubCommand {
	public BankInfoCommand(SimpleCommandGroup parent) {
		super(parent, "iteminfo");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Economy economy = BankPlugin.getEconomy();
		ItemStack item = getPlayer().getItemInHand();

		if (!BankCertificate.checkCertificate(getPlayer(), item)) return;


		tellSuccess("This is a valid Bank Certificate.");
		tellInfo("Owner: &l" + CompMetadata.getMetadata(item, "playerName"));
		tellInfo("Balance: &l" + economy.getBalance(Common.stripColors(item.getItemMeta().getDisplayName())));

	}
}
