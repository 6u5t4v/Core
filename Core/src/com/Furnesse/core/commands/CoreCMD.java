package com.Furnesse.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;
import com.Furnesse.core.utils.Scoreboard;

public class CoreCMD implements CommandExecutor {

	Core plugin = Core.instance;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			// Player commands
			if (player.hasPermission(cmd.getPermission())) {
				if (args.length == 0) {
					for (String message : plugin.getConfigs().getLangConfig().getStringList("help")) {
						player.sendMessage(message);
					}
				}

				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("help")) {
						for (String message : plugin.getConfigs().getLangConfig().getStringList("help")) {
							player.sendMessage(message);
						}
					}

					if (args[0].equalsIgnoreCase("reload")) {
						plugin.getConfigs().reloadConfigs(player);
					}
				}
			} else {
				player.sendMessage(Lang.NO_PERMISSION);
			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("sb")) {
					if (player.hasPermission("core.scoreboard")) {
						if (args[1].equalsIgnoreCase("scoreboard")) {
							player.sendMessage("/core sb update");
							player.sendMessage("/core sb toggle");
						}

//						if (args[1].equalsIgnoreCase("update")) {
//							Scoreboard.updateScoreboard(player);
//						}

						if (args[1].equalsIgnoreCase("toggle")) {
							Scoreboard.toggleScoreboard(player);
						}
					} else {
						player.sendMessage(Lang.NO_PERMISSION);
					}
				}

				if (args[0].equalsIgnoreCase("dc")) {

					if (args[1].equalsIgnoreCase("clear")) {
						if (player.hasPermission("core.deathchests.clear")) {
							plugin.getDeathChest().removeAll();
						} else {
							player.sendMessage(Lang.NO_PERMISSION);
						}
					}

					if (args[1].equalsIgnoreCase("reload")) {
						if (player.hasPermission("core.deathchests.reload")) {
							plugin.getDeathChest().loadDeathChests();
						} else {
							player.sendMessage(Lang.NO_PERMISSION);
						}
					}
				}

			}
			return true;

		}

		if (args.length == 0) {
			for (String message : plugin.getConfigs().getLangConfig().getStringList("help")) {
				sender.sendMessage(message);
			}
		}

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("help")) {
				for (String message : plugin.getConfigs().getLangConfig().getStringList("help")) {
					sender.sendMessage(message);
				}
			}

			if (args[0].equalsIgnoreCase("reload")) {
				plugin.getConfigs().reloadConfigs(sender);
			}
		}

		if (args.length == 2) {
			if (args[1].equalsIgnoreCase("clear")) {

				plugin.getDeathChest().removeAll();
			}

			if (args[1].equalsIgnoreCase("reload")) {

				plugin.getDeathChest().loadDeathChests();

			}
		}
		return true;
	}

}
