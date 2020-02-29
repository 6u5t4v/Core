package com.Furnesse.core.combatlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.Furnesse.core.Core;
import com.Furnesse.core.Events.PlayerUntagEvent;
import com.Furnesse.core.config.Message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class CombatLog {
	Core plugin;

	public CombatLog(Core plugin) {
		this.plugin = plugin;
	}

	public HashMap<String, Long> taggedPlayers = new HashMap<>();
	public ArrayList<String> killPlayers = new ArrayList<>();

	public void enableTimer() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin) this, new Runnable() {
			public void run() {
				Iterator<Map.Entry<String, Long>> iter = CombatLog.this.taggedPlayers.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, Long> c = iter.next();
					Player player = plugin.getServer().getPlayer(c.getKey());
					if (CombatLogSettings.actionBar) {
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
								TextComponent.fromLegacyText(Message.ACTIONBAR_IN_COMBAT.getMessage().replace("%time%",
										String.valueOf(CombatLog.this.tagTimeRemaining(player.getName())))));
					}

					if (CombatLog.this.getCurrentTime()
							- ((Long) c.getValue()).longValue() >= CombatLogSettings.tagDuration) {
						iter.remove();
						PlayerUntagEvent event = new PlayerUntagEvent(player, PlayerUntagEvent.UntagCause.TIME_EXPIRE);
						plugin.getServer().getPluginManager().callEvent((Event) event);
					}
				}
			}
		}, 0L, 20L);
	}

	public void loadListeners(PluginManager pm) {

	}

	public void removeFly(Player player) {
		if (player.isFlying() && CombatLogSettings.disabledOnTag.contains("fly")) {
			player.setFlying(false);
			player.sendMessage(Message.FLIGHT_DISABLED.getMessage());
		}
	}

	public long tagTimeRemaining(String id) {
		return CombatLogSettings.tagDuration - getCurrentTime()
				- Long.valueOf(((Long) this.taggedPlayers.get(id)).longValue()).longValue();
	}

	public long tagTime(String id) {
		return CombatLogSettings.tagDuration - getCurrentTime()
				- Long.valueOf(((Long) this.taggedPlayers.get(id)).longValue()).longValue();
	}

	public long getCurrentTime() {
		return System.currentTimeMillis() / 1000L;
	}
}
