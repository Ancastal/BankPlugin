package org.ancastal.bankplugin.model;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;

public class BankPrompt extends NumericPrompt {

	private final Player targetPlayerName;

	BankPrompt(Player targetPlayerName) {
		this.targetPlayerName = targetPlayerName;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
		Player player = (Player) context.getForWhom();
		ItemStack item = player.getItemInHand();

		String bankName = Common.stripColors(item.getItemMeta().getDisplayName());
		BankCertificate.transferToPlayer(bankName, player, targetPlayerName, input.doubleValue());
		return null;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return ChatColor.YELLOW + "How much would you like to transfer?";

	}

	@Override
	protected String getInputNotNumericText(ConversationContext context, String invalidInput) {
		return "Please, enter a number. How much would you like to transfer?";
	}
}
