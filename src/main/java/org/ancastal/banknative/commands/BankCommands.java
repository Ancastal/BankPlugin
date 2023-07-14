package org.ancastal.banknative.commands;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.banknative.BankNative;
import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.gui.BankMenu;
import org.ancastal.banknative.listeners.InterestListener;
import org.ancastal.banknative.models.Account;
import org.ancastal.banknative.models.Bank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.util.StringUtil;
import org.mineacademy.fo.Common;

import java.sql.SQLException;
import java.util.*;

public class BankCommands implements CommandExecutor, TabCompleter {
	private final BankNative plugin;
	private final Database database;
	public static HashMap<UUID, UUID> map = new HashMap<>();
	Economy economy = BankNative.getEconomy();


	public BankCommands(BankNative plugin, Database database) {
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

		if (!player.hasPermission("privatebanks.command.use")) {
			player.sendMessage(ChatColor.RED + "You do not have permissions to use this command");
			return true;
		}

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Correct usage: /bank <create|balance|withdraw|deposit|clear>");
			return true;
		}

		String subCommand = args[0].toLowerCase();
		try {
			switch (subCommand) {
				case "create":
					if (player.hasPermission("privatebanks.command.create")) {
						createBank(player, args);
						player.sendMessage(ChatColor.GREEN + "Bank created successfully.");
					} else {
						player.sendMessage(ChatColor.RED + "You do not have permission to use this command. privatebanks.command.create");
					}
					break;
				case "balance":
					showBalance(player);
					break;
				case "withdraw":
					if (args.length < 2) {
						player.sendMessage(ChatColor.RED + "Correct usage: /bank withdraw <amount>");
						return true;
					}
					withdraw(player, args);
					break;
				case "deposit":
					if (args.length < 2) {
						player.sendMessage(ChatColor.RED + "Correct usage: /bank deposit <amount>");
						return true;
					}
					deposit(player, args);
					break;
				case "account":
					if (args.length < 2) {
						player.sendMessage(ChatColor.RED + "Correct usage: /bank account <username>");
						return true;
					}
					player.sendMessage(args[1]);
					sendAccountRequest(player, args[1]);
					break;
				case "menu":
					Bank bank = null;
					bank = database.getBankByOwner(player.getUniqueId().toString());
					if (bank == null) {
						player.sendMessage(ChatColor.RED + "You do not own any bank.");
						return true;
					}
					new BankMenu(player, bank.getBankName(), database).displayTo(player);
					break;
				case "interest":
					setInterests(player, args[1]);
					break;
				case "clear":
					if (player.hasPermission("privatebanks.command.clear")) {
						bank = database.getBankByOwner(player.getUniqueId().toString());
						if (bank == null) {
							player.sendMessage(ChatColor.RED + "You do not own a bank.");
							return true;
						}
						deleteBank(bank, player);
					} else {
						player.sendMessage("You do not have permission to use this command. privatebanks.command.clear");
					}
					break;
				case "forceclear":
					if (player.hasPermission("privatebanks.admin")) {
						OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
						if (!target.hasPlayedBefore()) {
							player.sendMessage(ChatColor.RED + "Player does not exist.");
							return true;
						}
						bank = database.getBankByOwner(target.getUniqueId().toString());
						if (bank == null) {
							player.sendMessage(ChatColor.RED + "Player does not own a bank.");
							return true;
						}
						List<Account> accounts = database.getAccountsList(bank.getBankName());
						if (accounts == null) {
							database.deleteBank(bank);
							return true;
						}
						for (Account account : accounts) {
							database.deleteAccountByUUID(account);
						}
						OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(UUID.fromString(bank.getPlayerUUID().toString()));
						economy.depositPlayer(bankOwner, bank.getBalance());
						database.deleteBank(bank);
						player.sendMessage("Bank has been cleared.");
					}
					break;
				default:
					player.sendMessage(ChatColor.RED + "Unknown sub-command. Correct usage: /bank <create|balance|withdraw|deposit|menu|account|interest>");
					break;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return true;
	}


	private void deleteBank(Bank bank, Player player) throws SQLException {
		if (bank == null) {
			player.sendMessage(ChatColor.RED + "You do not own any bank.");
			return;
		}
		List<Account> accounts = database.getAccountsList(bank.getBankName());
		if (accounts == null) {
			double balance = bank.getBalance();
			economy.depositPlayer(player, balance);
			database.deleteBank(bank);
			player.sendMessage(ChatColor.GREEN + "Your bank has been cleared.");
			return;
		}
		for (Account account : accounts) {
			Double balance = account.getBalance();
			if (bank.getBalance() >= balance) {
				database.take(bank, balance);
				OfflinePlayer receiver = Bukkit.getOfflinePlayer(account.getUuid());
				economy.depositPlayer(receiver, balance);
				if (receiver.isOnline()) {
					((Player) receiver).sendMessage("You have been given " + balance + " from your deposit account.");
				}
				database.deleteAccountByUUID(account);
			} else {
				player.sendMessage(ChatColor.YELLOW + "You cannot pay back your deposit accounts.");
				player.sendMessage(ChatColor.RED + "Your bank cannot be deleted.");
				return;
			}
		}
		double balance = bank.getBalance();
		try {
			database.deleteBank(bank);
			economy.depositPlayer(player, balance);
		} catch (Throwable t) {
			plugin.getLogger().severe("There was an error while deleting the bank: " + t.getMessage());
			t.printStackTrace();
		}
		player.sendMessage(ChatColor.GREEN + "Your bank has been cleared.");
	}

	private void setInterests(Player player, String interest) throws SQLException {
		Double interestAsDouble = Double.parseDouble(interest);
		if (interestAsDouble == null) return;
		if (interestAsDouble.isNaN()) return;
		Bank bank = database.getBankByOwner(player.getUniqueId().toString());
		if (bank == null) {
			player.sendMessage(ChatColor.RED + "You do not own any bank.");
			return;
		}
		database.updateInterest(bank, interest);
		plugin.registerEventIfNotRegistered(InterestListener.class);

	}

	private void sendAccountRequest(Player sender, String receiver) throws SQLException {
		String senderUUID = sender.getUniqueId().toString();
		Player playerReceiver = Bukkit.getPlayer(receiver);
		Bank bank = database.getBankByOwner(senderUUID);
		String bankName = bank.getBankName();

		if (bankName == null || bankName == "") {
			sender.sendMessage("You do not own any bank.");
		}
		Common.tellNoPrefix(Bukkit.getPlayer(receiver), "&e" + bankName + " &einvites you to create a deposit account.\n&e" + "Type &a/account accept &eor &c/account deny");
		map.put(playerReceiver.getUniqueId(), sender.getUniqueId());
		sender.sendMessage(ChatColor.YELLOW + "You have sent a request to create a deposit account.");
		PermissionAttachment perms = playerReceiver.addAttachment(BankNative.getInstance());
		perms.setPermission("privatebanks.command.account.accept", true);
		perms.setPermission("privatebanks.command.account.deny", true);
		Common.runLater(800, () -> map.clear());
	}


	private void showBalance(Player player) throws SQLException {
		Bank bank = database.getBankByOwner(player.getUniqueId().toString());
		if (bank == null) {
			player.sendMessage(ChatColor.RED + "This bank does not exist.");
			return;
		}
		double balance = bank.getBalance();
		player.sendMessage(ChatColor.GREEN + "Balance: " + ChatColor.GRAY + balance);
	}


	private void createBank(Player executor, String[] args) throws SQLException {
		if (args.length < 3) {
			executor.sendMessage(ChatColor.RED + "Correct usage: /bank create <username> <bankName>");
			return;
		}
		Player player = Bukkit.getPlayer(args[1]);
		assert player != null;
		if (database.getBankByOwner(player.getUniqueId().toString()) != null || database.getBankByName(args[2]) != null)
			return;
		String bankName = args[2];
		Bank bank = new Bank(player.getUniqueId().toString(), player.getName(), bankName, 0D, 1, "0", 0);
		database.createBank(bank);

	}

	private void withdraw(Player executor, String[] args) throws SQLException {
		Economy economy = BankNative.getEconomy();
		if (args.length < 2) {
			executor.sendMessage(ChatColor.RED + "Correct usage: /bank withdraw <amount>");
			return;
		}
		Bank bank = database.getBankByOwner(executor.getUniqueId().toString());
		double amount = Double.parseDouble(args[1]);
		double bankBalance = bank.getBalance();
		if (bankBalance < amount) {
			executor.sendMessage(ChatColor.RED + "This bank does not have enough money.");
			return;
		}
		database.take(bank, amount);
		economy.depositPlayer(executor, amount);
		executor.sendMessage(ChatColor.GREEN + "You have withdrawn " + args[1] + " KR.");
		Bukkit.getLogger().info(executor.getName() + " has withdrawn " + args[1] + " KR " + "from " + bank.getBankName());
	}

	private void deposit(Player executor, String[] args) throws SQLException {
		Economy economy = BankNative.getEconomy();
		if (args.length < 2) {
			executor.sendMessage(ChatColor.RED + "Correct usage: /bank deposit <amount>");
			return;
		}
		Bank bank = database.getBankByOwner(executor.getUniqueId().toString());
		double amount = Double.parseDouble(args[1]);
		double playerBalance = economy.getBalance(executor);
		if (playerBalance < amount) {
			executor.sendMessage(ChatColor.RED + "You do not have enough money.");
			return;
		}
		database.give(bank, amount);
		economy.withdrawPlayer(executor, amount);
		executor.sendMessage(ChatColor.GREEN + "You have deposited " + args[1] + " KR.");
		Bukkit.getLogger().info(executor.getName() + " has deposited " + args[1] + " KR to " + bank.getBankName());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		final String[] COMMANDS = {"deposit", "withdraw", "create", "balance", "interest", "account", "menu", "clear"};
		final List<String> completions = new ArrayList<>();

		if (args.length == 1) {
			StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), completions);
			Collections.sort(completions);
			return completions;
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("account") || args[0].equalsIgnoreCase("create")) {
				String input = args[1].toLowerCase();
				for (Player player : Bukkit.getOnlinePlayers()) {
					String playerName = player.getName();
					if (playerName.toLowerCase().startsWith(input)) {
						completions.add(playerName);
					}
				}
			} else if (args[0].equalsIgnoreCase("withdraw") || args[0].equalsIgnoreCase("deposit")) {
				completions.add("<amount>");
			} else if (args[0].equalsIgnoreCase("interest")) {
				completions.add("<interest_rate>");
			}
			return completions;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
			completions.add("<bank_name>");
			return completions;
		}

		return new ArrayList<>();
	}


}