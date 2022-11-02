package org.ancastal.bankplugin.helper;


import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.MenuClickLocation;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.nbt.*;

import java.io.File;
import java.util.List;

/**
 * A sample command to open a menu we create in the course.
 */
public final class InvEditCommand extends NMSCommand {

	public InvEditCommand() {
		super("invedit");

		setMinArguments(2);
	}

	/**
	 * @see org.mineacademy.fo.command.SimpleCommand#onCommand()
	 */
	@Override
	protected void onCommand() {
		checkConsole();

		final String param = args[0];
		final String name = args[1];

		final OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(name);
		final boolean isOnline = targetOfflinePlayer.isOnline();
		final Player targetPlayer = targetOfflinePlayer.getPlayer();

		if ("inv".equals(param)) {
			if (isOnline)
				getPlayer().openInventory(targetPlayer.getInventory());
			else
				OfflineInvMenu.showTo(getPlayer(), targetOfflinePlayer);

		} else if ("enderchest".equals(param)) {
			if (isOnline)
				getPlayer().openInventory(targetPlayer.getEnderChest());
			else
				returnTell("homework!");

		} else if ("armor".equals(param)) {
			if (isOnline)
				ArmorMenu.showTo(getPlayer(), targetPlayer);
			else
				returnTell("homework!");
		}

		// level 1 > beginners...
		// level 2 >
	}

	private static class OfflineInvMenu extends Menu {

		private final NBTFile nbtFile;
		private final NBTCompoundList nbtInventory;
		private final ItemStack[] content;

		@SneakyThrows
		private OfflineInvMenu(final OfflinePlayer target) {
			setSize(PlayerUtil.USABLE_PLAYER_INV_SIZE);
			setTitle("&4" + target.getName() + "'s offline inventory");

			this.nbtFile = new NBTFile(new File(Bukkit.getWorldContainer(), "world/playerdata/" + target.getUniqueId() + ".dat"));

			this.nbtInventory = this.nbtFile.getCompoundList("Inventory");
			this.content = this.readData(target);
		}

		private ItemStack[] readData(final OfflinePlayer player) {
			final ItemStack[] content = new ItemStack[PlayerUtil.USABLE_PLAYER_INV_SIZE];

			for (final NBTListCompound item : this.nbtInventory) {
				final int slot = item.getByte("Slot");

				if (slot >= 0 && slot <= PlayerUtil.USABLE_PLAYER_INV_SIZE)
					content[slot] = NBTItem.convertNBTtoItem(item);
			}

			return content;
		}

		@Override
		public ItemStack getItemAt(final int slot) {
			return this.content[slot];
		}

		@Override
		protected boolean isActionAllowed(final MenuClickLocation location, final int slot, final ItemStack clicked, final ItemStack cursor) {
			return true;
		}

		@Override
		@SneakyThrows
		protected void onMenuClose(final Player player, final Inventory inventory) {
			final ItemStack[] editedContent = inventory.getContents();

			this.nbtInventory.clear();

			for (int slot = 0; slot < editedContent.length; slot++) {
				final ItemStack item = editedContent[slot];

				if (item != null) {
					final NBTContainer container = NBTItem.convertItemtoNBT(item);
					container.setByte("Slot", (byte) slot);

					this.nbtInventory.addCompound(container); // arraylist#add
				}
			}

			this.nbtFile.save();
		}

		private static void showTo(final Player viewer, final OfflinePlayer target) {
			new OfflineInvMenu(target).displayTo(viewer);
		}
	}

	private static class ArmorMenu extends Menu {

		// ONE ROW - 9 slot
		// HELMET 0, CHESTPLATE 1, LEGGINGS 2, BOOTS 3, EMPTY SLOT 4, EMPTY SLOT 5, EMPTY 6, EMPTY 7, OFFHAND 8

		private final Player targetPlayer;

		private final static ItemStack EMPTY_SLOT_FILLER = ItemCreator
				.of(CompMaterial.GRAY_STAINED_GLASS_PANE, "")
				.build().make();

		private ArmorMenu(final Player targetPlayer) {
			setTitle(targetPlayer.getName() + "'s armor");
			setSize(9);
			//setSlotNumbersVisible();

			this.targetPlayer = targetPlayer;
		}

		@Override
		public ItemStack getItemAt(final int slot) {
			final PlayerInventory inv = this.targetPlayer.getInventory();

			if (slot == 0)
				return inv.getHelmet();

			else if (slot == 1)
				return inv.getChestplate();

			else if (slot == 2)
				return inv.getLeggings();

			else if (slot == 3)
				return inv.getBoots();

			else if (slot == this.getSize() - 1)
				return inv.getItemInHand();

			return NO_ITEM;
		}

		@Override
		protected boolean isActionAllowed(final MenuClickLocation location, final int slot, final ItemStack clicked, final ItemStack cursor) {

			return location != MenuClickLocation.MENU || (slot != 4 && slot != 5 && slot != 6 && slot != 7);
		}

		@Override
		protected void onMenuClose(final Player player, final Inventory inventory) {
			final PlayerInventory targetPlayerInv = this.targetPlayer.getInventory();

			targetPlayerInv.setItemInHand(inventory.getItem(this.getSize() - 1));

			targetPlayerInv.setHelmet(inventory.getItem(0));
			targetPlayerInv.setChestplate(inventory.getItem(1));
			targetPlayerInv.setLeggings(inventory.getItem(2));
			targetPlayerInv.setBoots(inventory.getItem(3));
		}

		private static void showTo(final Player viewer, final Player targetPlayer) {
			new ArmorMenu(targetPlayer).displayTo(viewer);
		}
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return this.completeLastWord("inv", "enderchest", "armor");

		else if (args.length == 2)
			return this.completeLastWordPlayerNames();

		return NO_COMPLETE;
	}
}