package com.Furnesse.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.database.MySQLPlayer;

public class PlayerListeners implements Listener {

	Core plugin = Core.instance;

	private void registerPlayer(Player player) {
		String uuid = player.getUniqueId().toString();
		if (plugin.getSettings().usingMySQL) {
			MySQLPlayer.createPlayer(player.getUniqueId(), player);
			return;
		}

		if (!isRegistered(player) || hasChangedName(player)) {
			plugin.getFileManager().getConfig("players.yml").get().set("Players." + uuid + ".username",
					player.getName());
			
			// Setup rank for the player
			// Setup player's scoreboard prefrences (scoreboard enabled or not)
			// Setup chat for the player
			if (plugin.getSettings().usingChat) {
				plugin.getChatFormat().initFormat(player);
			}
		}
	}

	private boolean isRegistered(Player player) {
		if (plugin.getFileManager().getConfig("players.yml").get().contains("Players." + player.getUniqueId())) {
			return true;
		}
		return false;
	}

	private boolean hasChangedName(Player player) {
		if (!plugin.getFileManager().getConfig("players.yml").get()
				.getString("Players." + player.getUniqueId() + ".username").equals(player.getName())) {
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		registerPlayer(player);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		// Save player's rank
	}
}
