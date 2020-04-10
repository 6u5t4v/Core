package com.Furnesse.core.customitems;

import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.Furnesse.core.Core;
import com.Furnesse.core.common.ItemStacks;
import com.Furnesse.core.utils.Debug;

public abstract class CustomItemstack {

	Core plugin = Core.instance;

	public final NamespacedKey key = new NamespacedKey(Core.instance, "customitem");

	public abstract void register();

	public abstract ItemStack getItemstack();

	public ShapedRecipe getRecipe(ConfigurationSection section, String key) {
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, key), getItemstack());

		Debug.Log("test : " + getItemstack().getItemMeta().getDisplayName());

		recipe.shape(section.getStringList("recipe.shape").toArray(new String[0]));

		for (String strVal : section.getConfigurationSection("recipe.ingredients").getKeys(false)) {
			char charVal = strVal.charAt(0);
			try {
				Material material = Material.getMaterial(section.getString("recipe.ingredients." + strVal));
				recipe.setIngredient(charVal, material);
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE,
						"Could not find material for recipe, make sure it is spelled right in config.yml", e);
			}
		}

		return recipe;
	}

	public boolean isHoldingItem(ItemStack stack, String val) {
		if (stack == null || stack.getItemMeta() == null)
			return false;
		
		PersistentDataContainer pdc = ItemStacks.toPDCItemStack(stack);
		if(pdc == null) return false;


		return (pdc.has(key, PersistentDataType.STRING) && (pdc.get(key, PersistentDataType.STRING) == val));
	}
}
