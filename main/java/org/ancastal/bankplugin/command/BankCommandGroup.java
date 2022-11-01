package org.ancastal.bankplugin.command;

import org.ancastal.bankplugin.command.BankCommands.BankCreateCommand;
import org.ancastal.bankplugin.command.BankCommands.BankInfoCommand;
import org.ancastal.bankplugin.command.BankCommands.BankMenuCommand;
import org.ancastal.bankplugin.command.BankCommands.BankWithdrawCommand;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;

@AutoRegister
public final class BankCommandGroup extends SimpleCommandGroup {

	public BankCommandGroup() {
		super("bank");
	}

	@Override
	protected void registerSubcommands() {
		registerSubcommand(new BankCreateCommand(this));
		registerSubcommand(new BankWithdrawCommand(this));
		registerSubcommand(new BankInfoCommand(this));
		registerSubcommand(new BankMenuCommand(this));
	}
}
