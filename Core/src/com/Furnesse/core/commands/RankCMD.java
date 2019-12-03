package com.Furnesse.core.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.rank.Rank;
import com.Furnesse.core.utils.Lang;

public class RankCMD implements CommandExecutor {

	Core plugin = Core.instance;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			// Player commands

			if (args.length == 0) {
				for (String message : plugin.getConfigs().getLangConfig().getStringList("rank.help")) {
					player.sendMessage(message);
				}
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {
					for (String message : plugin.getConfigs().getLangConfig().getStringList("rank.help")) {
						player.sendMessage(message);
					}
				}

				if (args[0].equalsIgnoreCase("list")) {
					player.sendMessage("§a§lRanks §7" + plugin.getRanks().getRanks().toString());
				}

			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("create")) {
					String name = args[1];
					
					plugin.getRanks().createRank(player, name);
				}

				if (args[0].equalsIgnoreCase("delete")) {
					String name = args[1];
					
					plugin.getRanks().removeRank(player, name);
				}
			}

			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("setprefix")) {
					String rank = args[1];
					String prefix = args[2];
					
					plugin.getRanks().setPrefix(sender, rank, prefix);
					
				}

				if (args[0].equalsIgnoreCase("setsuffix")) {
					String rank = args[1];
					String suffix = args[2];
					
					plugin.getRanks().setSuffix(sender, rank, suffix);
					
				}

				if (args[0].equalsIgnoreCase("addperm")) {
					Rank rank = plugin.getRanks().getRank(args[1]);
					String perm = args[2];

					if (rank != null) {
						if (!rank.getPermissions().contains(perm)) {
							plugin.getRanks().addRankPerm(player, rank, perm);
						} else {
							player.sendMessage(rank.getName() + " Already have access to the permission " + perm);
						}

					} else {
						player.sendMessage(args[1] + " is not a valid rank");
					}
				}

				if (args[0].equalsIgnoreCase("removeperm")) {
					Rank rank = plugin.getRanks().getRank(args[1]);
					String perm = args[2];

					if (rank != null) {
						if (rank.getPermissions().contains(perm)) {
							plugin.getRanks().removeRankPerm(player, rank, perm);
						} else {
							player.sendMessage(rank.getName() + " Dont have the permission " + perm);
						}
					} else {
						player.sendMessage(args[1] + " is not a valid rank");
					}
				}
			}

			if (args.length == 4) {
				if (args[0].equalsIgnoreCase("user")) {
					Player target = Bukkit.getPlayer(args[1]);
					String uuid = target.getUniqueId().toString();
					if (args[2].equalsIgnoreCase("set")) {
						Rank rankToSet = plugin.getRanks().getRank(args[3]);
						if (rankToSet != null) {
							// set rank to target
							plugin.getRanks().setRank(target, rankToSet);

							player.sendMessage(Lang.USER_SET_RANK);
						} else {
							player.sendMessage(args[3] + " is not a valid rank! see /rank list");
						}
					}

					if (args[2].equalsIgnoreCase("addperm")) {
						// add permission to the target
						String perm = args[3];
						List<String> perms = plugin.getConfigs().getPlayersConfig().getStringList("Players." + uuid + ".permissions");
						if (!perms.contains(perm) && !target.hasPermission(perm)) {
							plugin.getRanks().addPlayerPerm(target, perm);
						} else {
							player.sendMessage(target.getName() + " Already have access to this permission");
						}
					}

					if (args[2].equalsIgnoreCase("removeperm")) {
						// remove permission from the target
						String perm = args[3];
						List<String> perms = plugin.getConfigs().getPlayersConfig().getStringList("Players." + uuid + ".permissions");
						if (perms.contains(perm) && target.hasPermission(perm)) {
							plugin.getRanks().removePlayerPerm(target, perm);
						} else {
							player.sendMessage(target.getName() + " Dont have this permission");
						}
					}
				}
			}

			return true;
		}
		
		if (args.length == 0) {
			for (String message : plugin.getConfigs().getLangConfig().getStringList("rank.help")) {
				sender.sendMessage(message);
			}
		}

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("help")) {
				for (String message : plugin.getConfigs().getLangConfig().getStringList("rank.help")) {
					sender.sendMessage(message);
				}
			}

			if (args[0].equalsIgnoreCase("list")) {
				sender.sendMessage("§a§lRanks §7" + plugin.getRanks().getRanks().toString());
			}

		}

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("create")) {
				String name = args[1];
				plugin.getRanks().createRank(sender, name);
			}

			if (args[0].equalsIgnoreCase("delete")) {
				String name = args[1];
				plugin.getRanks().removeRank(sender, name);
			}
		}

		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("setprefix")) {
				String rank = args[1];
				String prefix = args[2];
				
				plugin.getRanks().setPrefix(sender, rank, prefix);
				
			}

			if (args[0].equalsIgnoreCase("setsuffix")) {
				String rank = args[1];
				String suffix = args[2];
				
				plugin.getRanks().setSuffix(sender, rank, suffix);
				
			}

			if (args[0].equalsIgnoreCase("addperm")) {
				Rank rank = plugin.getRanks().getRank(args[1]);
				String perm = args[2];

				if (rank != null) {
					if (!rank.getPermissions().contains(perm)) {
						plugin.getRanks().addRankPerm(sender, rank, perm);
						sender.sendMessage(Lang.RANK_PERM_ADDED);
					} else {
						sender.sendMessage(rank.getName() + " Already have access to the permission " + perm);
					}

				} else {
					sender.sendMessage(args[1] + " is not a valid rank");
				}
			}

			if (args[0].equalsIgnoreCase("removeperm")) {
				Rank rank = plugin.getRanks().getRank(args[1]);
				String perm = args[2];

				if (rank != null) {
					if (rank.getPermissions().contains(perm)) {
						plugin.getRanks().removeRankPerm(sender, rank, perm);
						sender.sendMessage(Lang.RANK_PERM_REMOVED);
					} else {
						sender.sendMessage(rank.getName() + " Dont have the permission " + perm);
					}
				} else {
					sender.sendMessage(args[1] + " is not a valid rank");
				}
			}
		}

		if (args.length == 4) {
			if (args[0].equalsIgnoreCase("user")) {
				Player target = Bukkit.getPlayer(args[1]);
				String uuid = target.getUniqueId().toString();
				if (args[2].equalsIgnoreCase("set")) {
					Rank rankToSet = plugin.getRanks().getRank(args[3]);
					if (rankToSet != null) {
						// set rank to target
						plugin.getRanks().setRank(target, rankToSet);

						sender.sendMessage(Lang.USER_SET_RANK);
					} else {
						sender.sendMessage(args[3] + " is not a valid rank! see /rank list");
					}
				}

				if (args[2].equalsIgnoreCase("addperm")) {
					// add permission to the target
					String perm = args[3];
					if (!target.hasPermission(perm)) {
						plugin.getRanks().addPlayerPerm(target, perm);
						
						sender.sendMessage(Lang.USER_ADDED_PERM);
					} else {
						sender.sendMessage(target.getName() + " Already have access to this permission");
					}
				}

				if (args[2].equalsIgnoreCase("removeperm")) {
					// remove permission from the target
					String perm = args[3];
					List<String> perms = plugin.getConfigs().getPlayersConfig().getStringList("Players." + uuid + ".permissions");
					if (perms.contains(perm) && target.hasPermission(perm)) {
						plugin.getRanks().removePlayerPerm(target, perm);
						
						sender.sendMessage(Lang.USER_REMOVED_PERM);
					} else {
						sender.sendMessage(target.getName() + " Dont have this permission");
					}
				}
			}
		}
		
		return true;
	}

}
