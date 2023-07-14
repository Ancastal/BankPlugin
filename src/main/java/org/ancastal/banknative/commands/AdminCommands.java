package org.ancastal.banknative.commands;

import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.models.Account;
import org.ancastal.banknative.models.Bank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class AdminCommands implements CommandExecutor, TabCompleter {
	private final Database database;

	public AdminCommands(Database database) {
		this.database = database;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		if (!sender.hasPermission("privatebanks.admin")) {
			sender.sendMessage("You do not have permission to use this command.");
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
			return true;
		}
		Player player = (Player) sender;
		String subcommand = strings[0].toLowerCase();
		try {
			switch (subcommand) {
				case "deletebank":
					String bankName = strings[1];
					Bank bank = database.getBankByName(bankName);
					if (bank == null) {
						Bank bank1 = database.getBankByOwner(bankName);
						if (bank1 == null) {
							player.sendMessage("Bank " + bankName + " does not exist.");
							return true;
						}
						bank = bank1;
					}
					database.deleteBank(bank);
					player.sendMessage("Bank " + bankName + " deleted.");
					Bukkit.getLogger().info("Bank " + bankName + " deleted by " + player.getName());
					break;
				case "setbalance":
					String playerName1 = strings[1];
					Bank bank2 = database.getBankByOwner(playerName1);
					if (bank2 == null) {
						player.sendMessage("Player " + playerName1 + " does not have a bank.");
						return true;
					}
					double balance = Double.parseDouble(strings[2]);
					database.updateBalance(bank2, balance);
					player.sendMessage("Balance for player " + playerName1 + " set to " + balance);
					Bukkit.getLogger().info("Balance for player " + playerName1 + " set to " + balance + " by " + player.getName());
					break;
				case "setinterest":
					String playerName2 = strings[1];
					Bank bank3 = database.getBankByOwner(playerName2);
					if (bank3 == null) {
						player.sendMessage("Player " + playerName2 + " does not have a bank.");
						return true;
					}
					double interest = Double.parseDouble(strings[2]);
					bank3.setInterest(String.valueOf(interest));
					player.sendMessage("Interest for player " + playerName2 + " set to " + interest);
					Bukkit.getLogger().info("Interest for player " + playerName2 + " set to " + interest + " by " + player.getName());
					break;
				case "deleteaccount":
					String accountUUID = strings[1];
					Account account = database.getAccountHolder(accountUUID);
					if (account == null) {
						player.sendMessage("Account " + accountUUID + " does not exist.");
						return true;
					}
					database.deleteAccountByUUID(account);
					player.sendMessage("Account " + accountUUID + " deleted.");
					Bukkit.getLogger().info("Account " + accountUUID + " deleted by " + player.getName());
					break;
				case "setowner":
					String oldOwner = strings[1];
					String newOwner = strings[2];
					Player oldOwnerPlayer = Bukkit.getPlayer(oldOwner);
					Player newOwnerPlayer = Bukkit.getPlayer(newOwner);
					if (oldOwnerPlayer == null) {
						player.sendMessage("Player " + oldOwner + " does not exist.");
						return true;
					}
					if (newOwnerPlayer == null) {
						player.sendMessage("Player " + newOwner + " does not exist.");
						return true;
					}
					database.changeBankOwner(oldOwnerPlayer.getUniqueId().toString(), newOwner);
					database.changeBankUUID(oldOwnerPlayer.getUniqueId().toString(), newOwnerPlayer.getUniqueId().toString());
					player.sendMessage("Bank owner changed from " + oldOwner + " to " + newOwner);
					Bukkit.getLogger().info("Bank owner changed from " + oldOwner + " to " + newOwner + " by " + player.getName());
					break;
			}
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command
			command, @NotNull String s, @NotNull String[] strings) {
		final String[] COMMANDS = {"deletebank", "setbalance", "setinterest", "deleteaccount"};
		if (strings.length == 1) {
			return List.of(COMMANDS);
		}
		return null;
	}
}


