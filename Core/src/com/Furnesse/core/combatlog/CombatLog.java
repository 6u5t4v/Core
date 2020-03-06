package com.Furnesse.core.combatlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.Furnesse.core.Core;
import com.Furnesse.core.Events.PlayerUntagEvent;
import com.Furnesse.core.config.Message;
import com.Furnesse.core.utils.ActionBar;

public class CombatLog {
	Core plugin;

	public CombatLog(Core plugin) {
		this.plugin = plugin;
	}

	public HashMap<UUID, Long> taggedPlayers = new HashMap<>();
	public ArrayList<UUID> killPlayers = new ArrayList<>();

	public void enableTimer() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				Iterator<Map.Entry<UUID, Long>> iter = taggedPlayers.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<UUID, Long> c = iter.next();
					Player player = plugin.getServer().getPlayer(c.getKey());
					if (player == null || !player.isOnline()) {
						continue;
					}

					if (plugin.getSettings().cl_actionBar) {

						ActionBar.sendActionBar(player, Message.ACTIONBAR_IN_COMBAT.getMessage().replace("%time%",
								String.valueOf(tagTimeRemaining(player.getUniqueId()))));

//						player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
//								TextComponent.fromLegacyText(Message.ACTIONBAR_IN_COMBAT.getMessage().replace("%time%",
//										String.valueOf(tagTimeRemaining(player.getUniqueId())))));
					}

					if (getCurrentTime() - c.getValue() >= plugin.getSettings().cl_tagDuration) {
						iter.remove();
						PlayerUntagEvent event = new PlayerUntagEvent(player, PlayerUntagEvent.UntagCause.TIME_EXPIRE);
						plugin.getServer().getPluginManager().callEvent((Event) event);
					}
				}
			}
		}, 0L, 20L);

	}

	public void removeFly(Player player) {
		if (player.isFlying() && plugin.getSettings().cl_disabledOnTag.contains("fly")) {
			player.setFlying(false);
			player.sendMessage(Message.FLIGHT_DISABLED.getMessage());
		}
	}

	public long tagTimeRemaining(UUID id) {
		return this.taggedPlayers.get(id) - (getCurrentTime() - plugin.getSettings().cl_tagDuration);
	}

//	public long tagTime(UUID id) { return plugin.getSettings().cl_tagDuration - getCurrentTime() - Long.valueOf(((Long) this.taggedPlayers.get(id)).longValue()).longValue(); }

	public long getCurrentTime() {
		return System.currentTimeMillis() / 1000L;
	}
}
