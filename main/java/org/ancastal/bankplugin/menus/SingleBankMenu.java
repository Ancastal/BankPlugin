package org.ancastal.bankplugin.menus;


import net.milkbowl.vault.economy.Economy;
import org.ancastal.bankplugin.BankPlugin;
import org.ancastal.bankplugin.model.BankCertificate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;


public class SingleBankMenu extends Menu {


	@Position(10)
	private final Button depositButton;


	@Position(12)
	private final Button withdrawButton;


	@Position(14)
	private final Button balanceButton;


	@Position(16)
	private final Button transferButton;

	@Position(15)
	private Button accountsButton = Button.DummyButton.makeEmpty();

	private final Economy economy = BankPlugin.getEconomy();


	public SingleBankMenu(Player player, final String bankName) {

		setTitle("Bank Administration");
		setSize(27);
		setViewer(player);

		this.depositButton = new ButtonMenu(new DepositMenu(player, bankName),
				ItemCreator.of(CompMaterial.PLAYER_HEAD, "&bDeposit Money", "\nDeposit money into \nyour Bank Account\n")
						.skullUrl("https://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f")
						.make()
		);

		this.withdrawButton = new ButtonMenu(new WithdrawMenu(player, bankName),
				ItemCreator.of(CompMaterial.PLAYER_HEAD, "&bWithdraw Money", "\nWithdraw money into \nyour Bank Account\n")
						.skullUrl("https://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46")
						.make()
		);

		this.balanceButton = Button.makeSimple(
				ItemCreator.of(CompMaterial.PLAYER_HEAD, "&bCheck Balance", "\nGet the balance of \nyour Bank Account\n")
						.skullUrl("https://textures.minecraft.net/texture/44c00164238d8ad99afad790c3050ff9eff410e2d4a3fe85600389dcb818a849"),
				action -> {

					ItemStack item = player.getItemInHand();
					tellInfo("Balance: " + economy.getBalance(BankCertificate.getBankString(bankName)) + "kr");

				});

		this.transferButton = Button.makeSimple(ItemCreator.of(CompMaterial.PAPER, "&bTransfer Money", "\nTransfer money to a\nplayer or a bank.\n"), action -> {

			Inventory inventory = Bukkit.createInventory(player, 18, "Transfer money to...");
			player.openInventory(inventory);
			for (Player p : Bukkit.getOnlinePlayers()) {

				inventory.addItem(ItemCreator.of(CompMaterial.PLAYER_HEAD, p.getName(), "\nTransfer money to \n" + p.getName()).skullOwner(p.getName()).make());

			}

		});
		ItemStack item = player.getItemInHand();
		if (item != null && item.getItemMeta().getDisplayName() != bankName) return;
		this.accountsButton = new ButtonMenu(
				new AccountsMenu(
						player
				), ItemCreator.of(
						CompMaterial.PLAYER_HEAD, "&bBank Accounts", "\nManage bank accounts")
				.skullUrl("https://textures.minecraft.net/texture/d1f222b98538d4ea5dc48891a98138dc191b807ef281da749b781039f1209ca4")
				.make()

		);
	}


	@Override
	public ItemStack getItemAt(int slot) {

		if (slot == 11 || slot == 13 || slot == 15)
			return NO_ITEM;

		ItemStack filler = new ItemStack(ItemCreator.of(CompMaterial.BLACK_STAINED_GLASS_PANE, " ").make());

		return filler;

	}

}


