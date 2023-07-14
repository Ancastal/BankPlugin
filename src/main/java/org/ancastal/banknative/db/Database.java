package org.ancastal.banknative.db;

import net.milkbowl.vault.economy.Economy;
import org.ancastal.banknative.BankNative;
import org.ancastal.banknative.models.Account;
import org.ancastal.banknative.models.Bank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
	private BankNative plugin;

	private Connection connection;


	public enum BankType {
		BANK,
		ACCOUNT
	}

	public Database(BankNative plugin) {
		this.plugin = plugin;
	}

	public Connection getConnection() throws SQLException {
		if (connection == null) {
			String url = "jdbc:mysql://" + plugin.getSettings().getAddress() + "/" + plugin.getSettings().getDatabase();
			String user = plugin.getSettings().getUsername();
			String password = plugin.getSettings().getPassword();
			connection = DriverManager.getConnection(url, user, password);
			System.out.println("Connected to database.");
		}
		return connection;
	}


	public void initializeDatabase() throws SQLException {
		try (Statement statement = getConnection().createStatement()) {
			String sql = "CREATE TABLE IF NOT EXISTS banks (uuid varchar(36) primary key, bankOwner varchar(36), bankName varchar(36), balance double, ATMs int, interest double, activeATMs integer)";
			statement.execute(sql);
			sql = "CREATE TABLE IF NOT EXISTS bank_accounts (uuid varchar(36) primary key, accountHolder varchar(36), bankName varchar(36), balance double)";
			statement.execute(sql);
		}
	}

	public void createLoan(String playerId, double amount, double interestRate, int duration) throws SQLException {
		try (PreparedStatement statement = getConnection()
				.prepareStatement("INSERT INTO loans(playerId, amount, interestRate, duration, status) VALUES (?, ?, ?, ?, ?)")
		) {
			statement.setString(1, playerId);
			statement.setDouble(2, amount);
			statement.setDouble(3, interestRate);
			statement.setInt(4, duration);
			statement.setString(5, "active");
			statement.executeUpdate();
		}
	}

	public void updateLoanStatus(int loanId, String status) throws SQLException {
		try (PreparedStatement statement = getConnection()
				.prepareStatement("UPDATE loans SET status = ? WHERE loanId = ?")
		) {
			statement.setString(1, status);
			statement.setInt(2, loanId);
			statement.executeUpdate();
		}
	}

	public Bank getBankByName(String bankName) throws SQLException {
		try (PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM banks WHERE bankName = ?")) {
			statement.setString(1, bankName);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Bank(
							resultSet.getString("uuid"),
							resultSet.getString("bankOwner"),
							resultSet.getString("bankName"),
							resultSet.getDouble("balance"),
							resultSet.getInt("ATMs"),
							resultSet.getString("interest"),
							resultSet.getInt("activeATMs"));
				}
			}
		}
		return null;
	}

	public Bank getBankByOwner(String owner) throws SQLException {
		try (PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM banks WHERE uuid = ?")) {
			statement.setString(1, owner);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Bank(
							resultSet.getString("uuid"),
							resultSet.getString("bankOwner"),
							resultSet.getString("bankName"),
							resultSet.getDouble("balance"),
							resultSet.getInt("ATMs"),
							resultSet.getString("interest"),
							resultSet.getInt("activeATMs"));
				}
			}
		}
		return null;
	}

	public Account getAccountHolder(String uuid) throws SQLException {
		try (PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM bank_accounts WHERE uuid = ?")) {
			statement.setString(1, uuid);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Account(
							resultSet.getString("uuid"),
							resultSet.getString("accountHolder"),
							resultSet.getString("bankName"),
							resultSet.getDouble("balance"));
				}
			}
		}
		return null;
	}

	public List<Account> getAccountsList(String bankName) throws SQLException {
		List<Account> accounts = new ArrayList<>();
		try (PreparedStatement statement = getConnection().prepareStatement("SELECT uuid, accountHolder, balance FROM bank_accounts WHERE bankName = ?")) {
			statement.setString(1, bankName);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Account account = new Account(
							resultSet.getString("uuid"),
							resultSet.getString("accountHolder"),
							bankName,
							resultSet.getDouble("balance"));
					accounts.add(account);
				}
			}
		}
		return accounts.isEmpty() ? null : accounts;
	}


	public void createBank(Bank bank) throws SQLException {
		try (PreparedStatement statement = getConnection()
				.prepareStatement("INSERT INTO banks(uuid, bankOwner, bankName, balance, ATMs, interest, activeATMs) VALUES (?, ?, ?, ?, ?, ?, ?)")
		) {
			statement.setString(1, bank.getPlayerUUID());
			statement.setString(2, bank.getUsername());
			statement.setString(3, bank.getBankName());
			statement.setDouble(4, bank.getBalance());
			statement.setInt(5, bank.getAtms());
			statement.setString(6, bank.getInterest());
			statement.setInt(7, bank.getActiveATMs());
			statement.executeUpdate();
		}
	}

	public void changeBankOwner(String uuid, String newOwner) throws SQLException {
		try (PreparedStatement statement = getConnection()
				.prepareStatement("UPDATE banks SET bankOwner = ? WHERE uuid = ?")
		) {
			statement.setString(1, newOwner);
			statement.setString(2, uuid);
			statement.executeUpdate();
		}
	}

	public void changeBankUUID(String oldUUID, String newUUID) throws SQLException {
		try (PreparedStatement statement = getConnection()
				.prepareStatement("UPDATE banks SET uuid = ? WHERE uuid = ?")
		) {
			statement.setString(1, newUUID);
			statement.setString(2, oldUUID);
			statement.executeUpdate();
		}
	}

	public void createAccount(Account account) throws SQLException {
		try (PreparedStatement statement = getConnection()
				.prepareStatement("INSERT INTO bank_accounts(uuid, accountHolder, bankName, balance) VALUES (?, ?, ?, ?)")
		) {
			statement.setString(1, account.getUuid());
			statement.setString(2, account.getAccountHolder());
			statement.setString(3, account.getBankName());
			statement.setDouble(4, account.getBalance());
			statement.executeUpdate();
		}
	}

	public void deleteBank(Bank bank) throws SQLException {
		try (PreparedStatement statement = getConnection()
				.prepareStatement("DELETE FROM banks WHERE uuid = ?")) {
			statement.setString(1, bank.getPlayerUUID());
			statement.executeUpdate();
		}
	}

	public void deleteAccountByUUID(Account account) throws SQLException {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try (PreparedStatement statement = getConnection()
					.prepareStatement("DELETE FROM bank_accounts WHERE uuid = ?")) {
				statement.setString(1, account.getUuid());
				statement.executeUpdate();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});

	}

	public void updateBalance(Bank bank, Double money) throws SQLException {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try (PreparedStatement statement = getConnection()
					.prepareStatement("UPDATE banks SET balance = ? WHERE uuid = ?")) {
				statement.setDouble(1, money);
				statement.setString(2, bank.getPlayerUUID());
				statement.executeUpdate();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void updateBalanceSync(Bank bank, Double money) throws SQLException {
		try (PreparedStatement statement = getConnection()
				.prepareStatement("UPDATE banks SET balance = ? WHERE uuid = ?")) {
			statement.setDouble(1, money);
			statement.setString(2, bank.getPlayerUUID());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public void updateInterest(Bank bank, String interest) throws SQLException {

		try (PreparedStatement statement = getConnection()
				.prepareStatement("UPDATE banks SET interest = ? WHERE uuid = ?")) {
			statement.setString(1, interest);
			statement.setString(2, bank.getPlayerUUID());
			statement.executeUpdate();
		}
	}

	public void give(Bank bank, Double money) throws SQLException {
		updateBalance(bank, bank.getBalance() + money);
	}

	public void take(Bank bank, Double money) throws SQLException {
		updateBalance(bank, bank.getBalance() - money);
	}

	public void takeSync(Bank bank, Double money) throws SQLException {
		updateBalanceSync(bank, bank.getBalance() - money);
	}

	public void clear(Bank bank, Double money) throws SQLException {
		updateBalance(bank, 0D);
	}

	public void updateAccount(Account account, Double money) throws SQLException {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try (PreparedStatement statement = getConnection()
					.prepareStatement("UPDATE bank_accounts SET balance = ? WHERE uuid = ?")) {
				statement.setDouble(1, money);
				statement.setString(2, account.getUuid());
				statement.executeUpdate();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void updateAccountSync(Account account, Double money) throws SQLException {

		try (PreparedStatement statement = getConnection()
				.prepareStatement("UPDATE bank_accounts SET balance = ? WHERE uuid = ?")) {
			statement.setDouble(1, money);
			statement.setString(2, account.getUuid());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public void giveAccount(Account account, Double money) throws SQLException {
		updateAccount(account, account.getBalance() + money);
	}

	public void giveAccountSync(Account account, Double money) throws SQLException {
		updateAccountSync(account, account.getBalance() + money);
	}

	public void takeAccount(Account account, Double money) throws SQLException {
		updateAccount(account, account.getBalance() - money);
	}

	public void clearAccount(Account account, Double money) throws SQLException {
		updateAccount(account, 0D);
	}

	public void bankDeposit(Player player, Double quantity, Database database) throws SQLException {
		Economy economy = BankNative.getEconomy();
		double playerBalance = economy.getBalance(player);
		if (playerBalance < quantity) {
			player.sendMessage(ChatColor.RED + "You do not have enough money.");
			return;
		}
		Bank bank = database.getBankByOwner(player.getUniqueId().toString());
		if (bank == null) return;
		economy.withdrawPlayer(player, quantity + 0.0);
		database.give(bank, quantity + 0.0);
		player.sendMessage(ChatColor.GREEN + "You have deposited " + quantity + " KR.");
	}

	public void increaseActiveATMs(Bank bank) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try (PreparedStatement statement = getConnection()
					.prepareStatement("UPDATE banks SET activeATMs = ? WHERE bankName = ?")) {
				statement.setDouble(1, bank.getActiveATMs() + 1);
				statement.setString(2, bank.getBankName());
				statement.executeUpdate();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		});
	}

	public void decreaseActiveATMs(Bank bank) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try (PreparedStatement statement = getConnection()
					.prepareStatement("UPDATE banks SET activeATMs = ? WHERE bankName = ?")) {
				statement.setDouble(1, bank.getActiveATMs() - 1);
				statement.setString(2, bank.getBankName());
				statement.executeUpdate();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		});
	}

	public void bankWithdraw(Player player, Double quantity, Database database) throws SQLException {
		Economy economy = BankNative.getEconomy();
		Bank bank = database.getBankByOwner(player.getUniqueId().toString());
		if (bank == null) return;
		double bankBalance = bank.getBalance();
		if (bankBalance < quantity) {
			player.sendMessage(ChatColor.RED + "This bank does not have enough money.");
			return;
		}
		economy.depositPlayer(player, quantity + 0.0);
		database.take(bank, quantity + 0.0);
		player.sendMessage(ChatColor.GREEN + "You have withdrawn " + quantity + " KR.");
	}


}
