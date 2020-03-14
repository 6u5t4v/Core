package com.Furnesse.core.customitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import com.Furnesse.core.Core;
import com.Furnesse.core.config.Message;
import com.Furnesse.core.utils.Debug;
import com.Furnesse.core.utils.ItemUtil;

public class CItemManager {

	Core plugin;

	public CItemManager(Core plugin) {
		this.plugin = plugin;
	}

	public List<CItem> customItems = new ArrayList<CItem>();
	public Map<String, ItemStack> customItemstacks = new HashMap<>();

	public void loadCustomItems() {
		FileConfiguration config = plugin.getFileManager().getConfig("customitems.yml").get();
		customItems.clear();

		int loaded = 0;
		for (String cItem : config.getKeys(false)) {
			if (cItem == null)
				continue;
			try {
				boolean cItemEnabled = config.getBoolean(cItem + ".enabled");
				if (cItemEnabled) {
					String id = cItem.toString();

					boolean hasRecipe = config.getBoolean(cItem + ".recipe.enabled");
					CRecipe recipe = null;
					if (hasRecipe) {
						List<String> list = config.getStringList(cItem + ".recipe.pattern");
						String[] pattern = list.toArray(new String[0]);

						Map<Character, Material> ingredients = new HashMap<>();
						for (String str : config.getConfigurationSection(cItem + ".recipe.ingredients")
								.getKeys(false)) {
							char val = str.charAt(0);

							Material mat = null;

							mat = Material.getMaterial(config.getString(cItem + ".recipe.ingredients." + str));
							ingredients.put(val, mat);

						}
						recipe = new CRecipe(pattern, ingredients);
					}

					ItemStack item = ItemUtil.loadItemFromConfig("customitems.yml", id);
					
					CItem customItem = new CItem(id, recipe, item);

					customItems.add(customItem);
					customItemstacks.put(id, item);
					loaded++;
				}

			} catch (NullPointerException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		loadRecipes();
		plugin.getLogger().info("Loaded: " + loaded + " custom items");
	}

	private void loadRecipes() {
		for (CItem cItem : customItems) {
			if (cItem.getRecipe() != null) {
				CRecipe cRecipe = cItem.getRecipe();
				
				NamespacedKey key = new NamespacedKey(plugin, cItem.getName());

				ShapedRecipe recipe = new ShapedRecipe(key, cItem.getItem());

				recipe.shape(cRecipe.getShape());

				
				recipe.setIngredient('s', cItem.getItem().getType());
				try {
					cRecipe.getIngredients().forEach((ingred, mat) -> mat != null ? recipe.setIngredient(ingred, mat) : cItem.getItem().getType() != null ? recipe.setIngredient(ingred, cItem.getItem().getType()) : recipe.setIngredient(ingred, ));
				} catch (Exception e) {
					plugin.getLogger().log(Level.SEVERE,
							"recipe material for " + cItem.getName() + " could not be found", e);
					return;
				}

				Bukkit.getServer().addRecipe(recipe);
			}
		}
	}

	public CItem getCItem(String str) {
		for (CItem item : customItems) {
			if (item.getName().equalsIgnoreCase(str)) {
				return item;
			}
		}
		return null;
	}

	public boolean itemInHand(ItemStack item) {
		for (CItem cItem : customItems) {
			if (cItem.getItem().equals(item)) {
				return true;
			}
		}
		return false;
	}

	public void giveItem(CommandSender sender, Player target, CItem cItem, int amount) {
		if (target.getInventory().getSize() == -1) {
			target.sendMessage(Message.FULL_INVENTORY.getChatMessage());
			return;
		}

		amount = amount < 1 ? 1 : amount;
		for (int i = 0; i < amount; i++) {
			target.getInventory().addItem(cItem.getItem());
		}

		target.sendMessage(Message.ITEMS_PLAYER_RECEIVED.getChatMessage().replace("%amount%", String.valueOf(amount))
				.replace("%item%", cItem.getName()));
	}
}
