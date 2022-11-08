package org.ancastal.bankplugin.command.BankCommands;

import org.ancastal.bankplugin.menus.SingleBankMenu;
import org.ancastal.bankplugin.model.BankCertificate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public final class BankMenuCommand extends SimpleSubCommand {
	public BankMenuCommand(SimpleCommandGroup parent) {
		super(parent, "menu");
		setDescription("Opens the Banking GUI");
	}

	@Override
	protected void onCommand() {
		checkConsole();
		Player player = (Player) sender;
		ItemStack item = getPlayer().getItemInHand();
		if (!BankCertificate.checkCertificate(getPlayer(), item)) return;
		String bankName = Common.stripColors(item.getItemMeta().getDisplayName());

		new SingleBankMenu(player, bankName).displayTo(player);
	}
}
