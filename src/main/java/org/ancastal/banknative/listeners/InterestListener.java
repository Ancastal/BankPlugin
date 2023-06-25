package org.ancastal.banknative.listeners;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.banknative.BankNative;
import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.models.Account;
import org.ancastal.banknative.models.Bank;
import org.ancastal.banknative.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;

import java.sql.SQLException;
import java.util.List;


public final class InterestListener implements Listener {
	public int taskId;

	public int getTaskId() {
		return taskId;
	}

	private final Database database;

	public InterestListener(Database database, org.ancastal.banknative.settings.Settings config) {
		this.database = database;
	}

	@EventHandler
	public void onJoinEvent(PlayerJoinEvent e) throws SQLException {

		Player player = e.getPlayer();
		final Bank[] bank = {database.getBankByOwner(player.getUniqueId().toString())};
		if (bank[0] == null) return;
		final List<Account>[] accounts = new List[]{database.getAccountsList(bank[0].getBankName())};
		if (accounts[0] == null) return;

		final Double[] bankBalance = {bank[0].getBalance()};
		final Double[] interest = {Double.parseDouble(bank[0].getInterest())};
		String currency = Settings.getCurrency();
		Economy economy = BankNative.getEconomy();

		if (interest[0] != 0D || !interest[0].isNaN()) {
			BukkitRunnable task = new BukkitRunnable() {

				@Override
				public void run() {
					try {
						bank[0] = database.getBankByOwner(player.getUniqueId().toString());
						interest[0] = Double.parseDouble(bank[0].getInterest());
						bankBalance[0] = bank[0].getBalance(); // Update the bank balance here
						accounts[0] = database.getAccountsList(bank[0].getBankName()); // Update the accounts list here
					} catch (SQLException e) {
						e.printStackTrace();
						return;
					}

					if (interest[0] == 0.0) {
						this.cancel();
						HandlerList.unregisterAll(InterestListener.this);
						Bukkit.getScheduler().cancelTask(getTaskId());
						Common.tellNoPrefix(player, "&cInterest is disabled. Scheduled task has been cancelled.");
						this.cancel();
						return;
					}

					if (bankBalance[0] > interest[0] * accounts[0].size()) {
						for (Account account : accounts[0]) {
							if (interest[0] == 0.0) Bukkit.getScheduler().cancelTask(getTaskId());
							try {
								System.out.println(interest[0]);
								System.out.println(bank[0].getBankName());
								database.takeSync(bank[0], interest[0]);
								database.giveAccountSync(account, interest[0]);
								System.out.println("Done!");
							} catch (SQLException e) {
								throw new RuntimeException(e);
							}
						}
					} else {
						Common.tellNoPrefix(player, "&cYour bank does not have enough money to pay interests.");
						return;
					}
					Common.tellNoPrefix(player, "&7You have paid &a" + interest[0] + " &7" + currency + " to your accounts. (Total: " + (interest[0] * accounts[0].size()) + ")");
				}
			};

			task.runTaskTimerAsynchronously(BankNative.getInstance(), 0, 900 * 20);


			//Bukkit.getScheduler().runTaskTimerAsynchronously(BankNative.getInstance(), interestPaymentTask, 0, 100); // Run the task every hour
		}
	}


}



