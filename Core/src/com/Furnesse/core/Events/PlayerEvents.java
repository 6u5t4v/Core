package com.Furnesse.core.Events;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.database.MySQLPlayer;

public class PlayerEvents implements Listener {

	Core plugin = Core.instance;

	private void registerPlayer(Player player) {
		String uuid = player.getUniqueId().toString();
		if (plugin.usingMySQL) {
			MySQLPlayer.createPlayer(player.getUniqueId(), player);
			return;
		}

		if (!isRegistered(player) || hasChangedName(player)) {
			plugin.fileManager.getConfig("players.yml").get().set("Players." + uuid + ".username", player.getName());
			if (plugin.usingRanks) {
				plugin.fileManager.getConfig("players.yml").get().set("Players." + uuid + ".permissions",
						new ArrayList<String>());
			}

			plugin.fileManager.saveConfig("players.yml");
		}

	}

	private boolean isRegistered(Player player) {
		if (plugin.fileManager.getConfig("players.yml").get().contains("Players." + player.getUniqueId())) {
			return true;
		}
		return false;
	}

	private boolean hasChangedName(Player player) {
		String oldName = plugin.fileManager.getConfig("players.yml").get()
				.getString("Players." + player.getUniqueId() + ".username");
		if (!oldName.equals(player.getName())) {
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

//		registerPlayer(player);

//		if (plugin.usingRanks) {
//			plugin.getRanks().loadRank(player);
//			plugin.getRanks().loadPlayerPerms(player);
//		}

		if (plugin.usingSb) {
			plugin.getScoreboard().setScoreboard(player);
		}

		if (plugin.usingChat) {
			plugin.chatFormat.initFormat(player);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (plugin.usingSb)
			plugin.getScoreboard().removeScoreboard(player);

//		if (plugin.usingRanks)
//			plugin.getRanks().saveRank(player);

		if (plugin.usingChat)
			if (plugin.chatFormat.getPlayerFormat(player) != null)
				plugin.chatFormat.pFormat.remove(player.getName());
	}
}
