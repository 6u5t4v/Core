package com.Furnesse.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.customitems.CItem;
import com.Furnesse.core.utils.Lang;

public class ItemsCMD implements CommandExecutor {

	Core plugin = Core.instance;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			// Player commands
			if (player.hasPermission(cmd.getPermission())) {
				if (args.length == 0) {
					// Open the gui containing all custom items
				}
			} else {
				player.sendMessage(Lang.NO_PERMISSION);
			}

			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("help")) {
					for (String message : plugin.getConfigs().getLangConfig().getStringList("help")) {
						player.sendMessage(message);
					}
				}

				if (args[0].equalsIgnoreCase("list")) {
					player.sendMessage(Lang.chat("&aAvailable Items: &7" + plugin.cItemMan.customItems.toString()));
				}

				if (args[0].equalsIgnoreCase("give")) {
					if (args.length == 1 || args.length == 2) {
						player.sendMessage("Usage: /items give <player> <Item> [amount]");
						return true;
					}

					Player target = Bukkit.getPlayer(args[1]);
					if (target == null) {
						player.sendMessage(args[1] + " is not online");
					}

					if (plugin.cItemMan.getCItem(args[2]) != null) {
						CItem cItem = plugin.cItemMan.getCItem(args[2]);
						if (args.length == 4) {
							int amount = Integer.parseInt(args[3]);
							plugin.cItemMan.giveCItem(sender, target, cItem, amount);
							return true;
						}

						int amount = 64;
						plugin.cItemMan.giveCItem(sender, target, cItem, amount);

					} else {
						player.sendMessage(args[2] + " is not a valid item, try /items list");
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
		}

		if (args.length == 2) {
			if (args[1].equalsIgnoreCase("give")) {

			}
		}
		return true;
	}

}
