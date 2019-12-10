package com.Furnesse.core.commands;

import java.util.ArrayList;
import java.util.List;

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

				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("help")) {

						for (String str : plugin.getConfigs().getLangConfig().getStringList("citems.help")) {
							player.sendMessage(Lang.chat(str));
						}

					}

					if (args[0].equalsIgnoreCase("list")) {
						List<String> cItems = new ArrayList<String>();
						for (CItem cItem : plugin.cItemMan.customItems) {
							cItems.add(cItem.getName());
						}

						player.sendMessage(Lang.chat("&a&lAvailable Items: &7" + cItems.toString()));
					}

					if (args[0].equalsIgnoreCase("give")) {
						Player target = Bukkit.getPlayer(args[1]);

						if (target == null) {
							player.sendMessage(Lang.INVALID_PLAYER.replace("%player%", args[1]));
						}

						if (plugin.cItemMan.getCItem(args[2]) != null) {
							CItem cItem = plugin.cItemMan.getCItem(args[2]);
							if (args.length == 4) {
								int amount = Integer.parseInt(args[3]);
								plugin.cItemMan.giveCItem(sender, target, cItem, amount);
								player.sendMessage(Lang.ITEMS_SUCCESFULL_RECEIVED.replace("%player%",
										target.getName().replace("%amount%", String.valueOf(amount)).replace("%item%",
												cItem.getName())));
							}

						} else {
							player.sendMessage(Lang.ITEMS_INVALID_ITEM.replace("%item%", args[2]));
						}
					}

				}
			} else {
				player.sendMessage(Lang.NO_PERMISSION);
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
