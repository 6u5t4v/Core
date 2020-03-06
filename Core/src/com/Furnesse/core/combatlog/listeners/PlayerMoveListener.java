package com.Furnesse.core.combatlog.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.Events.PlayerUntagEvent;

public class PlayerMoveListener implements Listener {
	Core plugin = Core.instance;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();

		if (plugin.getSettings().cl_removeTagInSafezone) {
			if (plugin.getCombatLog().taggedPlayers.containsKey(p.getUniqueId())) {
				if (plugin.landsHook) {
					if (plugin.landsAPI.isClaimed(loc) && !plugin.landsAPI.getLand(loc).isInWar()) {
						PlayerUntagEvent event = new PlayerUntagEvent(p, PlayerUntagEvent.UntagCause.SAFE_AREA);
						Bukkit.getServer().getPluginManager().callEvent(event);
						return;
					}
				}
			}
		}
	}
}
