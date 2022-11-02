package org.ancastal.bankplugin.model;


import org.ancastal.bankplugin.BankPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;


@AutoRegister
public final class BankListener implements Listener {

	public static BankListener getInstance() {
		return instance;
	}

	private static final BankListener instance = new BankListener();

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {

		String title = ChatColor.stripColor(e.getView().getTitle());
		Player player = (Player) e.getView().getPlayer();

		if (title.equals("Transfer money to...")) {
			e.setCancelled(true);

			if (e.getCurrentItem().getItemMeta() == null)
				return;

			Player targetPlayerName = Bukkit.getPlayer(Common.stripColors(e.getCurrentItem().getItemMeta().getDisplayName()));
			player.closeInventory();

			Conversation conversation = new Conversation(BankPlugin.getInstance(), player, new BankPrompt(targetPlayerName));
			player.beginConversation(conversation);

		}

	}


	/*
	@EventHandler
	public void onInventoryClosedEvent(InventoryCloseEvent e) {

		if (CompMetadata.hasTempMetadata(e.getPlayer(), "DepositMenu_" + BankPlugin.getInstance())) {

			CompMetadata.removeTempMetadata(e.getPlayer(), "DepositMenu_" + BankPlugin.getInstance());

			DepositMenu.setQuantity(1000);


		}

		if (CompMetadata.hasTempMetadata(e.getPlayer(), "WithdrawMenu_" + BankPlugin.getInstance())) {

			CompMetadata.removeTempMetadata(e.getPlayer(), "WithdrawMenu_" + BankPlugin.getInstance());
			WithdrawMenu.setQuantity(1000);
			//Player player = (Player) e.getPlayer();
			//Menu menu = new WithdrawMenu(player);
			//menu.displayTo(player);

		}

	}

	*/

}


