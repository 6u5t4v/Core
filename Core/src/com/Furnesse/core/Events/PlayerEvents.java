package com.Furnesse.core.Events;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.Core;
import com.Furnesse.core.database.MySQLPlayer;

public class PlayerEvents implements Listener {

	Core plugin = Core.instance;

	private void registerPlayer(Player player) {
		String uuid = player.getUniqueId().toString();
		if (!isRegistered(player) || hasChangedName(player)) {
			plugin.getConfigs().getPlayersConfig().set("Players." + uuid + ".username", player.getName());
			if (plugin.usingRanks) {
				plugin.getConfigs().getPlayersConfig().set("Players." + uuid + ".permissions", new ArrayList<String>());
			}
			plugin.getConfigs().saveConfigs();
		}
		
		MySQLPlayer.createPlayer(player.getUniqueId(), player);
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
//		String uuid = player.getUniqueId().toString();

		registerPlayer(player);
		if (plugin.usingRanks) {
			plugin.getRanks().loadRank(player);
			plugin.getRanks().loadPlayerPerms(player);
		}

		if (plugin.usingSb)
			plugin.getScoreboard().setScoreboard(player);

		if (plugin.usingChat) {
			plugin.chatFormat.initFormat(player);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (plugin.usingSb)
			plugin.getScoreboard().removeScoreboard(player);

		if (plugin.usingRanks)
			plugin.getRanks().saveRank(player);

		if (plugin.usingChat)
			if (plugin.chatFormat.getPlayerFormat(player) != null)
				plugin.chatFormat.pFormat.remove(player.getName());
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = e.getEntity();
			if (plugin.usingDc) {
				if (plugin.dcEnabledWorlds.contains(player.getWorld().getName())) {
					if (e.getDrops().size() >= plugin.minItems) {
						ItemStack[] drops = e.getDrops().toArray(new ItemStack[0]);
						e.getDrops().clear();
						plugin.getDeathChest().createDeathChest(player, drops);
					}
				}else{
					player.sendMessage("§cNo deathchest has spawned as deathchests are disabled in this world");
				}
			}
		}
	}

}
