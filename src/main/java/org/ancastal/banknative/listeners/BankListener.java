package org.ancastal.banknative.listeners;

import org.ancastal.banknative.BankNative;
import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.models.BankPrompt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.mineacademy.fo.Common;

public class BankListener implements Listener {
	private final Database database;

	public BankListener(Database database) {
		this.database = database;
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {

		String title = ChatColor.stripColor(e.getView().getTitle());
		Player player = (Player) e.getView().getPlayer();
		if (title.equals("Transfer money to...")) {
			e.setCancelled(true);

			Player targetPlayerName = Bukkit.getPlayer(Common.stripColors(e.getCurrentItem().getItemMeta().getDisplayName()));
			player.getOpenInventory().close();

			Conversation conversation = new Conversation(BankNative.getInstance(), player, new BankPrompt(targetPlayerName, database));
			player.beginConversation(conversation);
		}
	}

}
