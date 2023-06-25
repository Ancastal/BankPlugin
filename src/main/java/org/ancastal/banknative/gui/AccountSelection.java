package org.ancastal.banknative.gui;

import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.models.Account;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a GUI to manage deposit accounts
 */

public class AccountSelection extends Menu {
	@Position(22)
	private final Button BackButton;
	private final Map<Integer, Button> buttons = new HashMap<>();
	private final Database database;

	public AccountSelection(Player player, String bankName, Database database) throws SQLException {
		this.database = database;
		setTitle("Select players...");
		int i = 0;
		this.BackButton = Button.makeSimple(ItemCreator.of(
								CompMaterial.PLAYER_HEAD, "&bBack", "\nGo back to your main bank.")
						.skullUrl("https://textures.minecraft.net/texture/4c301a17c955807d89f9c72a19207d1393b8c58c4e6e420f714f696a87fdd")
				, action -> {
					try {
						new BankMenu(player, bankName, this.database).displayTo(player);
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				}
		);

		List<Account> accountList = database.getAccountsList(bankName);
		if (accountList == null) return;
		for (Account account : accountList) {

			buttons.put(i++, new Button() {
						@Override
						public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
							try {
								new AccountMenu(player, account, database).displayTo(player);
							} catch (SQLException e) {
								throw new RuntimeException(e);
							}
						}

						@Override
						public ItemStack getItem() {
							return ItemCreator.of(CompMaterial.PLAYER_HEAD, account.getAccountHolder(),
									String.format("\nAccount Name: &f%s\nBalance: &b%,.2f\n\n&eClick to manage this account", account.getAccountHolder(), account.getBalance())
							).skullOwner(account.getAccountHolder()).make();
						}
					}
			);

		}


	}

	@Override
	protected List<Button> getButtonsToAutoRegister() {
		return new ArrayList<>(buttons.values());
	}

	/*
	private static List<String> compilePlayers(Player player) {
		final List<String> list = new ArrayList<>();
		list.addAll(PlayerData.from(player).getBankList());
		return list;
	}
	 */

	@Override
	public ItemStack getItemAt(int slot) {
		if (buttons.containsKey(slot))
			return buttons.get(slot).getItem();
		if (slot >= 18 && slot <= 26)
			return ItemCreator.of(CompMaterial.BLACK_STAINED_GLASS_PANE, " ").make();
		return null;
	}

	@Override
	protected void onMenuClose(Player player, Inventory inventory) {

		//new SingleBankMenu(player, PlayerData.from(player).getBankName(), false).displayTo(player);
	}


}
