package org.ancastal.bankplugin.command.BankCommands;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.bankplugin.BankPlugin;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.Collections;
import java.util.List;

public final class BankOwnerCommand extends SimpleSubCommand {
	public BankOwnerCommand(SimpleCommandGroup parent) {
		super(parent, "owner");
		setMinArguments(1);
		setUsage("<new_owner>");
		setDescription("Changes the ownership of the certificate");
	}

	@Override
	protected void onCommand() {

		Economy economy = BankPlugin.getEconomy();
		String param = args[0];
		ItemStack item = getPlayer().getItemInHand();
		getPlayer().getInventory().remove(item);
		PlayerUtil.addItemsOrDrop(getPlayer(), CompMetadata.setMetadata(item, "playerName", param));
	}

	@Override
	protected List<String> tabComplete() {
		return Collections.singletonList("<owner>");
	}
}
