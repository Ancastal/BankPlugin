package org.ancastal.bankplugin.command.BankCommands;

import org.ancastal.bankplugin.model.BankCertificate;
import org.ancastal.bankplugin.model.BankMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class BankMenuCommand extends SimpleSubCommand {
	public BankMenuCommand(SimpleCommandGroup parent) {
		super(parent, "menu");
	}

	@Override
	protected void onCommand() {
		Player player = (Player) sender;
		ItemStack item = getPlayer().getItemInHand();
		if (!BankCertificate.checkCertificate(getPlayer(), item)) return;

		new BankMenu(player).displayTo(player);
	}
}
