package com.Furnesse.core.customitems;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.ItemUtil;

public class CItem {
	private String name;
	private ItemStack item;
	private CRecipe recipe;

	public CItem(String name, CRecipe recipe, ItemStack item) {
		this.name = name;
		this.recipe = recipe;
		this.item = item;
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
		return this.recipe;
	}
	
	public ShapedRecipe gfdetRecipe() {
		NamespacedKey key = new NamespacedKey(Core.instance, this.name);
		
		ShapedRecipe recipe = new ShapedRecipe(key, getItem());

		recipe.shape();		
		return null;
	}
}
