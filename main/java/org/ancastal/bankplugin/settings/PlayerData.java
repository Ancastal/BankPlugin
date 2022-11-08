package org.ancastal.bankplugin.settings;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.*;

/**
 * A second example of storing/loading settings using a custom file for each player.
 */
@Getter
public final class PlayerData extends YamlConfig {

	private static final Map<UUID, PlayerData> playerData = new HashMap<>();

	private final String playerName;
	private final UUID uuid;

	private String tabListName;
	private List<String> bankList = new ArrayList<>();

	private PlayerData(String playerName, UUID uuid) {
		this.playerName = playerName;
		this.uuid = uuid;

		this.setHeader("PlayerConfigs");
		this.loadConfiguration(NO_DEFAULT, "players/" + uuid + ".yml");
		this.save();
	}

	@Override
	protected void onLoad() {
		this.tabListName = this.getString("Tablist_Name", this.playerName);
		this.bankList = this.getStringList("bankList");
	}

	@Override
	protected void onSave() {
		this.set("Tablist_Name", this.tabListName);
		this.set("bankList", this.bankList);
	}


	public void setTabListName(String tabListName) {
		this.tabListName = tabListName;

		this.save();
	}

	public void addSaveBankList(String newBank) {
		this.bankList.add(newBank);

		this.save();
	}

	public static PlayerData from(Player player) {
		UUID uuid = player.getUniqueId();
		PlayerData data = playerData.get(uuid);

		if (data == null) {
			data = new PlayerData(player.getName(), uuid);

			playerData.put(uuid, data);
		}

		return data;
	}

	public static void remove(Player player) {
		playerData.remove(player.getUniqueId());
	}
}
