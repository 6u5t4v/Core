package com.Furnesse.core.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.Furnesse.core.Core;
import com.Furnesse.core.combatlog.CombatLog;

public class PlayerCombatLogEvent extends Event implements Cancellable {
	private boolean cancelled;
	private Player player;
	CombatLog combatLog;
	private static final HandlerList handlerList = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlerList;
	}

	public HandlerList getHandlers() {
		return handlerList;
	}

	public PlayerCombatLogEvent(CombatLog combatLog, Player player) {
		this.combatLog = combatLog;
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
	}

	public long getTagTimeRemaining() {
		return Core.instance.getSettings().cl_tagDuration - this.combatLog.getCurrentTime()
				- Long.valueOf(((Long) this.combatLog.taggedPlayers.get(this.player.getUniqueId())).longValue()).longValue();
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean b) {
		this.cancelled = b;
	}
}