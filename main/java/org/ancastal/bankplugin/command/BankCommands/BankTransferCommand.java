package org.ancastal.bankplugin.command.BankCommands;

import org.ancastal.bankplugin.model.BankCertificate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public final class BankTransferCommand extends SimpleSubCommand {
	public BankTransferCommand(SimpleCommandGroup parent) {
		super(parent, "transfer");
		setMinArguments(2);
		setDescription("Transfer money from bank to another player");
		setUsage("<player> <amount>");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		ItemStack item = getPlayer().getItemInHand();
		Player receivingPlayer = Bukkit.getPlayer(args[0]);

		if (!BankCertificate.checkCertificate(getPlayer(), item)) return;

		String bankName = Common.stripColors(item.getItemMeta().getDisplayName());
		BankCertificate.transferToPlayer(bankName, getPlayer(), receivingPlayer, Double.parseDouble(args[1]));
	}


}
