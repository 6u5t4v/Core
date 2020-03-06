package com.Furnesse.core.combatlog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.config.Message;

public class PlayerJoinListener implements Listener {
	Core plugin = Core.instance;

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (plugin.getCombatLog().killPlayers.contains(p.getUniqueId())) {
			p.sendMessage(Message.KILLED.getMessage());
			plugin.getCombatLog().killPlayers.remove(p.getUniqueId());
		}
	}
}
