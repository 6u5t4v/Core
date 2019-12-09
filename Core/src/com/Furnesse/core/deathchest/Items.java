package com.Furnesse.core.deathchest;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Items
{
	PREV_ITEM("deathchests.gui.prev_item"), NEXT_ITEM("deathchests.gui.next_item"),
	DEATHCHEST_LIST_ITEM("deathchests.gui.deathchest_item");

	private ItemStack itemStack;

	private String path;

	Items(String path) {
		this.path = path;
		this.itemStack = ItemUtil.loadItemFromConfig("config.yml", path);
	}

	public ItemStack getItemStack() {
		return this.itemStack;
	}

	public static void reload() {
		for (Items i : values()) {
			i.setItemStack(ItemUtil.loadItemFromConfig("config.yml", i.path));
		}
	}

	private void setItemStack(ItemStack itemstack) {
		this.itemStack = itemstack;
	}

	public static boolean isHelmet(Material type) {
		String n = type.toString();
		return (n.endsWith("_HELMET") || n.equalsIgnoreCase("HELMET"));
	}

	public static boolean isChestPlate(Material type) {
		String n = type.toString();
		return (n.endsWith("_CHESTPLATE") || n.equalsIgnoreCase("CHESTPLATE"));
	}

	public static boolean isBoots(Material type) {
		String n = type.toString();
		return (n.endsWith("_BOOTS") || n.equalsIgnoreCase("BOOTS"));
	}

	public static boolean isLeggings(Material type) {
		String n = type.toString();
		return (n.endsWith("_LEGGINGS") || n.equalsIgnoreCase("BOOTS"));
	}
}
