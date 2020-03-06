package com.Furnesse.core.combatlog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.config.Message;

public class PlayerCommandPreProcessListener implements Listener {

	Core plugin = Core.instance;

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerAttemptCommandInCombat(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String cmd = e.getMessage();

		if (plugin.getCombatLog().taggedPlayers.containsKey(p.getUniqueId()) && !cmd.equalsIgnoreCase("tag")) {
			cmd = cmd.substring(1).split(" ")[0];
			if (plugin.getSettings().cl_blockedCommands.contains(cmd)) {
				e.setCancelled(true);
				p.sendMessage(Message.COMMAND_BLOCKED.getMessage());
			}
		}
	}
}
