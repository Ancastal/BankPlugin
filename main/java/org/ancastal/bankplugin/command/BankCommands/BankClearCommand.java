package org.ancastal.bankplugin.command.BankCommands;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.bankplugin.BankPlugin;
import org.ancastal.bankplugin.model.BankCertificate;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.Collections;
import java.util.List;

public class BankClearCommand extends SimpleSubCommand {

	public BankClearCommand(SimpleCommandGroup parent) {
		super(parent, "clear");
		setUsage("<bankName> [playerName]");
		setMinArguments(1);
	}

	@Override
	protected void onCommand() {
		String bankName = args[0];

		BankCertificate.clearAccount(bankName);
		Economy economy = BankPlugin.getEconomy();

	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return Collections.singletonList("<bankName>");
		else if (args.length == 2)
			return this.completeLastWordPlayerNames();

		return NO_COMPLETE;
	}
}
