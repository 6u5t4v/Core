package com.Furnesse.core.customitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import com.Furnesse.core.Core;
import com.Furnesse.core.config.Message;
import com.Furnesse.core.utils.Debug;

public class CItemManager {

	Core plugin;

	public CItemManager(Core plugin) {
		this.plugin = plugin;
	}

	public List<CItem> customItems = new ArrayList<CItem>();

	public void loadCustomItems() {
		FileConfiguration config = plugin.getFileManager().getConfig("customitems.yml").get();
		customItems.clear();

		int loaded = 0;
		for (String cItem : config.getKeys(false)) {
			if (cItem == null)
				return;
			try {
				boolean cItemEnabled = config.getBoolean(cItem + ".enabled");
				if (cItemEnabled) {
					String id = cItem.toString();
//					CItemTypes type = CItemTypes
//							.valueOf(plugin.getConfigs().getCItemsConfig().getString(cItem + ".type"));

//					if (type == null) {
//						plugin.getLogger().severe(ChatColor.RED + "Make sure you are using a valid type for " + cItem);
//						continue;
//					}

					boolean hasRecipe = config.getBoolean(cItem + ".recipe.enabled");
					CRecipe recipe = null;
					if (hasRecipe) {
						List<String> list = config.getStringList(cItem + ".recipe.pattern");
						String[] pattern = list.toArray(new String[0]);
						List<Map<Character, Material>> ingredients = null;
						for (String str : config.getConfigurationSection(cItem + ".recipe.ingredients")
								.getKeys(false)) {
							Map<Character, Material> ingred = new HashMap<>();
							Character val = str.charAt(0);
							Material mat = Material.getMaterial(config.getString(cItem + ".recipe.ingredients." + str));
							ingred.put(val, mat);
						}
						Debug.Log("pattern: " + pattern);
						recipe = new CRecipe(pattern, ingredients);
					}

					CItem customItem = new CItem(id, recipe);

					customItems.add(customItem);
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

				Debug.Log("recipe pattern:" + cRecipe.getPattern().toString());
				Debug.Log("recipe pattern:" + cRecipe.getPattern());

				ShapedRecipe recipe = new ShapedRecipe(cItem.getItem()).shape(cRecipe.getPattern());

				for (Map<Character, Material> ingredients : cRecipe.getIngredients()) {
					ingredients.forEach((key, mat) -> recipe.setIngredient(key, mat));
				}

				Bukkit.getServer().addRecipe(recipe);
				Debug.Log("loaded recipe for " + cItem.getName());
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

		cItem.give(target, amount);
		target.sendMessage(Message.ITEMS_PLAYER_RECEIVED.getChatMessage().replace("%amount%", String.valueOf(amount))
				.replace("%item%", cItem.getName()));
	}
}
