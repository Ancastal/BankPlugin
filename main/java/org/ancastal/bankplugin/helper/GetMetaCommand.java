package org.ancastal.bankplugin.helper;

import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.remain.CompMetadata;

@AutoRegister
public final class GetMetaCommand extends SimpleCommand {

	public GetMetaCommand() {
		super("getmetadata");
	}

	@Override
	protected void onCommand() {
		ItemStack item = getPlayer().getItemInHand();
		getPlayer().sendMessage("The playerName metadata in this item is: " + CompMetadata.getMetadata(item, "playerName"));
		getPlayer().sendMessage("The bankName metadata in this item is: " + CompMetadata.getMetadata(item, "bankName"));

	}
}
