package org.ancastal.bankplugin.command.BankCommands;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.bankplugin.BankPlugin;
import org.ancastal.bankplugin.model.BankCertificate;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class BankBalanceCommand extends SimpleSubCommand {

	public BankBalanceCommand(SimpleCommandGroup parent) {
		super(parent, "balance|bal");
		setUsage("");
		setDescription("Gets your bank balance");
	}

	@Override
	protected void onCommand() {
		Economy economy = BankPlugin.getEconomy();

		ItemStack item = getPlayer().getItemInHand();

		if (!BankCertificate.checkCertificate(getPlayer(), item)) return;
		tellInfo("Balance: &l" + economy.getBalance(Common.stripColors(item.getItemMeta().getDisplayName()) + BankCertificate.CUSTOM_BANK_STRING));
	}
}
