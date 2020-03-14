package com.Furnesse.core.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

public class ItemsAPI {

	private static Core plugin;

	public ItemsAPI(Core plugin) {
		this.plugin = plugin;
	}

	public static ItemStack createSkullItem(String id) {
		return plugin.getHeadAPI().getItemHead(id);
	}

	public static ItemStack createItemStack(Material mat) {
		return new ItemStack(mat);
	}

	public static ItemStack createItemStack(Material mat, int amount) {
		return new ItemStack(mat, amount);
	}

	public static void addEnchantment(Enchantment ench, int level, ItemStack item) {
		item.addUnsafeEnchantment(ench, level);
		
		List<String> newLore = new ArrayList<>();
		newLore.add(Lang.chat("&7" + ench.getName() + " " + level));
	
		ItemMeta meta = item.getItemMeta();
		for(int i = 0; i < meta.getLore().size(); i++) {
			newLore.add(meta.getLore().get(i));
		}
		
		item.getItemMeta().addItemFlags(ItemFlag.HIDE_ENCHANTS);
	}
	
	public static void addLore(ItemStack item, List<String> lore) {
		
	}
}
