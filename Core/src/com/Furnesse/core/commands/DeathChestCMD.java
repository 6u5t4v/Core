package com.Furnesse.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.deathchest.DeathChestManager;
import com.Furnesse.core.utils.Lang;

public class DeathChestCMD implements CommandExecutor {
	Core plugin = Core.instance;

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			// Player commands
			if (args.length == 0) {
				if(args[0].equalsIgnoreCase("help")) {
					if(player.hasPermission("core.dc.help")){
						for(String str : plugin.getConfigs().getLangConfig().getStringList("deathchest.help")) {
							player.sendMessage(Lang.chat(str));
						}
					}
				}
				return true;
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("clear")) {
					if (player.hasPermission("core.dc.clear")) {
//						plugin.getDeathChest().removeAll();
					} else {
						player.sendMessage(Lang.NO_PERMISSION);
					}
				}

				if (args[0].equalsIgnoreCase("reload")) {
					if (player.hasPermission("core.dc.load")) {
//						plugin.getDeathChest().loadDeathChests();
					} else {
						player.sendMessage(Lang.NO_PERMISSION);
					}
				}
				
				if(args[0].equalsIgnoreCase("gui")) {
//					if(player.hasPermission("core.dc.gui")) {
//						
//					}
					player.sendMessage("open gui");
				}
			}
			return true;
		}
		return false;
	}
}
