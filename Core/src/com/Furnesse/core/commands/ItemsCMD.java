package com.Furnesse.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.Core;
import com.Furnesse.core.config.Message;
import com.Furnesse.core.utils.Lang;

public class ItemsCMD implements CommandExecutor {

	Core plugin = Core.instance;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration langFile = plugin.getFileManager().getConfig("lang.yml").get();

		if (sender instanceof Player) {
			Player player = (Player) sender;
			// Player commands
			if (args.length == 0) {
				// Open the gui containing all custom items
			}

			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("help")) {
					for (String str : langFile.getStringList("customitems.help")) {
						player.sendMessage(Lang.chat(str));
					}
				}

				if (args[0].equalsIgnoreCase("list")) {
					player.sendMessage(Lang
							.chat("&a&lAvailable Items: &7" + plugin.getCustomItems().customitems.keySet().toString()));
				}

				// /citems give <item> [player] [amount]
				if (args[0].equalsIgnoreCase("give")) {
					ItemStack item = plugin.getCustomItems().getItem(args[1]);
					if (item != null) {
						Player target = player;
						int amount = 1;

						if (args.length == 3) {
							target = Bukkit.getPlayer(args[2]);
							if (target == null) {
								player.sendMessage(Message.INVALID_PLAYER.getChatMessage().replace("%player%", args[2]));
								return true;
							}
						}

						if (args.length == 4) {
							amount = Integer.valueOf(args[3]);
						}

						if (!target.getInventory().addItem(new ItemStack[] { item }).isEmpty()) {
							player.sendMessage(Message.NO_SPACE_IN_PLAYER_INVENTORY.getChatMessage()
									.replace("%player%", target.getName()).replace("%item%", args[1]));
							return true;
						}

						player.sendMessage(
								Message.ITEMS_SUCCESFULL_RECEIVED.getChatMessage().replace("%player%", target.getName())
										.replace("%amount%", String.valueOf(amount)).replace("%item%", args[2]));
						target.sendMessage(
								Message.ITEMS_PLAYER_RECEIVED.getChatMessage().replace("%player%", target.getName())
										.replace("%amount%", String.valueOf(amount)).replace("%item%", args[2]));

					} else {
						player.sendMessage(Message.ITEMS_INVALID_ITEM.getChatMessage().replace("%item%", args[2]));
					}

				}

			}

			return true;
		}

		if (args.length == 0) {
			// Open the gui containing all custom items
		}

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("help")) {

				for (String str : langFile.getStringList("customitems.help")) {
					sender.sendMessage(Lang.chat(str));
				}
			}

			if (args[0].equalsIgnoreCase("list")) {

				sender.sendMessage(
						Lang.chat("&a&lAvailable Items: &7" + plugin.getCustomItems().customitems.keySet().toString()));
			}

			if (args[0].equalsIgnoreCase("give")) {
				ItemStack item = plugin.getCustomItems().getItem(args[1]);
				if (item != null) {
					Player target = null;
					int amount = 1;

					if (args.length < 3) {
						sender.sendMessage("usage: /citems give <item> <player> [amount]");
					}

					if (args.length == 3) {
						target = Bukkit.getPlayer(args[2]);
						if (target == null) {
							sender.sendMessage(Message.INVALID_PLAYER.getChatMessage());
							return true;
						}
					}

					if (args.length == 4) {
						amount = Integer.valueOf(args[3]);
					}

					if (!target.getInventory().addItem(new ItemStack[] { item }).isEmpty()) {
						target.sendMessage(Message.FULL_INVENTORY.getChatMessage());
						return true;
					}

					target.sendMessage(
							Message.ITEMS_SUCCESFULL_RECEIVED.getChatMessage().replace("%player%", target.getName())
									.replace("%amount%", String.valueOf(amount)).replace("%item%", args[2]));

				} else {
					sender.sendMessage(Message.ITEMS_INVALID_ITEM.getChatMessage().replace("%item%", args[2]));
				}
			}
		}
		return true;
	}

}
