package org.ancastal.bankplugin.command.BankCommands;

import org.ancastal.bankplugin.model.BankCertificate;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.Collections;
import java.util.List;

public final class BankDepositCommand extends SimpleSubCommand {
	public BankDepositCommand(SimpleCommandGroup parent) {
		super(parent, "deposit");
		setMinArguments(1);
		setDescription("Deposit money into the bank.");
		setUsage("<amount>");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		ItemStack item = getPlayer().getItemInHand();

		if (!BankCertificate.checkCertificate(getPlayer(), item)) return;

		String bankName = Common.stripColors(item.getItemMeta().getDisplayName());
		BankCertificate.depositToBank(bankName, getPlayer(), Double.parseDouble(args[0]));

	}

	@Override
	protected List<String> tabComplete() {
		if (args.length == 1) {
			return Collections.singletonList("<amount>");
		}
		return NO_COMPLETE;
	}
}
