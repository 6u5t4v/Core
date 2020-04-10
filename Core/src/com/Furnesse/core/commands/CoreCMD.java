package com.Furnesse.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.config.Message;
import com.Furnesse.core.mechanics.Cooldown;
import com.Furnesse.core.sidebar.Sidebar;
import com.Furnesse.core.utils.Lang;

public class CoreCMD implements CommandExecutor {

	Core plugin = Core.instance;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration langFile = plugin.getFileManager().getConfig("lang.yml").get();

		if (sender instanceof Player) {
			Player player = (Player) sender;
			// Player commands

			if (args.length == 0) {
				if (player.hasPermission("core.help")) {
					for (String message : langFile.getStringList("help")) {
						player.sendMessage(Lang.chat(message));
					}

				} else {
					player.sendMessage(Message.NO_PERMISSION.getChatMessage());
				}
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {
					if (player.hasPermission("core.help")) {
						for (String message : langFile.getStringList("help")) {
							player.sendMessage(Lang.chat(message));
						}
					} else {
						player.sendMessage(Message.NO_PERMISSION.getChatMessage());
					}
				}

				if (args[0].equalsIgnoreCase("reload")) {
					if (player.hasPermission("core.reload")) {
						plugin.reloadPlugin();
						sender.sendMessage(Message.RELOADED.getChatMessage());
					} else {
						player.sendMessage(Message.NO_PERMISSION.getChatMessage());
					}
				}

				if (args[0].equalsIgnoreCase("tasks")) {
					if (player.hasPermission("core.showtasks")) {
						player.sendMessage(Lang.chat("&aTasks &7") + plugin.getCommandCD().cmdCooldown.keySet().toString());
					} else {
						player.sendMessage(Message.NO_PERMISSION.getChatMessage());
					}
				}
			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("sb")) {
					if (player.hasPermission("core.sb.toggle")) {
						if (args[1].equalsIgnoreCase("toggle")) {
							Sidebar.toggleSidebar(player.getUniqueId());
						}
					} else {
						player.sendMessage(Message.NO_PERMISSION.getChatMessage());
					}
				}

				if (args[0].equalsIgnoreCase("tasks")) {
					if (player.hasPermission("core.disabletasks")) {
						if (args[1].equalsIgnoreCase("disable")) {
							player.sendMessage("Usage: /core tasks disable <task>");
						}
					} else {
						player.sendMessage(Message.NO_PERMISSION.getChatMessage());
					}

					if (player.hasPermission("core.enabletasks")) {
						if (args[1].equalsIgnoreCase("enable")) {
							player.sendMessage("Usage: /core tasks enable <task>");
						}
					} else {
						player.sendMessage(Message.NO_PERMISSION.getChatMessage());
					}
				}
			}

			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("tasks")) {
					if (player.hasPermission("core.disabletasks")) {
						if (args[1].equalsIgnoreCase("disable")) {
							Cooldown cd = plugin.getCommandCD().getCooldown(args[2]);
							if (cd == null) {
								player.sendMessage(Lang.chat("&cNo task with name " + args[2] + " exists"));
								return true;
							}
							
							if (cd.isCmdOnCooldown()) {
								cd.stopTask();
								player.sendMessage(Lang.chat("&a" + args[2] + " &ahas been disabled"));
							} else {
								player.sendMessage(Lang.chat("&c" + args[2] + " &cis already disabled"));
							}
						}
					} else {
						player.sendMessage(Message.NO_PERMISSION.getChatMessage());
					}

					if (player.hasPermission("core.enabletasks")) {
						if (args[1].equalsIgnoreCase("enable")) {
							Cooldown cd = plugin.getCommandCD().getCooldown(args[2]);
							if (cd == null) {
								player.sendMessage(Lang.chat("&cNo task with name " + args[2] + " exists"));
								return true;
							}
							if (!cd.isCmdOnCooldown()) {
								cd.startTimer(true);
								player.sendMessage(Lang.chat("&a" + args[2] + " &ahas been enabled"));
							} else {
								player.sendMessage(Lang.chat("&c" + args[2] + " &cis already enabled"));
							}
						}
					} else {
						player.sendMessage(Message.NO_PERMISSION.getChatMessage());
					}
				}
			}
		}

		if (args.length == 0) {
			for (String message : langFile.getStringList("help")) {
				sender.sendMessage(Lang.chat(message));

			}
		}

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("help")) {
				for (String message : langFile.getStringList("help")) {
					sender.sendMessage(message);
				}
			}

			if (args[0].equalsIgnoreCase("reload")) {
				plugin.reloadPlugin();
				sender.sendMessage(Message.RELOADED.getChatMessage());
			}
		}

		return false;
	}
}
