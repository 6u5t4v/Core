package com.Furnesse.core.deathchest;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.Core;

public class DeathChest {

	Core plugin = Core.instance;
	
	private String uuid;
	private String owner;
	private Location loc;
	private List<ItemStack> drops;
	private Inventory inv;

	public DeathChest(String uuid, String owner, Location loc, List<ItemStack> drops, Inventory inv) {
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
		plugin.getConfigs().getDchestsConfig().set("Deathchests." + this.getUuid() + ".Owner", owner);
		plugin.getConfigs().saveConfigs();
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
		String worldName = loc.getWorld().getName();
		
		plugin.getConfigs().getDchestsConfig().set("Deathchests." + this.getUuid() + "." + worldName + ".X", loc.getBlockX());
		plugin.getConfigs().getDchestsConfig().set("Deathchests." + this.getUuid() + "." + worldName + ".Y", loc.getBlockY());
		plugin.getConfigs().getDchestsConfig().set("Deathchests." + this.getUuid() + "." + worldName + ".Z", loc.getBlockZ());
		plugin.getConfigs().saveConfigs();
	}

	public List<ItemStack> getDrops() {
		return drops;
	}

	public void setDrops(List<ItemStack> drops) {
		this.drops = drops;
		
		plugin.getConfigs().getDchestsConfig().set("Deathchests." + this.getUuid() + ".Drops", drops);
		plugin.getConfigs().saveConfigs();
	}
}
