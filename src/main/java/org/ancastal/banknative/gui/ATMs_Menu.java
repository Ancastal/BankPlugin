package org.ancastal.banknative.gui;


import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.ancastal.banknative.BankNative;
import org.ancastal.banknative.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Locale;

/**
 * Opens a GUI to deposit money in bank.
 */


public class ATMs_Menu extends Menu {

	public static String formattedDouble(double value) {
		return String.format(Locale.US, "%,.2f", value);
	}

	@Position(1)
	private final Button ATM_2;
	@Position(2)
	private final Button ATM_3;
	@Position(3)
	private final Button ATM_4;
	@Position(4)
	private final Button ATM_5;

	public ATMs_Menu(Player player, String bankName) {
		setTitle("Buy ATMs...");
		setSize(9);
		String currency = Settings.getCurrency();
		Permission permission = BankNative.getPermission();
		Economy economy = BankNative.getEconomy();

		this.ATM_2 = Button.makeSimple(ItemCreator.of(CompMaterial.PAPER, "ATM Limit (2)", "\nIncrease your ATM limit\n\n&bPrice: &e5.000" + currency),
				action -> {
					if (economy.has(player.getName(), 5000)) {
						economy.withdrawPlayer(player, 5000);
						permission.playerAdd(null, player, "PrivateBanks.atm.2");
						tellSuccess("You have increased your ATM limit.");

						restartMenu("ATM Limit + 1");

					} else {
						tellError("You do not have enough money.");
					}
				}
		);

		if (PlayerUtil.hasPerm(player, "PrivateBanks.atm.2")) {
			this.ATM_3 = Button.makeSimple(ItemCreator.of(CompMaterial.PAPER, "ATM Limit (3)", "\nIncrease your ATM limit\n\n&bPrice: &e10.000" + currency),
					action -> {
						if (economy.has(player.getName(), 10000)) {
							economy.withdrawPlayer(player, 10000);
							permission.playerAdd(null, player, "PrivateBanks.atm.3");

							tellSuccess("You have increased your ATM limit.");
							restartMenu("ATM Limit + 1");
						} else {
							tellError("You do not have enough money.");
						}
					}
			);
		} else {
			this.ATM_3 = Button.makeSimple(ItemCreator.of(CompMaterial.PLAYER_HEAD, "&cATM Limit (3) ", "\n&cYou do not own previous ATMs.")
					.skullUrl("https://textures.minecraft.net/texture/e9cdb9af38cf41daa53bc8cda7665c509632d14e678f0f19f263f46e541d8a30"), action -> {

			});
		}

		if (PlayerUtil.hasPerm(player, "PrivateBanks.atm.3")) {
			this.ATM_4 = Button.makeSimple(ItemCreator.of(CompMaterial.PAPER, "ATM Limit (4)", "\nIncrease your ATM limit\n\n&bPrice: &e25.000" + currency),
					action -> {
						if (economy.has(player.getName(), 25000)) {
							economy.withdrawPlayer(player, 25000);
							permission.playerAdd(null, player, "PrivateBanks.atm.4");

							tellSuccess("You have increased your ATM limit.");
							restartMenu("ATM Limit + 1");
						} else {
							tellError("You do not have enough money.");
						}
					}
			);
		} else {
			this.ATM_4 = Button.makeSimple(ItemCreator.of(CompMaterial.PLAYER_HEAD, "&cATM Limit (4) ", "\n&cYou do not own previous ATMs.")
					.skullUrl("https://textures.minecraft.net/texture/e9cdb9af38cf41daa53bc8cda7665c509632d14e678f0f19f263f46e541d8a30"), action -> {

			});
		}
		if (PlayerUtil.hasPerm(player, "PrivateBanks.atm.4")) {
			this.ATM_5 = Button.makeSimple(ItemCreator.of(CompMaterial.PAPER, "ATM Limit (5)", "\nIncrease your ATM limit\n\n&bPrice: &e25.000" + currency),
					action -> {
						if (economy.has(player.getName(), 25000)) {
							economy.withdrawPlayer(player, 25000);
							permission.playerAdd(null, player, "PrivateBanks.atm.5");

							tellSuccess("You have increased your ATM limit.");
							restartMenu("ATM Limit + 1");
						} else {
							tellError("You do not have enough money.");
						}
					}
			);
		} else {
			this.ATM_5 = Button.makeSimple(ItemCreator.of(CompMaterial.PLAYER_HEAD, "&cATM Limit (5) ", "\n&cYou do not own previous ATMs.")
					.skullUrl("https://textures.minecraft.net/texture/e9cdb9af38cf41daa53bc8cda7665c509632d14e678f0f19f263f46e541d8a30"), action -> {

			});
		}

	}

	@Override
	protected void onMenuClose(Player player, Inventory inventory) {
		getPreviousMenu(player).displayTo(player);
	}


	@Override
	public ItemStack getItemAt(int slot) {

		if (slot == 1 || slot == 2 || slot == 3)
			return NO_ITEM;
		ItemStack filler = new ItemStack(ItemCreator.of(CompMaterial.BLACK_STAINED_GLASS_PANE, " ").make());

		return filler;

	}

}


