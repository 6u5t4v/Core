package com.Furnesse.core.customitems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.utils.ItemUtil;

public class CItem {
	private String name;
	private ItemStack item;
	private CRecipe recipe;

	public CItem(String name, CRecipe recipe) {
		this.name = name;
		this.recipe = recipe;
		this.item = ItemUtil.loadItemFromConfig("customitems.yml", this.name);
	}

	public void give(Player p, int amount) {
		amount = amount < 1 ? amount = 1 : amount;
		
		for (int i = 0; i < amount; i++) {
			p.getInventory().addItem(this.item);
		}
	}
	
	public String getName() {
		return name;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public CRecipe getRecipe() {
		return recipe;
	}
	
}
