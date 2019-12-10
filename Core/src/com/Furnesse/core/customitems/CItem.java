package com.Furnesse.core.customitems;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import com.Furnesse.core.utils.ItemUtil;

public class CItem {
	private String name;
	private ItemStack item;
	private CRecipe cRecipe;

	public CItem(String name, CRecipe cRecipe) {
		this.name = name;
		this.cRecipe = cRecipe;
		this.item = ItemUtil.loadItemFromConfig("customitems.yml", this.name);
	}

	public void give(Player p, int amount) {
		amount = amount <= 0 ? amount = 1 : amount;
		
		for (int i = 0; i < amount; i++) {
			p.getInventory().addItem(this.item);
		}
	}
	
	public void initRecipe() {
		// Our custom variable which we will be changing around.
		ItemStack result = this.item;

		// create a NamespacedKey for your recipe
		NamespacedKey key = new NamespacedKey((Plugin) this, this.name);

		// Create our custom recipe variable
		ShapedRecipe recipe = new ShapedRecipe(key, result);

		recipe.shape(recipe.getShape());

		for(Map<Character, Material> ingred : cRecipe.ingredient) {
			ingred.forEach((val, material) -> recipe.setIngredient(val, material));
		}

		// Finally, add the recipe to the bukkit recipes
		Bukkit.addRecipe(recipe);
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

	public CRecipe getcRecipe() {
		return cRecipe;
	}
	
}
