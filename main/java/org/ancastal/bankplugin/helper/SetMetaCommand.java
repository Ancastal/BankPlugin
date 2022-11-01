package org.ancastal.bankplugin.helper;

import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.remain.CompMetadata;

@AutoRegister
public final class SetMetaCommand extends SimpleCommand {

	public SetMetaCommand() {
		super("setmetadata");
	}

	@Override
	protected void onCommand() {
		ItemStack item = getPlayer().getItemInHand();
		CompMetadata.setMetadata(item, "key", "value");
	}
}
