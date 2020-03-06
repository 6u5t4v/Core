package com.Furnesse.core.combatlog.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.Events.PlayerCombatLogEvent;
import com.Furnesse.core.Events.PlayerUntagEvent;

public class PlayerQuitListener implements Listener {
	Core plugin = Core.instance;

	public static String disconnectMsg = "";

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission("core.cl.bypass")) {
			return;
		}

		if (plugin.getCombatLog().taggedPlayers.containsKey(p.getUniqueId())) {
			if (!playerCombatLogged()) {
				PlayerUntagEvent event = new PlayerUntagEvent(p, PlayerUntagEvent.UntagCause.LAGOUT);
				Bukkit.getServer().getPluginManager().callEvent(event);
				return;
			}

			if (!plugin.getSettings().cl_logoutCommands.isEmpty() || plugin.getSettings().cl_logoutCommands != null) {
				for (String s : plugin.getSettings().cl_logoutCommands) {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", p.getName()));
				}
			}

			if (plugin.getSettings().cl_instantKill) {
				p.setHealth(0.0D);
				plugin.getCombatLog().killPlayers.add(p.getUniqueId());
			}

			PlayerCombatLogEvent combatlogEvent = new PlayerCombatLogEvent(plugin.getCombatLog(), p);
			Bukkit.getServer().getPluginManager().callEvent(combatlogEvent);
			PlayerUntagEvent untagEvent = new PlayerUntagEvent(p, PlayerUntagEvent.UntagCause.COMBATLOG);
			Bukkit.getServer().getPluginManager().callEvent(untagEvent);
			plugin.combatlogs++;
		}
	}

	public static void setDisconnectMsg(String msg) {
		disconnectMsg = msg;
	}

	public boolean playerCombatLogged() {
		if (!disconnectMsg.equalsIgnoreCase("disconnect.overflow")
				&& !disconnectMsg.equalsIgnoreCase("disconnect.genericreason")
				&& !disconnectMsg.equalsIgnoreCase("disconnect.timeout")) {
			return true;
		}
		return false;
	}
}
