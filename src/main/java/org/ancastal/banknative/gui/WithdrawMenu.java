package org.ancastal.banknative.gui;


import net.milkbowl.vault.economy.Economy;
import org.ancastal.banknative.BankNative;
import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.models.Bank;
import org.ancastal.banknative.settings.Settings;
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
import java.util.Locale;


public class WithdrawMenu extends Menu {

	@Position(0)
	private final Button increase10kButton;
	@Position(1)
	private final Button increase1kButton;
	@Position(2)
	private final Button increase100Button;
	@Position(3)
	private final Button increase10Button;
	@Position(4)
	private final Button confirmButton;
	@Position(5)
	private final Button decrease10kButton;
	@Position(6)
	private final Button decrease1kButton;
	@Position(7)
	private final Button decrease100Button;
	@Position(8)
	private final Button decrease10Button;

	private int quantity = 0;
	private final Database database;
	private Double balance;

	public static String formattedDouble(double value) {
		return String.format(Locale.US, "%,.2f", value);
	}

	public WithdrawMenu(Player player, String bankName, Database database) {
		Economy economy = BankNative.getEconomy();
		setTitle("Set your withdraw...");
		setSize(9);
		String currency = Settings.getCurrency();
		this.database = database;
		this.confirmButton = new Button() {

			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				WithdrawMenu.this.restartMenu();
				try {
					Bank bank = database.getBankByName(bankName);
					balance = bank.getBalance();
					if (quantity > balance) {
						tellError("The bank does not have enough money.");
						return;
					}
					database.take(bank, quantity + 0.0);
					economy.depositPlayer(player, quantity + 0.0);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				quantity = 0;
				player.closeInventory();
			}


			@Override
			public ItemStack getItem() {
				return ItemCreator.of(CompMaterial.PAPER, "&bWithdraw: &7" + formattedDouble(quantity) + " " + currency, "\nConfirm your deposit.").glow(true).make();
			}
		};

		this.increase1kButton = Button.makeSimple(ItemCreator.of(CompMaterial.PLAYER_HEAD, "&2Increase by 1,000 " + currency, "\nIncrease the amount you\nwould like to deposit").skullUrl("https://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f"), action -> {
			if (quantity >= balance) {
				animateTitle("&4Not enough money in the bank");
				return;
			}
			quantity += 1000;
			animateTitle("Increased by 1,000 " + currency);
			restartMenu();

		});

		this.increase10kButton = Button.makeSimple(ItemCreator.of(CompMaterial.PLAYER_HEAD, "&2Increase by 10,000 " + currency, "\nIncrease the amount you\nwould like to deposit").skullUrl("https://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f"), action -> {
			if (quantity >= balance) {
				animateTitle("&4Not enough money in the bank");
				return;
			}
			quantity += 10000;
			animateTitle("Increased by 10,000 " + currency);
			restartMenu();

		});

		this.increase100Button = Button.makeSimple(ItemCreator.of(CompMaterial.PLAYER_HEAD, "&2Increase by 100 " + currency, "\nIncrease the amount you\nwould like to deposit").skullUrl("https://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f"), action -> {
			if (quantity >= balance) {
				animateTitle("&4Not enough money in the bank");
				return;
			}
			quantity += 100;
			animateTitle("Increased by 100 " + currency);
			restartMenu();

		});

		this.increase10Button = Button.makeSimple(ItemCreator.of(CompMaterial.PLAYER_HEAD, "&2Increase by 10 " + currency, "\nIncrease the amount you\nwould like to deposit").skullUrl("https://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f"), action -> {
			if (quantity >= balance) {
				animateTitle("&4Not enough money in the bank");
				return;
			}
			quantity += 10;
			animateTitle("Increased by 10 " + currency);
			restartMenu();

		});

		this.decrease1kButton = Button.makeSimple(ItemCreator.of(CompMaterial.PLAYER_HEAD, "&cDecrease by 1,000 " + currency, "\nDecrease the amount you\nwould like to deposit")
						.skullUrl("https://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46"),
				action -> {
					quantity = (quantity != 0) ? (quantity - 1000) : quantity;
					animateTitle((quantity != 0) ? "Decreased by 1,000 " + currency : "&4Deposit cannot be less than 0");
					restartMenu();

				});
		this.decrease10kButton = Button.makeSimple(ItemCreator.of(CompMaterial.PLAYER_HEAD, "&cDecrease by 10,000 " + currency, "\nDecrease the amount you\nwould like to deposit")
						.skullUrl("https://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46"),
				action -> {
					quantity = (quantity != 0) ? (quantity - 10000) : quantity;
					animateTitle((quantity != 0) ? "Decreased by 10,000 " + currency : "&4Deposit cannot be less than 0");
					restartMenu();

				});

		this.decrease100Button = Button.makeSimple(ItemCreator.of(CompMaterial.PLAYER_HEAD, "&cDecrease by 100 " + currency, "\nDecrease the amount you\nwould like to deposit")
						.skullUrl("https://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46"),
				action -> {
					quantity = (quantity != 0) ? (quantity - 100) : quantity;
					animateTitle((quantity != 0) ? "Decreased by 100 " + currency : "&4Deposit cannot be less than 0");
					restartMenu();

				});

		this.decrease10Button = Button.makeSimple(ItemCreator.of(CompMaterial.PLAYER_HEAD, "&cDecrease by 10 " + currency, "\nDecrease the amount you\nwould like to deposit")
						.skullUrl("https://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46"),
				action -> {
					quantity = (quantity != 0) ? (quantity - 10) : quantity;
					animateTitle((quantity != 0) ? "Decreased by 10 " + currency : "&4Deposit cannot be less than 0");
					restartMenu();

				});

	}

	@Override
	protected void onMenuClose(Player player, Inventory inventory) {
		quantity = 0;
		getPreviousMenu(player).displayTo(player);
	}


}


