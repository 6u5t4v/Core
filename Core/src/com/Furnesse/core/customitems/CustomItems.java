package com.Furnesse.core.customitems;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import com.Furnesse.core.Core;
import com.Furnesse.core.common.ItemStacks;
import com.Furnesse.core.common.ItemstackBuilder;
import com.Furnesse.core.utils.Utils;

public class CustomItems {

	Core plugin;

	public CustomItems(Core plugin) {
		this.plugin = plugin;
	}


	public HashMap<String, ItemStack> customitems = new HashMap<>();

	public void loadItems() {
		
		FileConfiguration file = plugin.getFileManager().getConfig("customitems.yml").get();

		for (String item : file.getKeys(false)) {
			if (item != null) {
				
				ItemstackBuilder builder;
				ConfigurationSection section = file.getConfigurationSection(item);
				if (section.getBoolean("enabled")) {
					builder = ItemStacks.toItemStackBuilder(section)
							.withPDCString(plugin.customitemKey, item)
							.withItemFlag(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

					if (!section.getStringList("enchantments").isEmpty()) {
						for (String ench : section.getStringList("enchantments")) {
							String[] split = ench.split(",");
							String enchant = split[0];
							int level = Integer.valueOf(split[1]);

							Enchantment enchantment = Utils.isEnchantment(enchant);
							if (enchantment != null) {
								builder.withEnchantment(enchantment, level);
							}
						}
					}

					ItemStack stack = builder.build();
					
					if (section.getBoolean("recipe.enabled")) {
						Bukkit.addRecipe(getCustomRecipe(section, item, stack));
					}

					customitems.put(item, stack);
				}
			}
		}
	}
	
	public ItemStack getItem(String name) {
		if(customitems.containsKey(name)) {
			return customitems.get(name);
		}
		return null;
	}
	
	private ShapedRecipe getCustomRecipe(ConfigurationSection section, String key, ItemStack item) {
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, key), item);

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
}
