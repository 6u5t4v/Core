package com.Furnesse.core.combatlog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.config.Message;

public class PlayerToggleFlightListener implements Listener {

	Core plugin = Core.instance;

	@EventHandler
	public void onFlightToggle(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();

		if (plugin.getSettings().cl_disabledOnTag.contains("fly") && !p.hasPermission("core.cl.bypass")
				&& plugin.getCombatLog().taggedPlayers.containsKey(p.getUniqueId())) {
			p.setFlying(false);
			p.setAllowFlight(false);
			e.setCancelled(true);
			p.sendMessage(Message.FLIGHT_DISABLED.getMessage());
		}
	}
}
