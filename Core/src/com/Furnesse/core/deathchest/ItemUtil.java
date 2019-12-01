package com.Furnesse.core.deathchest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.Furnesse.core.Core;

public class ItemUtil {
	static Core plugin = Core.instance;

	public static ItemStack getPlayerSkull(Player player, String title, List<String> lore) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(player.getName());
		if (title != null) {
			meta.setDisplayName(title);
		}
		if (lore != null) {
			meta.setLore(convertLore(lore));
		}
		skull.setItemMeta((ItemMeta) meta);
		return skull;
	}

	public static ItemStack getPlayerSkull(OfflinePlayer player, String title, List<String> lore) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(player.getName());
		meta.setDisplayName(title);
		meta.setLore(convertLore(lore));
		skull.setItemMeta((ItemMeta) meta);
		return skull;
	}

	public static ItemStack loadItemFromConfig(String configName, String path) {
		Material m = Material.getMaterial(plugin.getConfig().getString(path + ".material"));
		int amount = plugin.getConfig().getInt(path + ".amount");
		String displayName = plugin.getConfig().getString(path + ".displayname");
		List<String> lore = plugin.getConfig().getStringList(path + ".lore");
		return create(m, amount, displayName, lore);
	}

	public static int getInventorySizeBasedOnList(List<?> list) {
		int size = 9;
		while (list.size() > size && size != 54) {

			size += 9;
		}
		return size;
	}

	public static int getInventorySize(int i) {
		int size = 9;
		while (i > size && size != 54) {

			size += 9;
		}
		return size;
	}

	public static List<String> convertLore(List<String> list) {
		List<String> lore = new ArrayList<>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				lore.add(ChatColor.translateAlternateColorCodes('&', list.get(i)));
			}
		}
		return lore;
	}

	public static List<String> makeLore(String... string) {
		return Arrays.asList(string);
	}

	public static ItemStack create(Material material, int amount, String displayName, List<String> lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		if (displayName != null) {
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
		}
		if (lore != null) {
			meta.setLore(convertLore(lore));
		}
//		if (enchantments != null) {
//			for (int i = 0; i < enchantments.size(); i++) {
//				String[] enchantment = ((String) enchantments.get(i)).split(":");
//				meta.addEnchant(ench(enchantment[0]), Integer.valueOf(enchantment[1]).intValue(), true);
//			}
//		}
		item.setItemMeta(meta);
		return item;
	}
}
