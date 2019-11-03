package com.Furnesse.core.rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

public class RankManager {

	private Core plugin;

	public RankManager(Core plugin) {
		this.plugin = plugin;
	}

	private List<Rank> ranks = new ArrayList<Rank>();

	public void loadRanks() {
		ranks.clear();
		ConfigurationSection availableRanks = plugin.getConfigs().getRanksConfig().getConfigurationSection("Ranks");
		if (availableRanks != null) {
			for (String rank : availableRanks.getKeys(false)) {
				if (rank != null) {
					String name = rank.toString();
					String prefix = plugin.getConfigs().getRanksConfig().getString("Ranks." + rank + ".prefix");
					String suffix = plugin.getConfigs().getRanksConfig().getString("Ranks." + rank + ".suffix");
					boolean defaultRank = plugin.getConfigs().getRanksConfig().getBoolean("Ranks." + rank + ".default");
					List<String> perms = plugin.getConfigs().getRanksConfig()
							.getStringList("Ranks." + rank + ".permissions");
					List<String> inherits = plugin.getConfigs().getRanksConfig()
							.getStringList("Ranks." + rank + ".inheritance");

					Rank currentRank = new Rank(name, prefix, suffix, defaultRank, perms, inherits);
					ranks.add(currentRank);
				}
			}
		}
	}

	public List<Rank> getRanks() {
		return ranks;
	}

	public Rank getRank(String name) {
		for (Rank rank : ranks) {
			if (rank.getName().equals(name)) {
				return rank;
			}
		}
		return null;
	}

	public void createRank(CommandSender sender, String name) {
		String prefix = "";
		String suffix = "";
		boolean defaultRank = false;
		List<String> perms = new ArrayList<String>();
		List<String> inherits = new ArrayList<String>();
		if (getRank(name) == null) {
			plugin.getConfigs().getRanksConfig().set("Ranks." + name + ".prefix", prefix);
			plugin.getConfigs().getRanksConfig().set("Ranks." + name + ".suffix", suffix);
			plugin.getConfigs().getRanksConfig().set("Ranks." + name + ".default", defaultRank);
			plugin.getConfigs().getRanksConfig().set("Ranks." + name + ".permissions", perms);
			plugin.getConfigs().getRanksConfig().set("Ranks." + name + ".inheritance", inherits);
			plugin.getConfigs().saveConfigs();

			Rank rank = new Rank(name, prefix, suffix, defaultRank, perms, inherits);
			ranks.add(rank);
			sender.sendMessage(Lang.CREATED_RANK.replaceAll("%rank%", rank.getName()));
		}
	}

	public void removeRank(CommandSender sender, String name) {
		Rank rank = getRank(name);
		if (rank != null) {
			plugin.getConfigs().getRanksConfig().set("Ranks." + name, null);
			plugin.getConfigs().saveConfigs();

			ranks.remove(rank);
			sender.sendMessage(Lang.DELETED_RANK);
		} else {
			sender.sendMessage(name + " This rank doesnt exist");
			return;
		}
	}

	public void setPrefix(CommandSender sender, String name, String newPrefix) {
		Rank rank = getRank(name);
		if(rank != null) {
			rank.setPrefix(newPrefix);
			plugin.getConfigs().getRanksConfig().set("Ranks." + rank.getName() + ".prefix", rank.getPrefix());
			plugin.getConfigs().saveConfigs();
			
			sender.sendMessage(Lang.SETPREFIX.replaceAll("%prefix%", rank.getPrefix()).replaceAll("%rank%", rank.getName()));
		}else {
			sender.sendMessage(name + " is not a valid rank");
			return;
		}
	}
	
	public void setSuffix(CommandSender sender, String name, String newSuffix) {
		Rank rank = getRank(name);
		if(rank != null) {
			rank.setSuffix(newSuffix);
			plugin.getConfigs().getRanksConfig().set("Ranks." + rank.getName() + ".suffix", rank.getSuffix());
			plugin.getConfigs().saveConfigs();
			
			sender.sendMessage(Lang.SETSUFFIX.replaceAll("%suffix%", rank.getSuffix()).replaceAll("%rank%", rank.getName()));
		}else {
			sender.sendMessage(name + " is not a valid rank");
			return;
		}
	}
	
	public Rank getDefaultRank() {
		List<Rank> defaultRanks = new ArrayList<Rank>();
		for (Rank rank : ranks) {
			if (rank.isDefaultRank()) {
				defaultRanks.add(rank);
			}
		}

		if (defaultRanks.size() > 1 || defaultRanks.size() < 1) {
			plugin.getLogger().severe("Make sure that there is onle ONE default rank!");
		}

		if (defaultRanks.size() == 1) {
			return defaultRanks.get(0);
		}

		return null;
	}

	public void setRank(Player player, Rank rank) {
		String uuid = player.getUniqueId().toString();
		if (rank != null) {
			plugin.getConfigs().getPlayersConfig().set("Players." + uuid + ".rank", rank.getName());
			plugin.getConfigs().saveConfigs();

			player.sendMessage(Lang.RECEIVED_RANK.replace("%rank%", rank.getName()));
		}
	}

	public Rank getPlayerRank(Player player) {
		String uuid = player.getUniqueId().toString();
		Rank rank = getRank(plugin.getConfigs().getPlayersConfig().getString("Players." + uuid + ".rank"));
		if(rank != null) {
			return rank;
		}
		return null;
	}
	
	// PERMISSION FUN

	HashMap<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();

	public void addRankPerm(CommandSender sender, Rank rank, String perm) {
		rank.getPermissions().add(perm);

		plugin.getConfigs().getRanksConfig().set("Ranks." + rank.getName() + ".permissions", rank.getPermissions());
		plugin.getConfigs().saveConfigs();

		if (Bukkit.getOnlinePlayers().size() >= 1) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				Rank pRank = getRank(
						plugin.getConfigs().getPlayersConfig().getString("Players." + p.getUniqueId() + ".rank"));
				if (pRank.equals(rank)) {
					perms.get(p.getUniqueId()).setPermission(perm, true);
				}
			}
		}

		sender.sendMessage(Lang.ADDED_PERM.replace("%perm%", perm).replace("%rank%", rank.getName()));
	}

	public void removeRankPerm(CommandSender sender, Rank rank, String perm) {
		rank.getPermissions().remove(perm);

		plugin.getConfigs().getRanksConfig().set("Ranks." + rank.getName() + ".permissions", rank.getPermissions());
		plugin.getConfigs().saveConfigs();

		if (Bukkit.getOnlinePlayers().size() >= 1) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				Rank pRank = getRank(
						plugin.getConfigs().getPlayersConfig().getString("Players." + p.getUniqueId() + ".rank"));
				if (pRank.equals(rank)) {
					perms.get(p.getUniqueId()).unsetPermission(perm);
				}
			}
		}

		sender.sendMessage(Lang.REMOVED_PERM.replace("%perm%", perm).replace("%rank%", rank.getName()));
	}

	public void addPlayerPerm(Player player, String perm) {
		String uuid = player.getUniqueId().toString();
		
		if(!plugin.getConfigs().getPlayersConfig().contains("Players." + uuid + ".permissions")) {
			plugin.getConfigs().getPlayersConfig().set("Players." + uuid + ".permissions", new ArrayList<String>());
			plugin.getConfigs().saveConfigs();
		}
		List<String> pPerms = plugin.getConfigs().getPlayersConfig().getStringList("Players." + uuid + ".permissions");
		pPerms.add(perm);
		plugin.getConfigs().getPlayersConfig().set("Players." + uuid + ".permissions", pPerms);
		plugin.getConfigs().saveConfigs();

		perms.get(player.getUniqueId()).setPermission(perm, true);
		player.sendMessage(Lang.RECEIVED_PERM.replace("%perm%", perm));
	}

	public void removePlayerPerm(Player player, String perm) {
		String uuid = player.getUniqueId().toString();

		plugin.getConfigs().getPlayersConfig().getStringList("Players." + uuid + ".permissions").remove(perm);
		plugin.getConfigs().saveConfigs();

		perms.get(player.getUniqueId()).unsetPermission(perm);
		player.sendMessage(Lang.LOSTED_PERM.replace("%perm%", perm));
	}

	public void loadPlayerPerms(Player player) {
		String uuid = player.getUniqueId().toString();
		String rank = getPlayerRank(player).getName();

		List<String> rankPerms = plugin.getConfigs().getRanksConfig().getStringList("Ranks." + rank + ".permissions");
		List<String> playerPerms = plugin.getConfigs().getPlayersConfig().getStringList("Players." + uuid + ".permissions");
		List<String> inheritatedPerms = plugin.getConfigs().getRanksConfig().getStringList("Ranks." + rank + ".inheritance");

		PermissionAttachment attachment = player.addAttachment(plugin);
		perms.put(player.getUniqueId(), attachment);
		
		if (rankPerms != null) {
			for (String perm : rankPerms) {
				if (perm != null) {
					perms.get(player.getUniqueId()).setPermission(perm, true);
				}
			}
		}

		if(inheritatedPerms != null) {
			for(String perm : inheritatedPerms) {
				if(perm != null) {
					perms.get(player.getUniqueId()).setPermission(perm, true);
				}
			}
		}
		
		if (playerPerms != null) {
			for (String perm : playerPerms) {
				if (perm != null) {
					perms.get(player.getUniqueId()).setPermission(perm, true);
				}
			}
		}

		plugin.getLogger().info(player.getName() + "'s permissions has successfully been loaded");
	}
}
