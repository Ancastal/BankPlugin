package org.ancastal.banknative.models;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.banknative.BankNative;
import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.settings.Settings;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class BankPrompt extends NumericPrompt {

	private final Player targetPlayerName;
	private final Database database;

	public BankPrompt(Player targetPlayerName, Database database) {
		this.targetPlayerName = targetPlayerName;
		this.database = database;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
		Player player = (Player) context.getForWhom();
		Economy economy = BankNative.getEconomy();
		Bank bank = null;
		try {
			bank = database.getBankByOwner(player.getUniqueId().toString());
			if (bank == null) return null;
			if (bank.getBalance() < input.doubleValue()) {
				context.getForWhom().sendRawMessage(ChatColor.RED + "You do not have enough money.");
				return Prompt.END_OF_CONVERSATION;
			}
			database.take(bank, input.doubleValue());
			economy.depositPlayer(targetPlayerName, input.doubleValue());
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "You have sent " + input.doubleValue() + " " + Settings.getCurrency() + " to " + targetPlayerName.getName());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}


		return END_OF_CONVERSATION;
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
