package org.ancastal.bankplugin.command.BankCommands;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.bankplugin.BankPlugin;
import org.ancastal.bankplugin.model.BankCertificate;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public final class BankWithdrawCommand extends SimpleSubCommand {
	public BankWithdrawCommand(SimpleCommandGroup parent) {
		super(parent, "withdraw");
		setMinArguments(1);
		setUsage("<amount>");
	}


	@Override
	protected void onCommand() {
		checkConsole();

		Economy economy = BankPlugin.getEconomy();
		ItemStack item = getPlayer().getItemInHand();

		if (!BankCertificate.checkCertificate(getPlayer(), item)) return;

		String bankName = Common.stripColors(item.getItemMeta().getDisplayName());
		BankCertificate.withdrawFromBank(bankName, getPlayer(), Double.parseDouble(args[0]));

	}


}
