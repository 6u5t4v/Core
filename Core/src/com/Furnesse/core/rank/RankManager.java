package com.Furnesse.core.rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

public class RankManager {

	private Core plugin;

	public RankManager(Core plugin) {
		this.plugin = plugin;
	}

	private List<Rank> ranks = new ArrayList<Rank>();
	private Map<UUID, Rank> pRank = new HashMap<>();
//	private Permission perms = plugin.getPermissions();

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

	// RANK MANAGEMENT

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
		if (rank != null) {
			rank.setPrefix(newPrefix);
			plugin.getConfigs().getRanksConfig().set("Ranks." + rank.getName() + ".prefix", rank.getPrefix());
			plugin.getConfigs().saveConfigs();

			sender.sendMessage(
					Lang.SETPREFIX.replaceAll("%prefix%", rank.getPrefix()).replaceAll("%rank%", rank.getName()));
		} else {
			sender.sendMessage(name + " is not a valid rank");
			return;
		}
	}

	public void setSuffix(CommandSender sender, String name, String newSuffix) {
		Rank rank = getRank(name);
		if (rank != null) {
			rank.setSuffix(newSuffix);
			plugin.getConfigs().getRanksConfig().set("Ranks." + rank.getName() + ".suffix", rank.getSuffix());
			plugin.getConfigs().saveConfigs();

			sender.sendMessage(
					Lang.SETSUFFIX.replaceAll("%suffix%", rank.getSuffix()).replaceAll("%rank%", rank.getName()));
		} else {
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
			plugin.getLogger().severe("Make sure that there is only ONE default rank!");
		}

//		if (defaultRanks.size() < 1) {
//			createRank(, "default");
//		}

		if (defaultRanks.size() == 1) {
			return defaultRanks.get(0);
		}

		return null;
	}

	// PLAYER RANK

	public void setRank(Player player, Rank rank) {
		String uuid = player.getUniqueId().toString();

		plugin.getConfigs().getPlayersConfig().set("Players." + uuid + ".rank", rank.getName());
		plugin.getConfigs().saveConfigs();

		pRank.put(player.getUniqueId(), rank);
		player.sendMessage(Lang.RECEIVED_RANK.replace("%rank%", rank.getName()));

	}

	public Rank getPRank(Player player) {
		return pRank.get(player.getUniqueId());
	}

	public void loadRank(Player player) {
		UUID uuid = player.getUniqueId();
		String rankString = plugin.getConfigs().getPlayersConfig().getString("Players." + uuid.toString() + ".rank");
		Rank rank = (rankString == null) ? getDefaultRank() : getRank(rankString);

//		player.setDisplayName(rank.getPrefix() + player.getName() + rank.getSuffix());
//		player.setPlayerListName(rank.getPrefix() + player.getName() + rank.getSuffix());

		pRank.put(uuid, rank);
	}

	public void saveRank(Player player) {
		UUID uuid = player.getUniqueId();
		plugin.getConfigs().getPlayersConfig().set("Players." + uuid.toString() + ".rank", pRank.get(uuid).getName());
		plugin.getConfigs().saveConfigs();
		pRank.remove(uuid);
	}

	// PERMISSION FUN

//	HashMap<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();

	public void addRankPerm(CommandSender sender, Rank rank, String perm) {
		rank.getPermissions().add(perm);
//		Bukkit.getServer().getWorlds().forEach((world) -> perms.groupAdd(world, rank.getName(), perm));

		plugin.getConfigs().getRanksConfig().set("Ranks." + rank.getName() + ".permissions", rank.getPermissions());
		plugin.getConfigs().saveConfigs();

		sender.sendMessage(Lang.ADDED_PERM.replace("%perm%", perm).replace("%rank%", rank.getName()));
	}

	public void removeRankPerm(CommandSender sender, Rank rank, String perm) {
		rank.getPermissions().remove(perm);
//		Bukkit.getServer().getWorlds().forEach((world) -> perms.groupRemove(world, rank.getName(), perm));

		plugin.getConfigs().getRanksConfig().set("Ranks." + rank.getName() + ".permissions", rank.getPermissions());
		plugin.getConfigs().saveConfigs();

		sender.sendMessage(Lang.REMOVED_PERM.replace("%perm%", perm).replace("%rank%", rank.getName()));
	}

	public void addPlayerPerm(Player player, String perm) {
		String uuid = player.getUniqueId().toString();

		List<String> pPerms = plugin.getConfigs().getPlayersConfig().getStringList("Players." + uuid + ".permissions");
		pPerms.add(perm);
		plugin.getConfigs().getPlayersConfig().set("Players." + uuid + ".permissions", pPerms);
		plugin.getConfigs().saveConfigs();

//		perms.playerAdd(player, perm);
		player.sendMessage(Lang.RECEIVED_PERM.replace("%perm%", perm));
	}

	public void removePlayerPerm(Player player, String perm) {
		String uuid = player.getUniqueId().toString();

		plugin.getConfigs().getPlayersConfig().getStringList("Players." + uuid + ".permissions").remove(perm);
		plugin.getConfigs().saveConfigs();

//		perms.playerRemove(player, perm);

		player.sendMessage(Lang.LOSTED_PERM.replace("%perm%", perm));
	}

	public void loadPlayerPerms(Player player) {
		UUID uuid = player.getUniqueId();
		String rank = getPRank(player).getName();

		List<String> rankPerms = plugin.getConfigs().getRanksConfig().getStringList("Ranks." + rank + ".permissions");
		List<String> playerPerms = plugin.getConfigs().getPlayersConfig()
				.getStringList("Players." + uuid.toString() + ".permissions");
		List<String> inheritatedPerms = plugin.getConfigs().getRanksConfig()
				.getStringList("Ranks." + rank + ".inheritance");

		if (rankPerms != null) {
			for (String perm : rankPerms) {
				if (perm != null) {
//					Bukkit.getServer().getWorlds().forEach((world) -> perms.groupAdd(world, rank, perm));
				}
			}
		}

		if (inheritatedPerms != null) {
			for (String perm : inheritatedPerms) {
				if (perm != null) {
//					Bukkit.getServer().getWorlds().forEach((world) -> perms.groupAdd(world, rank, perm));
				}
			}
		}

		if (playerPerms != null) {
			for (String perm : playerPerms) {
				if (perm != null) {
//					perms.playerAdd(player, perm);
				}
			}
		}

		plugin.getLogger().info(player.getName() + "'s permissions has successfully been loaded");
	}
}
