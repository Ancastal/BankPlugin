package org.ancastal.bankplugin.helper;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.mineacademy.fo.conversation.SimplePrompt;

public class ConfirmPrompt extends SimplePrompt {

	@Override
	protected String getPrompt(ConversationContext context) {
		return "Enter 'deposit', 'withdraw', 'clear', 'exit'";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) {
		return null;
	}
}
