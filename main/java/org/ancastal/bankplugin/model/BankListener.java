package org.ancastal.bankplugin.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BankListener implements Listener {

	@Getter
	private static final BankListener instance = new BankListener();


	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {

		String title = ChatColor.stripColor(e.getView().getTitle());
		Player player = (Player) e.getView().getPlayer();

		if (title.equals("Transfer money to...")) {
			e.setCancelled(true);
			if (e.getCurrentItem().getItemMeta() == null) return;
			Player targetPlayerName = Bukkit.getPlayer(Common.stripColors(e.getCurrentItem().getItemMeta().getDisplayName()));
			player.closeInventory();
			Conversation conversation = new Conversation(BankPlugin.getInstance(), player, new BankPrompt(targetPlayerName));
			player.beginConversation(conversation);
		}

	}

}




