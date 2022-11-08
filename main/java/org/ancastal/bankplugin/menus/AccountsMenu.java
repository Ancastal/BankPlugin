package org.ancastal.bankplugin.menus;

import org.ancastal.bankplugin.settings.PlayerData;
import org.ancastal.bankplugin.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountsMenu extends Menu {

	private final Map<Integer, Button> buttons = new HashMap<>();

	public AccountsMenu(Player player) {
		setTitle("Select players... (Foundation)");

		int i = 0;
		for (String bankAccount : PlayerData.from(player).getBankList()) {

			buttons.put(i++, new Button() {
						@Override
						public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
							new SingleBankMenu(player, removeCustomBankString(bankAccount)).displayTo(player);
						}

						@Override
						public ItemStack getItem() {
							return ItemCreator.of(CompMaterial.PLAYER_HEAD, removeCustomBankString(bankAccount), "Click to manage\nthis bank account.").make();
						}
					}
			);
		}

	}

	@Override
	protected List<Button> getButtonsToAutoRegister() {
		return new ArrayList<>(buttons.values());
	}

	private static List<String> compilePlayers(Player player) {
		final List<String> list = new ArrayList<>();
		list.addAll(PlayerData.from(player).getBankList());
		return list;
	}

	@Override
	public ItemStack getItemAt(int slot) {
		if (buttons.containsKey(slot))
			return buttons.get(slot).getItem();

		return null;
	}

	private String removeCustomBankString(String s) {
		for (String string : Settings.getCustomBankString()) s = s.replaceAll(string, "");
		return s;
	}

}
