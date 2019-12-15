package com.Furnesse.core.customitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Debug;
import com.Furnesse.core.utils.Lang;

public class CItemManager {

	Core plugin;

	public CItemManager(Core plugin) {
		this.plugin = plugin;
	}

	public List<CItem> customItems = new ArrayList<CItem>();

	public void loadCustomItems() {
		customItems.clear();

		int loaded = 0;
		for (String cItem : plugin.fileManager.getConfig("customitems.yml").get().getKeys(false)) {
			if (cItem == null)
				return;
			try {
				boolean cItemEnabled = plugin.fileManager.getConfig("customitems.yml").get().getBoolean(cItem + ".enabled");
				if (cItemEnabled) {
					String id = cItem.toString();
//					CItemTypes type = CItemTypes
//							.valueOf(plugin.getConfigs().getCItemsConfig().getString(cItem + ".type"));

//					if (type == null) {
//						plugin.getLogger().severe(ChatColor.RED + "Make sure you are using a valid type for " + cItem);
//						continue;
//					}

					boolean hasRecipe = plugin.fileManager.getConfig("customitems.yml").get().getBoolean(cItem + ".recipe.enabled");
					CRecipe recipe = null;
					if(hasRecipe) {
						List<String> list = plugin.fileManager.getConfig("customitems.yml").get().getStringList(cItem + ".recipe.pattern");
						String[] pattern = list.toArray(new String[0]);
						List<Map<Character, Material>> ingredients = null;
						for(String str : plugin.fileManager.getConfig("customitems.yml").get().getConfigurationSection(cItem + ".recipe.ingredients").getKeys(false)) {
							Map<Character, Material> ingred = new HashMap<>();
							Character val = str.charAt(0);
							Material mat = Material.getMaterial(plugin.fileManager.getConfig("customitems.yml").get().getString(cItem + ".recipe.ingredients." + str));
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

		plugin.getLogger().info("Loaded: " + loaded + " custom items");

	}

	public CItem getCItem(String str) {
		for (CItem item : customItems) {
			if (item.getName().equalsIgnoreCase(str)) {
				return item;
			}
		}
		return null;
	}

//	@SuppressWarnings("deprecation")
//	public ItemStack createItem(CItem cItem, int amount) {
//		ItemStack item = new ItemStack(cItem.getMat(), amount);
//		ItemMeta meta = item.getItemMeta();
//		meta.setDisplayName(Lang.chat(cItem.getName()));
//		switch (cItem.getType()) {
//		case weapon:
//			if (cItem.getWeapon().enchants != null) {
//				for (Map<Enchantment, Integer> enchantment : cItem.getWeapon().enchants) {
//					enchantment.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
//					enchantment.forEach((enchant, level) -> System.out.println("added lvl: " + level + " " + enchantment));
//				}
//			}
//			break;
//		case armor:
//			if (cItem.getWeapon().enchants != null) {
//				for (Map<Enchantment, Integer> enchantment : cItem.getArmor().enchants) {
//					enchantment.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
//					enchantment.forEach((enchant, level) -> System.out.println("added lvl: " + level + " " + enchantment));
//					
//				}
//			}
//
//			AttributeModifier modifier = new AttributeModifier("generic.armor.toughness", cItem.getArmor().armor,
//					Operation.ADD_NUMBER);
//			meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, modifier);
//			break;
//		case block:
//			break;
//		case item:
//			break;
//		}
//		if (cItem.isCanBeRepaired()) {
//			item.setDurability((short) cItem.getDurability());
//		} else {
//			meta.setUnbreakable(true);
//			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
//		}
//		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
//		List<String> lore = new ArrayList<>();
//		for (String str : cItem.getLore()) {
//			lore.add(Lang.chat(str));
//		}
//		meta.setLore(lore);
//		item.setItemMeta(meta);
//		return item;
//	}

//	public List<Map<Enchantment, Integer>> getEnchantments(String item) {
//		Map<Enchantment, Integer> enchant = new HashMap<>();
//		List<Map<Enchantment, Integer>> enchants = new ArrayList<>();
//		for (String str : plugin.fileManager.getConfig("customitems.yml").get().getStringList(item + ".enchants")) {
//			String enchantName = str.split(":")[0];
//			int enchantlvl = Integer.parseInt(str.split(":")[1]);
//			if (Enchantment.getByKey(NamespacedKey.minecraft(enchantName)) != null) {
//				Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
//				enchant.put(enchantment, enchantlvl);
//				enchants.add(enchant);
//			} else {
//				System.out.println("Make sure you are using the vanilla enchantments & lowercase.");
//				continue;
//			}
//		}
//
//		return enchants;
//	}
	
	public void giveCItem(CommandSender sender, Player target, CItem cItem, int amount) {
		if (target.getInventory().getSize() == -1) {
			target.sendMessage(Lang.FULL_INVENTORY);
			return;
		}

		cItem.give(target, amount);
		target.sendMessage(Lang.ITEMS_PLAYER_RECEIVED
				.replace("%amount%", String.valueOf(amount))
				.replace("%item%", cItem.getName()));
	}
}
