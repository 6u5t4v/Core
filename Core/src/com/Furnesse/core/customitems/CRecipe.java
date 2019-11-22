package com.Furnesse.core.customitems;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;

import com.Furnesse.core.Core;

public class CRecipe {
	Core plugin = Core.instance;

	List<String> pattern;
	Map<Character, Material> ingredient;

	public CRecipe(List<String> pattern, Map<Character, Material> ingredient) {
		this.pattern = pattern;
		this.ingredient = ingredient;
	}

	public Map<Character, Material> getIngredient() {
		return ingredient;
	}

	public void setIngredient(Map<Character, Material> ingredient) {
		this.ingredient = ingredient;
	}

	public List<String> getPattern() {
		return pattern;
	}

	public void setPattern(List<String> pattern) {
		this.pattern = pattern;
	}

//	public void instantiateRecipes() {
//		for (CItem cItem : plugin.cItemMan.customItems) {
//			if (cItem.isHasRecipe()) {
//				// Our custom variable which we will be changing around.
//				ItemStack item = plugin.cItemMan.createItem(cItem);
//
//				// create a NamespacedKey for your recipe
//				NamespacedKey key = new NamespacedKey((Plugin) this, cItem.getId());
//
//				// Create our custom recipe variable
//				ShapedRecipe recipe = new ShapedRecipe(key, item);
//
//				// Here we will set the places. E and S can represent anything, and the letters
//				// can be anything. Beware; this is case sensitive.
//
//				recipe.shape(cItem.getRecipe().pattern.get(0), 
//							cItem.getRecipe().pattern.get(1),
//							cItem.getRecipe().pattern.get(2));
//
//				// Set what the letters represent.
//				// E = Emerald, S = Stick
//				recipe.setIngredient(cItem.getRecipe().ingredient.entrySet());
//
//				// Finally, add the recipe to the bukkit recipes
//				Bukkit.addRecipe(recipe);
//			}
//		}
//	}
}
