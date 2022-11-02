package org.ancastal.bankplugin.menus;


import net.milkbowl.vault.economy.Economy;
import org.ancastal.bankplugin.BankPlugin;
import org.ancastal.bankplugin.model.BankCertificate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;


public class DepositMenu extends Menu {

	@Position(2)
	private final Button increaseButton;
	@Position(6)
	private final Button decreaseButton;
	@Position(4)
	private final Button confirmButton;


	public static void setQuantity(int quantity) {
		DepositMenu.quantity = quantity;
	}


	static int quantity = 0;

	DepositMenu(Player player) {
		setTitle("Set your deposit...");
		setSize(Integer.valueOf(9));
		//CompMetadata.setTempMetadata(player, "DepositMenu_" + BankPlugin.getInstance());

		this.confirmButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				DepositMenu.this.restartMenu();

				ItemStack item = player.getItemInHand();
				String bankName = Common.stripColors(item.getItemMeta().getDisplayName());
				BankCertificate.depositToBank(bankName, player, Double.valueOf(DepositMenu.quantity));
				quantity = 0;
				player.closeInventory();

			}


			@Override
			public ItemStack getItem() {
				return ItemCreator.of(CompMaterial.PAPER, "&bDeposit: &7" + DepositMenu.quantity + " Krunas", "\nConfirm your deposit.").glow(true).make();
			}

		};

		this.increaseButton = Button.makeSimple(
				ItemCreator.of(CompMaterial.PLAYER_HEAD, "&2Increase by 1.000 Krunas", "\nIncrease the amount you\nwould like to deposit")
						.skullUrl("https://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f"),
				action -> {
					Economy economy = BankPlugin.getEconomy();
					if (economy.getBalance(player) < quantity) {
						animateTitle("&4Not enough money");
						return;
					}
					quantity += 1000;
					animateTitle("Increased by 1000 Krunas");
					restartMenu();


				});

		this.decreaseButton = Button.makeSimple(
				ItemCreator.of(CompMaterial.PLAYER_HEAD, "&cDecrease by 1.000 Krunas", "\nDecrease the amount you\nwould like to deposit")
						.skullUrl("https://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46"),
				action -> {

					quantity = (quantity != 0) ? (quantity - 1000) : quantity;
					animateTitle((quantity != 0) ? "Decreased by 1000 Krunas" : "&4Deposit cannot be less than 0");
					restartMenu();

				});

	}

	@Override
	protected void onMenuClose(Player player, Inventory inventory) {
		quantity = 0;
	}

	@Override
	public ItemStack getItemAt(int slot) {

		if (slot == 2 || slot == 6 || slot == 4)
			return NO_ITEM;
		ItemStack filler = new ItemStack(ItemCreator.of(CompMaterial.BLACK_STAINED_GLASS_PANE, " ").make());

		return filler;

	}

}


