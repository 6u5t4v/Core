package com.Furnesse.core.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTagEvent extends Event implements Cancellable {
	private Player damager;
	private Player victim;
	private int time;
	private boolean cancelled;

	public PlayerTagEvent(Player damager, Player victim, int time) {
		this.damager = damager;
		this.victim = victim;
		this.time = time;
	}

	public Player getDamager() {
		return this.damager;
	}

	public Player getVictim() {
		return this.victim;
	}

	public int getTagTime() {
		return this.time;
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
