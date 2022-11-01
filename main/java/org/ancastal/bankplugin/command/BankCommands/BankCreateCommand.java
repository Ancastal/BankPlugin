package org.ancastal.bankplugin.command.BankCommands;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.bankplugin.BankPlugin;
import org.ancastal.bankplugin.model.BankCertificate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.CompMetadata;


public final class BankCreateCommand extends SimpleSubCommand {

	BankCertificate bankCertificate = new BankCertificate();

	public BankCreateCommand(SimpleCommandGroup parent) {
		super(parent, "create");
		setMinArguments(2);
		setUsage("<player> <amount>");
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void onCommand() {
		checkConsole();
		Economy economy = BankPlugin.getEconomy();
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

		ItemStack certificate = bankCertificate.getItem(offlinePlayer, args[1]);

		PlayerUtil.addItemsOrDrop(getPlayer(), CompMetadata.setMetadata(certificate, "playerName", args[0]));

	}
}
