package org.ancastal.banknative.gui;


import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.models.Account;
import org.ancastal.banknative.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.sql.SQLException;
import java.util.Locale;


public class AccountMenu extends Menu {
	@Position(10)
	private final Button depositButton;
	@Position(11)
	private final Button withdrawButton;
	@Position(13)
	private final Button balanceButton;
	@Position(15)
	private final Button transferButton;
	@Position(9)
	private Button BackButton = Button.DummyButton.makeEmpty();

	private final Database database;
	String currency = Settings.getCurrency();

	public AccountMenu(Player viewer, Account account, Database database) throws SQLException {
		this.database = database;
		String uuid = account.getUuid().toString();
		Double balance = account.getBalance();
		String bankName = account.getBankName();

		setTitle("Deposit account: " + account.getAccountHolder());

		if (bankName == null || bankName == "") viewer.closeInventory();

		setSize(27);

		this.depositButton = new ButtonMenu(new AccountDepositMenu(viewer, account, bankName, database),
				ItemCreator.of(CompMaterial.PLAYER_HEAD, "&bDeposit Money", "\nDeposit money into \nyour bank account\n")
						.skullUrl("https://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f")
						.make()
		);

		this.withdrawButton = new ButtonMenu(new AccountWithdrawMenu(viewer, account, bankName, database),
				ItemCreator.of(CompMaterial.PLAYER_HEAD, "&bWithdraw Money", "\nWithdraw money from \nyour bank account\n")
						.skullUrl("https://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46")
						.make()
		);

		this.balanceButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				try {
					double newBalance = database.getAccountHolder(account.getUuid()).getBalance();
					tellInfo(String.format("Balance: %,.2f %s", newBalance, Settings.getCurrency()));
					restartMenu();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}

			}


			@Override
			public ItemStack getItem() {
				try {
					return ItemCreator.of(CompMaterial.PLAYER_HEAD, "&bMy Account",
							String.format(Locale.US, "\nBank Name: &f %s\nBalance: &a%,.0f &7%s\n", bankName, database.getAccountHolder(account.getUuid()).getBalance(), currency)
					).skullUrl("https://textures.minecraft.net/texture/44c00164238d8ad99afad790c3050ff9eff410e2d4a3fe85600389dcb818a849").glow(true).make();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

		};

		if (database.getBankByName(bankName).equals(database.getBankByOwner(viewer.getUniqueId().toString()))) {
			this.BackButton = new ButtonMenu(new BankMenu(viewer, bankName, database), ItemCreator.of(
							CompMaterial.PLAYER_HEAD, "&bBack", "\nGo back to your main bank.")
					.skullUrl("https://textures.minecraft.net/texture/4c301a17c955807d89f9c72a19207d1393b8c58c4e6e420f714f696a87fdd")
					.make()
			);
		} else {
			this.BackButton = Button.makeSimple(ItemCreator.of(CompMaterial.BLACK_STAINED_GLASS_PANE, " "), action -> {
			});
		}

		this.transferButton = Button.makeSimple(ItemCreator.of(CompMaterial.PAPER, "&bTransfer Money", "\nTransfer money to a\nplayer or a bank.\n"), action -> {
			Inventory inventory = Bukkit.createInventory(getViewer(), 18, "Transfer money to...");
			getViewer().openInventory(inventory);
			for (Player p : Bukkit.getOnlinePlayers()) {
				inventory.addItem(ItemCreator.of(CompMaterial.PLAYER_HEAD, p.getName(), "\nTransfer money to \n" + p.getName()).skullOwner(p.getName()).make());
			}
		});

	}


	@Override
	public ItemStack getItemAt(int slot) {

		ItemStack filler = new ItemStack(ItemCreator.of(CompMaterial.BLACK_STAINED_GLASS_PANE, " ").make());

		return filler;

	}

}

