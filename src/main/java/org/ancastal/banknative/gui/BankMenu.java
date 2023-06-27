package org.ancastal.banknative.gui;


import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.models.Bank;
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


public class BankMenu extends Menu {
	@Position(10)
	private final Button depositButton;
	@Position(11)
	private final Button withdrawButton;
	@Position(13)
	private final Button balanceButton;
	@Position(14)
	private Button ATMS_Button = Button.DummyButton.makeEmpty();
	@Position(15)
	private final Button transferButton;

	@Position(16)
	private Button accountsButton = Button.DummyButton.makeEmpty();

	//@Position(9)
	//private Button BackButton = Button.DummyButton.makeEmpty();

	private final double balance;


	/**
	 * @param player   Player to whom the GUI is displayed.
	 * @param bankName Name of the bank owned by the player.
	 */
	public BankMenu(Player player, final String bankName, Database database) throws SQLException {
		if (bankName == null || bankName.equals("")) player.closeInventory();
		Bank bank = database.getBankByName(bankName);
		if (bank == null) player.closeInventory();
		assert bank != null;
		if (!bank.getPlayerUUID().equals(player.getUniqueId().toString())) player.closeInventory();

		setTitle("Central Bank Administration");

		balance = database.getBankByName(bankName).getBalance();
		setSize(27);
		setViewer(player);

		final Double finalBalance = database.getBankByName(bankName).getBalance();
		this.depositButton = new ButtonMenu(new DepositMenu(player, bankName, database, finalBalance),
				ItemCreator.of(CompMaterial.PLAYER_HEAD, "&bDeposit Money", "\nDeposit money into \nyour bank account\n")
						.skullUrl("https://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f")
						.make()
		);

		this.withdrawButton = new ButtonMenu(new WithdrawMenu(player, bankName, database, finalBalance),
				ItemCreator.of(CompMaterial.PLAYER_HEAD, "&bWithdraw Money", "\nWithdraw money from \nyour bank account\n")
						.skullUrl("https://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46")
						.make()
		);

		this.balanceButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				tellInfo(String.format("Balance: %,.2f %s", balance, Settings.getCurrency()));
				restartMenu();

			}


			@Override
			public ItemStack getItem() {
				try {
					return ItemCreator.of(CompMaterial.PLAYER_HEAD, "&bMy Bank",
							String.format(Locale.US, "\nBank Name: &f %s\nBalance: &a%,.0f &7%s\n", bankName, database.getBankByName(bankName).getBalance(), Settings.getCurrency())
					).skullUrl("https://textures.minecraft.net/texture/44c00164238d8ad99afad790c3050ff9eff410e2d4a3fe85600389dcb818a849").glow(true).make();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

		};


		this.transferButton = Button.makeSimple(ItemCreator.of(CompMaterial.PAPER, "&bTransfer Money", "\nTransfer money to a\nplayer or a bank.\n"), action -> {

			Inventory inventory = Bukkit.createInventory(player, 18, "Transfer money to...");
			player.openInventory(inventory);
			for (Player p : Bukkit.getOnlinePlayers()) {

				inventory.addItem(ItemCreator.of(CompMaterial.PLAYER_HEAD, p.getName(), "\nTransfer money to \n" + p.getName()).skullOwner(p.getName()).make());

			}

		});


		this.accountsButton = new ButtonMenu(new AccountSelection(player, bankName, database), ItemCreator.of(
						CompMaterial.PLAYER_HEAD, "&bBank Accounts", "\nManage bank accounts\n")
				.skullUrl("https://textures.minecraft.net/texture/d1f222b98538d4ea5dc48891a98138dc191b807ef281da749b781039f1209ca4")
				.make()
		);

		this.ATMS_Button = new ButtonMenu(new ATMs_Menu(player, bankName), ItemCreator.of(
						CompMaterial.PLAYER_HEAD, "&bBuy more ATMs", "\nIncrease your ATM limit\n")
				.skullUrl("https://textures.minecraft.net/texture/7f6f8e81330def176d2d52a59bf6af7b27d06acb75749fb1e943830d0162c945")
				.make()
		);

	}


	@Override
	public ItemStack getItemAt(int slot) {

		ItemStack filler = new ItemStack(ItemCreator.of(CompMaterial.BLACK_STAINED_GLASS_PANE, " ").make());

		return filler;

	}

}
