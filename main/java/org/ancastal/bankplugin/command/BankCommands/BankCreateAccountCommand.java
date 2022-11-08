package org.ancastal.bankplugin.command.BankCommands;

import org.ancastal.bankplugin.model.BankCertificate;
import org.ancastal.bankplugin.settings.PlayerData;
import org.ancastal.bankplugin.settings.Settings;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public final class BankCreateAccountCommand extends SimpleSubCommand {
	public BankCreateAccountCommand(SimpleCommandGroup parent) {
		super(parent, "account");
		setMinArguments(1);
		setUsage("<accountName>");
		setDescription("Creates a bank account with no certificate");
	}

	@Override
	protected void onCommand() {
		String param = args[0];

		ItemStack item = getPlayer().getItemInHand();
		if (!BankCertificate.checkCertificate(getPlayer(), item)) return;

		String bankName = Common.stripColors(item.getItemMeta().getDisplayName());

		//Might need to add a prefix for main Bank to the Vault sub-account
		String LAST_CUSTOM_BANK_STRING = Settings.getCustomBankString().get(
				Settings.CUSTOM_BANK_STRING.size() - 1
		);
		if (!BankCertificate.createBankAccount(param)) tellError("Account by name " + param + " already exists.");
		PlayerData.from(getPlayer()).addSaveBankList(param + LAST_CUSTOM_BANK_STRING);
		tellSuccess("Account by name " + param + " has been created");

	}
}
