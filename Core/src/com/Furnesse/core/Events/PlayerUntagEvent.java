package com.Furnesse.core.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUntagEvent extends Event implements Cancellable {
	private Player player;
	private boolean cancelled;
	private final UntagCause cause;

	public enum UntagCause
	{
		COMBATLOG, KICK, TIME_EXPIRE, DEATH, LAGOUT, SAFE_AREA;
	}

	public PlayerUntagEvent(Player player, UntagCause uc) {
		this.player = player;
		this.cause = uc;
	}

	public Player getPlayer() {
		return this.player;
	}

	public UntagCause getCause() {
		return this.cause;
	}

	private static HandlerList handlerList = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlerList;
	}

	public HandlerList getHandlers() {
		return handlerList;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean b) {
		this.cancelled = b;
	}
}
