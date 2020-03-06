package com.Furnesse.core.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.config.Message;

public class CombatLogCMD implements CommandExecutor {

	Core plugin = Core.instance;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.getLogger().severe(label + " is a player command only");
			return true;
		}

		Player p = (Player) sender;
		UUID uuid = p.getUniqueId();

		if (plugin.getCombatLog().taggedPlayers.containsKey(uuid) && plugin.getSettings().cl_tagDuration
				- plugin.getCombatLog().getCurrentTime()
				- Long.valueOf(((Long) plugin.getCombatLog().taggedPlayers.get(uuid)).longValue()).longValue() >= 1L) {
			p.sendMessage(Message.IN_COMBAT.getMessage().replace("%time%",
					String.valueOf(plugin.getCombatLog().tagTimeRemaining(uuid))));
		} else {
			p.sendMessage(Message.NOT_IN_COMBAT.getMessage());
		}

		return true;
	}

}
