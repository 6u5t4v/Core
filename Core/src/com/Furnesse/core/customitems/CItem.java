package com.Furnesse.core.customitems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.utils.ItemUtil;

public class CItem {
	private String name;
	private ItemStack item;
	private CItemTypes type;
	private boolean canBeRepaired;
	private boolean hasRecipe;

	public CItem(String name, CItemTypes type, boolean canBeRepaired, boolean hasRecipe) {
		this.name = name;
		this.type = type;
		this.canBeRepaired = canBeRepaired;
		this.hasRecipe = hasRecipe;
		this.item = ItemUtil.loadItemFromConfig("customitems.yml", this.name);
	}

	public void give(Player p, int amount) {
		amount = amount <= 0 ? amount = 1 : amount;
		
		for (int i = 0; i < amount; i++) {
			p.getInventory().addItem(this.item);
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public CItemTypes getType() {
		return type;
	}

	public void setType(CItemTypes type) {
		this.type = type;
	}

	public boolean isCanBeRepaired() {
		return canBeRepaired;
	}

	public void setCanBeRepaired(boolean canBeRepaired) {
		this.canBeRepaired = canBeRepaired;
	}

	public boolean isHasRecipe() {
		return hasRecipe;
	}

	public void setHasRecipe(boolean hasRecipe) {
		this.hasRecipe = hasRecipe;
	}
}
