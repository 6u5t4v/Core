package com.Furnesse.core.deathchest;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DeathChest {

	private String uuid;
	private String owner;
	private Location loc;
	private ItemStack[] drops;
	private Inventory inv;

	public DeathChest(String uuid, String owner, Location loc, ItemStack[] drops, Inventory inv) {
		this.uuid = uuid;
		this.owner = owner;
		this.loc = loc;
		this.drops = drops;
		this.inv = inv;
	}

	public Inventory getInv() {
		return inv;
	}

	public void setInv(Inventory inv) {
		this.inv = inv;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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
