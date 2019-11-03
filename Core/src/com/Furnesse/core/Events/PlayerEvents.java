package com.Furnesse.core.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Scoreboard;

public class PlayerEvents implements Listener {

	Core plugin = Core.instance;

	private void registerPlayer(Player player) {
		String uuid = player.getUniqueId().toString();

		if (!isRegistered(player)) {
			plugin.getConfigs().getPlayersConfig().set("Players." + uuid + ".username", player.getName());
			plugin.getRanks().setRank(player, plugin.getRanks().getDefaultRank());
			plugin.getConfigs().saveConfigs();
		}

		if (hasChangedName(player)) {
			plugin.getConfigs().getPlayersConfig().set("Players." + uuid + ".username", player.getName());
			plugin.getConfigs().saveConfigs();
		}
	}

	public boolean hasRank(Player player) {
		if (plugin.getRanks().getPlayerRank(player) != null) {
			return true;
		}
		return false;
	}

	public boolean isRegistered(Player player) {
		if (plugin.getConfigs().getPlayersConfig().contains("Players." + player.getUniqueId())) {
			return true;
		}
		return false;
	}

	public boolean hasChangedName(Player player) {
		String oldName = plugin.getConfigs().getPlayersConfig()
				.getString("Players." + player.getUniqueId() + ".username");
		if (!oldName.equals(player.getName())) {
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		String uuid = player.getUniqueId().toString();
		if (plugin.usingRanks) {
			registerPlayer(player);

			plugin.getConfigs().getPlayersConfig().set("Players." + uuid + ".rank",
					plugin.getRanks().getPlayerRank(player).getName());
			plugin.getConfigs().saveConfigs();
			plugin.getRanks().loadPlayerPerms(player);
		}

		if (plugin.usingSb)
			Scoreboard.setScoreboard(player);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		Scoreboard.removeScoreboard(player);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = e.getEntity();
			if (plugin.usingDc) {
				if (plugin.dcEnabledWorlds.contains(player.getWorld().getName())) {
					if (e.getDrops().size() >= plugin.minItems) {
						ItemStack[] drops = e.getDrops().toArray(new ItemStack[0]);
						player.sendMessage("how much:" + drops.length);
						e.getDrops().clear();
						plugin.getDeathChest().createDeathChest(player, drops);
					}
				}
			}
		}
	}

}
