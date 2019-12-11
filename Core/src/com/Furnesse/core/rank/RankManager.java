package com.Furnesse.core.rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import com.Furnesse.core.Core;

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
		ConfigurationSection availableRanks = plugin.fileManager.getConfig("ranks.yml").get().getConfigurationSection("Ranks");
		if (availableRanks != null) {
			for (String rank : availableRanks.getKeys(false)) {
				if (rank != null) {
					String name = rank.toString();
					String prefix = plugin.fileManager.getConfig("ranks.yml").get().getString("Ranks." + rank + ".prefix");
					String suffix = plugin.fileManager.getConfig("ranks.yml").get().getString("Ranks." + rank + ".suffix");
					boolean defaultRank = plugin.fileManager.getConfig("ranks.yml").get().getBoolean("Ranks." + rank + ".default");
					List<String> perms = plugin.fileManager.getConfig("ranks.yml").get()
							.getStringList("Ranks." + rank + ".permissions");
					List<String> inherits = plugin.fileManager.getConfig("ranks.yml").get()
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
}
