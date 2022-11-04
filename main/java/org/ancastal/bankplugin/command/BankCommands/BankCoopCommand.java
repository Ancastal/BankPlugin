package org.ancastal.bankplugin.command.BankCommands;

import org.ancastal.bankplugin.command.BankCommandGroup;
import org.ancastal.bankplugin.model.BankCertificate;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMetadata;

public class BankCoopCommand extends SimpleSubCommand {
	public BankCoopCommand(BankCommandGroup parent) {
		super(parent, "coop");
		setUsage("<player>");
		setDescription("Gives <player> access to your bank");
		setMinArguments(1);
	}

	@Override
	protected void onCommand() {
		checkConsole();

		String param = args[0];
		ItemStack item = getPlayer().getItemInHand();

		if (!BankCertificate.checkCertificate(getPlayer(), item)) return;

		String bankName = Common.stripColors(item.getItemMeta().getDisplayName());
		item = ItemCreator.of(item).lore("\n" + "Access to: " + param).make();
		item = CompMetadata.setMetadata(item, "playerName", param);
		getPlayer().getInventory().addItem(item);
	}
}
