package org.ancastal.bankplugin.command.BankCommands;

import lombok.SneakyThrows;
import net.milkbowl.vault.economy.Economy;
import org.ancastal.bankplugin.BankPlugin;
import org.ancastal.bankplugin.model.BankCertificate;
import org.ancastal.bankplugin.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.nbt.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


public final class BankCreateCommand extends SimpleSubCommand {

	private NBTFile nbtFile;
	private NBTCompoundList nbtInventory;
	private ItemStack[] content;

	BankCertificate bankCertificate = new BankCertificate();

	public BankCreateCommand(SimpleCommandGroup parent) {
		super(parent, "create");
		setMinArguments(2);
		setUsage("<player> <bankName>");
		setDescription("Creates a bank account and sends the certificate to the player.");
	}

	@Override
	@SneakyThrows
	@SuppressWarnings("deprecation")
	protected void onCommand() {
		checkConsole();
		final String playerName = args[0];
		final String bankName = args[1];

		Economy economy = BankPlugin.getEconomy();
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

		ItemStack certificate = bankCertificate.getItem(offlinePlayer, bankName, getPlayer());

		if (certificate == null) {
			return;
		}

		certificate = CompMetadata.setMetadata(CompMetadata.setMetadata(certificate, "playerName", playerName), "bankName", bankName + Settings.CUSTOM_BANK_STRING.get(
						Settings.CUSTOM_BANK_STRING.size() - 1
				)
		);


		if (Bukkit.getPlayer(playerName) != null) {
			PlayerUtil.addItemsOrDrop(Bukkit.getPlayer(playerName), certificate);
			tellSuccess("Bank certificate sent to " + playerName);
		} else if (offlineSend(CompMetadata.setMetadata(certificate, "playerName", playerName))) {
			tellSuccess("Bank certificate shipped offline to " + playerName);
		} else {
			PlayerUtil.addItemsOrDrop(getPlayer(), certificate);
			tellInfo("Item could not be sent online or offline to " + playerName);
		}

	}

	private boolean offlineSend(ItemStack item) throws IOException {
		final OfflinePlayer param = Bukkit.getOfflinePlayer(args[0]);

		this.nbtFile = new NBTFile(new File(Bukkit.getWorldContainer(), "world/playerdata/" + param.getUniqueId() + ".dat"));

		this.nbtInventory = this.nbtFile.getCompoundList("Inventory");
		this.content = this.readData(Bukkit.getOfflinePlayer(param.getUniqueId()));

		for (int slot = 0; slot < content.length; slot++) {
			final ItemStack offlineItem = content[slot];

			if (offlineItem == null) {
				final NBTContainer container = NBTItem.convertItemtoNBT(item);
				container.setByte("Slot", (byte) slot);

				this.nbtInventory.addCompound(container); // arraylist#add
				this.nbtFile.save();
				return true;
			}
		}
		return false;
	}

	private ItemStack[] readData(final OfflinePlayer player) {
		return getItemStacks(this.nbtInventory);
	}

	@NotNull
	static ItemStack[] getItemStacks(NBTCompoundList nbtInventory) {
		final ItemStack[] content = new ItemStack[PlayerUtil.USABLE_PLAYER_INV_SIZE];

		for (final NBTListCompound item : nbtInventory) {
			final int slot = item.getByte("Slot");

			if (slot >= 0 && slot <= PlayerUtil.USABLE_PLAYER_INV_SIZE)
				content[slot] = NBTItem.convertNBTtoItem(item);
		}

		return content;
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return this.completeLastWordPlayerNames();
		else if (args.length == 2)
			return Collections.singletonList("<bankName>");

		return NO_COMPLETE;
	}
}
