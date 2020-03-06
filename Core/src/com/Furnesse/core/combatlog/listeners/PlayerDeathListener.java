package com.Furnesse.core.combatlog.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.Events.PlayerUntagEvent;

public class PlayerDeathListener implements Listener {

	Core plugin = Core.instance;

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent e) {
		Player p = e.getEntity().getPlayer();
		if (plugin.getCombatLog().taggedPlayers.containsKey(p.getUniqueId())) {
			PlayerUntagEvent event = new PlayerUntagEvent(p, PlayerUntagEvent.UntagCause.DEATH);
			Bukkit.getServer().getPluginManager().callEvent(event);
		}

		if (plugin.getCombatLog().killPlayers.contains(p.getUniqueId())) {
			plugin.getCombatLog().killPlayers.remove(p.getUniqueId());
		}
	}

}
