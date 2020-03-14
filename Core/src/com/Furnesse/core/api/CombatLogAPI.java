package com.Furnesse.core.api;

import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.Events.PlayerTagEvent;
import com.Furnesse.core.Events.PlayerUntagEvent;

public class CombatLogAPI {

	public CombatLogAPI instance;

	public Core plugin = Core.instance;

	public CombatLogAPI() {
		this.instance = this;
	}

	public boolean isInCombat(Player p) {
		return plugin.getCombatLog().taggedPlayers.containsKey(p.getUniqueId());
	}

	public boolean isInSafezone(Location loc) {
		if (plugin.landsHook) {
			if (plugin.landsAPI.isClaimed(loc) && !plugin.landsAPI.getLand(loc).isInWar()) {
				return true;
			}
		}

		return false;
	}
	
	public Long timeRemaining(UUID uuid) {
		return 0L;
	}

	public Player getCurrentEnemy(Player p) {
		return null;
	}

	public void tagPlayer(Player p, @Nullable int time) {
		if (time <= 0) {
			time = plugin.getSettings().cl_tagDuration;
		}

		PlayerTagEvent event = new PlayerTagEvent(p, time);
		plugin.getServer().getPluginManager().callEvent(event);
	}

	public void untagPlayer(Player p, PlayerUntagEvent.UntagCause cause) {
	}

	public CombatLogAPI getInstance() {
		return this.instance;
	}
}
