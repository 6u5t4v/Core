package com.Furnesse.core.deathchest;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class DeathChest {

	private String owner;
	private Location loc;
	private ItemStack[] drops;
	
	public DeathChest(String owner, Location loc, ItemStack[] drops) {
		this.owner = owner;
		this.loc = loc;
		this.drops = drops;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public ItemStack[] getDrops() {
		return drops;
	}

	public void setDrops(ItemStack[] drops) {
		this.drops = drops;
	}	
}
