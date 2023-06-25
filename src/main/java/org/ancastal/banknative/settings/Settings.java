package org.ancastal.banknative.settings;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Settings {

	private Plugin plugin;
	private static FileConfiguration config;

	public Settings(Plugin plugin) {
		this.plugin = plugin;
		// create the config file if it doesn't exist
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			plugin.saveResource("config.yml", false);
		}
		// load the config file
		this.config = YamlConfiguration.loadConfiguration(configFile);
	}

	public String getString(String path) {
		return config.getString(path);
	}

	public int getInt(String path) {
		return config.getInt(path);
	}

	public String getAddress() {
		return config.getString("connection-details.address");
	}

	public String getDatabase() {
		return config.getString("connection-details.database");
	}

	public String getUsername() {
		return config.getString("connection-details.username");
	}

	public String getPassword() {
		return config.getString("connection-details.password");
	}


	public int getATMS() {
		return config.getInt("ATMs");
	}

	public static String getCurrency() {
		return config.getString("currency");
	}

	public boolean getBoolean(String path) {
		return config.getBoolean(path);
	}
}
