package com.Furnesse.core.combatlog.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.Events.PlayerUntagEvent;

public class PlayerKickListener implements Listener {
	Core plugin = Core.instance;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onKickEvent(PlayerKickEvent e) {
		Player player = e.getPlayer();
		if (plugin.getCombatLog().taggedPlayers.containsKey(player.getUniqueId())
				&& !e.getReason().toLowerCase().contains("spam")) {
			PlayerUntagEvent event = new PlayerUntagEvent(player, PlayerUntagEvent.UntagCause.KICK);
			Bukkit.getServer().getPluginManager().callEvent(event);
		}
	}

}
