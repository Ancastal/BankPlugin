package org.ancastal.bankplugin.command;

import org.ancastal.bankplugin.command.BankCommands.*;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;

@AutoRegister
public final class BankCommandGroup extends SimpleCommandGroup {


	@Override
	protected String getCredits() {
		return "&7Contact &fadministrators &7for more information.";
	}

	public BankCommandGroup() {
		super("bank");
	}

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new BankCreateCommand(this));
		registerSubcommand(new BankWithdrawCommand(this));
		registerSubcommand(new BankInfoCommand(this));
		registerSubcommand(new BankMenuCommand(this));
		registerSubcommand(new BankClearCommand(this));
		registerSubcommand(new BankDepositCommand(this));
		registerSubcommand(new BankTransferCommand(this));
		registerSubcommand(new BankBalanceCommand(this));
		registerSubcommand(new BankOwnerCommand(this));
		registerSubcommand(new BankCoopCommand(this));
		registerSubcommand(new BankCreateAccountCommand(this));

	}
}
