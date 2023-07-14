package org.ancastal.banknative;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.ancastal.banknative.commands.AccountCommands;
import org.ancastal.banknative.commands.BankCommands;
import org.ancastal.banknative.db.Database;
import org.ancastal.banknative.listeners.ATM_Listener;
import org.ancastal.banknative.listeners.BankListener;
import org.ancastal.banknative.listeners.InterestListener;
import org.ancastal.banknative.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public final class BankNative extends SimplePlugin {
	private static BankNative instance;
	private Settings config;
	private Database database;
	private static Economy economy;
	private static Permission permission;
	private final Set<Class<? extends Listener>> registeredEvents = new HashSet<>();


	private boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}

	private boolean setupPermission() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		if (rsp == null) {
			return false;
		}
		permission = rsp.getProvider();
		return permission != null;
	}


	@Override
	protected void onPluginStart() {
		if (!setupEconomy() || !setupPermission()) {
			Common.logFramed("Disabled due to no Vault dependency found!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		//JDBC - Java Database Connectivity API
		this.config = new Settings(this);
		this.database = new Database(this);
		try {
			this.database.initializeDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not initialize database.");
		}

		this.getCommand("bank").setExecutor(new BankCommands(this, database));
		this.getCommand("account").setExecutor(new AccountCommands(this, database));
		registerEvents(new ATM_Listener(database, this));
		registerEvents(new InterestListener(database, config));
		registerEvents(new BankListener(database));
		instance = this;
	}

	public static Economy getEconomy() {
		return economy;
	}

	public static Permission getPermission() {
		return permission;
	}

	public static BankNative getInstance() {
		return instance;
	}

	public void registerEventIfNotRegistered(Class<? extends Listener> listenerClass) {
		if (!registeredEvents.contains(listenerClass)) {
			try {
				Listener listener;
				Constructor<? extends Listener> constructor;

				try {
					constructor = listenerClass.getDeclaredConstructor(); // Attempt to get a constructor with no params
					listener = constructor.newInstance();
				} catch (NoSuchMethodException e1) {
					try {
						// Attempt to get a constructor with JavaPlugin param
						constructor = listenerClass.getDeclaredConstructor(JavaPlugin.class);
						listener = constructor.newInstance(this);
					} catch (NoSuchMethodException e2) {
						// Attempt to get a constructor with Database and Settings params
						constructor = listenerClass.getDeclaredConstructor(Database.class, Settings.class);
						listener = constructor.newInstance(database, config);
					}
				}

				PluginManager pluginManager = getServer().getPluginManager();
				pluginManager.registerEvents(listener, this);
				registeredEvents.add(listenerClass);
			} catch (ReflectiveOperationException e) {
				getLogger().severe("Failed to register event: " + listenerClass.getSimpleName());
				e.printStackTrace();
			}
		}
	}


	public Settings getSettings() {
		return config;
	}


}
