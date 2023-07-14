package org.ancastal.banknative.commands;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.banknative.BankNative;
import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.gui.AccountMenu;
import org.ancastal.banknative.models.Account;
import org.ancastal.banknative.models.Bank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.SQLException;
import java.util.*;

public class AccountCommands implements CommandExecutor, TabCompleter {
	private final BankNative plugin;
	private final Database database;

	public AccountCommands(BankNative plugin, Database database) {
		this.plugin = plugin;
		this.database = database;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command cannot be executed from console.");
			return true;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Correct usage: /account <accept|deny|withdraw|deposit|menu>");
			return true;
		}

		if (!player.hasPermission("privatebanks.account.use") && !args[0].toLowerCase().equals("accept")) {
			player.sendMessage(ChatColor.RED + "You do not have permissions to use this command. privatebanks.command.use");
			return true;
		}


		String subCommand = args[0].toLowerCase();

		switch (subCommand) {
			case "accept":
				if (!player.hasPermission("privatebanks.command.account.accept")) break;
				try {
					accept(player, database);
				} catch (SQLException e) {
					plugin.getLogger().severe("An error occurred while using /accept: " + e.getMessage());
					e.printStackTrace();
				}
				break;
			case "deposit":

				try {
					deposit(player, args);
				} catch (SQLException e) {
					plugin.getLogger().severe("An error occurred while using /account deposit: " + e.getMessage());
					e.printStackTrace();
				}
				break;
			case "withdraw":
				try {
					withdraw(player, args);
				} catch (SQLException e) {
					plugin.getLogger().severe("An error occurred while using /account withdraw: " + e.getMessage());
					e.printStackTrace();
				}
				break;
			case "balance":
				try {
					showBalance(player);
				} catch (SQLException e) {
					plugin.getLogger().severe("An error occurred while using /account balance: " + e.getMessage());
					e.printStackTrace();
				}
				break;
			case "menu":
				try {
					Account account = database.getAccountHolder(player.getUniqueId().toString());
					new AccountMenu(player, account, database).displayTo(player);
				} catch (SQLException e) {
					plugin.getLogger().severe("An error occurred while using /account menu: " + e.getMessage());
					e.printStackTrace();
				}
				break;
			default:
				player.sendMessage(ChatColor.RED + "Unknown sub-command. /account <accept|deny|withdraw|deposit|menu>");
				break;
		}
		return true;
	}

	private void showBalance(Player player) throws SQLException {
		Account account = database.getAccountHolder(player.getUniqueId().toString());
		if (account == null) {
			player.sendMessage("You do not own a bank account");
			return;
		}
		Double balance = account.getBalance();
		player.sendMessage("Balance: " + balance);
	}

	private void deposit(Player sender, String[] args) throws SQLException {
		Economy economy = BankNative.getEconomy();
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /bank deposit <amount>");
		}
		Account account = database.getAccountHolder(sender.getUniqueId().toString());
		if (account == null) {
			sender.sendMessage("You do not own a bank account.");
		}
		double amount = Double.parseDouble(args[1]);
		double balance = economy.getBalance(sender);
		if (balance < amount) {
			sender.sendMessage(ChatColor.RED + "You do not have enough money.");
			return;
		}
		database.giveAccount(account, amount);
		economy.withdrawPlayer(sender, amount);
		sender.sendMessage(ChatColor.GREEN + "You have deposited " + args[1] + " KR.");
	}

	private void withdraw(Player sender, String[] args) throws SQLException {
		Economy economy = BankNative.getEconomy();
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /bank withdraw <amount>");
		}
		Account account = database.getAccountHolder(sender.getUniqueId().toString());
		if (account == null) {
			sender.sendMessage("You do not own a bank account.");
		}
		double amount = Double.parseDouble(args[1]);
		double balance = account.getBalance();
		if (balance < amount) {
			sender.sendMessage(ChatColor.RED + "You do not have enough money.");
			return;
		}
		database.takeAccount(account, amount);
		economy.depositPlayer(sender, amount);
		sender.sendMessage(ChatColor.GREEN + "You have withdrawn " + args[1] + " KR.");
	}

	private void accept(Player receiver, Database database) throws SQLException {
		if (BankCommands.map.isEmpty()) return;
		if (!BankCommands.map.containsKey(receiver.getUniqueId())) {
			return;
		}

		// Bank Owner who sends the request
		UUID sender = BankCommands.map.get(receiver.getUniqueId());
		Bank bank = database.getBankByOwner(sender.toString());
		List<Account> accountList = database.getAccountsList(bank.getBankName());
		if (accountList != null) {
			for (Account account : accountList) {
				if (Objects.equals(account.getUuid(), receiver.getUniqueId().toString())) {
					receiver.sendMessage(ChatColor.RED + "You already own an account in this bank.");
					return;
				}
			}
		}

		Account account = new Account(receiver.getUniqueId().toString(), receiver.getName(), bank.getBankName(), 0.0);
		database.createAccount(account);
		BankNative.getPermission().playerAdd(null, Bukkit.getOfflinePlayer(receiver.getUniqueId()), "privatebanks.account.use");
		receiver.sendMessage(ChatColor.GREEN + "You have created a deposit account in " + bank.getBankName());


	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		final String[] COMMANDS = {"accept", "withdraw", "deposit", "balance"};
		if (args.length != 1) return new ArrayList<>();
		final List<String> completions = new ArrayList<>();
		StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), completions);
		Collections.sort(completions);
		return completions;
	}
}
