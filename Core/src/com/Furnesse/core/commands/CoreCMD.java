package com.Furnesse.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

public class CoreCMD implements CommandExecutor {

	Core plugin = Core.instance;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			// Player commands

			if (args.length == 0) {
				if (player.hasPermission("core.help") || player.isOp()) {
					for (String message : plugin.getConfigs().getLangConfig().getStringList("help")) {
						player.sendMessage(message);
					}
				} else {
					player.sendMessage(Lang.NO_PERMISSION);
				}
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {
					if (player.hasPermission("core.help")) {
						for (String message : plugin.getConfigs().getLangConfig().getStringList("help")) {
							player.sendMessage(message);
						}
					} else {
						player.sendMessage(Lang.NO_PERMISSION);
					}
				}

				if (args[0].equalsIgnoreCase("reload")) {
					if (player.hasPermission("core.reload")) {
						plugin.getConfigs().reloadConfigs(player);
					} else {
						player.sendMessage(Lang.NO_PERMISSION);
					}
				}
			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("sb")) {
					if (player.hasPermission("core.sb.toggle")) {
						if (args[1].equalsIgnoreCase("toggle")) {
							plugin.getScoreboard().toggleScoreboard(player);
						}
					} else {
						player.sendMessage(Lang.NO_PERMISSION);
					}
				}

				if (args[0].equalsIgnoreCase("dc")) {
					if (args[1].equalsIgnoreCase("clear")) {
						if (player.hasPermission("core.dc.clear")) {
//							plugin.getDeathChest().removeAll();
						} else {
							player.sendMessage(Lang.NO_PERMISSION);
						}
					}

					if (args[1].equalsIgnoreCase("reload")) {
						if (player.hasPermission("core.dc.load")) {
//							plugin.getDeathChest().loadDeathChests();
						} else {
							player.sendMessage(Lang.NO_PERMISSION);
						}
					}
				}
			}
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
			if (args[0].equalsIgnoreCase("dc")) {
				if (args[1].equalsIgnoreCase("clear")) {
//				plugin.getDeathChest().removeAll();
				}

				if (args[1].equalsIgnoreCase("reload")) {
//				plugin.getDeathChest().loadDeathChests();

				}
			}

		}

		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("dc")) {
//				if (args[1].equalsIgnoreCase("debug")) {
//					Player target = Bukkit.getPlayer(args[2]);
//					if (target == null || !target.isOnline()) {
//						sender.sendMessage(
//								"Cant seem to find player §c" + args[2] + "§7 make sure the player is online");
//						return true;
//					}
//
//					List<ItemStack> drops = new ArrayList<>();
//					for (ItemStack item : target.getInventory().getContents()) {
//						if (item == null) {
//							continue;
//						}
//
//						if (item.getType() == Material.AIR) {
//							continue;
//						}
//
//						Debug.Log("current item: " + item.getType());
//						drops.add(item);
//					}
//
//					Debug.Log("items: " + drops.size());
//					plugin.getDeathChest().spawnDeathChest(target, drops, target.getWorld());
//
//				}
				return true;
			}
		}

		return false;
	}
}
