package com.Furnesse.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;

public class RankCMD implements CommandExecutor {

	Core plugin = Core.instance;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			// Player commands

			if (args.length == 0) {
				for (String message : plugin.fileManager.getConfig("lang.yml").get().getStringList("rank.help")) {
					player.sendMessage(message);
				}
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {
					for (String message : plugin.fileManager.getConfig("lang.yml").get().getStringList("rank.help")) {
						player.sendMessage(message);
					}
				}

				if (args[0].equalsIgnoreCase("list")) {
					player.sendMessage("§a§lRanks §7" + plugin.getRanks().getRanks().toString());
				}

			}
		}
		return true;
	}

}
