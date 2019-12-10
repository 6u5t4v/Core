package com.Furnesse.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
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
	
	public static ItemStack loadItemFromConfig(String configName, String path) {
		Material m = Material.getMaterial(plugin.getConfig().getString(path + ".material"));
		int amount = 1;
		if (plugin.getConfig().get(path + ".amount") != null) {
			amount = plugin.getConfig().getInt(path + ".amount") <= 0 ? amount = 1
					: plugin.getConfig().getInt(path + ".amount");
		}

		ItemStack is = m != null ? new ItemStack(m)
				: plugin.getHeadAPI().getItemHead(plugin.getConfigs().getCItemsConfig().getString(path + ".material"));

		String displayName = plugin.getConfig().getString(path + ".displayname");

		List<String> lore = plugin.getConfig().getStringList(path + ".lore");

		List<String> enchantments = new ArrayList<>();
		if (plugin.getConfig().getStringList(path + ".enchantments") != null) {
			enchantments = plugin.getConfig().getStringList(path + ".enchantments");
		}

		boolean glowing = false;
		if (plugin.getConfig().get(path + ".glowing") != null) {
			glowing = plugin.getConfig().getBoolean(path + ".glowing");
		}

		boolean hasDurability = true;
		if (plugin.getConfig().get(path + ".canBeRepaired") != null) {
			glowing = plugin.getConfig().getBoolean(path + ".canBeRepaired");
		}
		
		int durability = is.getDurability();
		if(plugin.getConfig().get(path + ".durability") != null) {
			durability = plugin.getConfig().getInt(path + ".durability");
		}

		return create(is, amount, displayName, lore, enchantments, hasDurability, durability, glowing);
	}

	public static List<String> convertLore(List<String> list) {
		List<String> lore = new ArrayList<>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				lore.add(Lang.chat(list.get(i)));
			}
		}
		return lore;
	}

	public static List<String> makeLore(String... string) {
		return Arrays.asList(string);
	}

	public static ItemStack create(ItemStack item, int amount, String displayName, List<String> lore,
			List<String> enchantments, boolean hasDurability, int durability, boolean glowing) {
		if (item == null)
			return null;

		item.setAmount(amount);

		ItemMeta meta = item.getItemMeta();
		if (displayName != null) {
			meta.setDisplayName(Lang.chat(displayName));
		}

		if (lore != null) {
			meta.setLore(convertLore(lore));
		}
		if (enchantments != null) {
			for (String enchant : enchantments) {
				String enchantName = enchant.split(":")[0];
				int enchantLvl = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(ench(enchantName), enchantLvl, true);
			}
		}

		if (hasDurability && durability != -1) {
			item.setDurability((short) durability);
		}else {
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}

		if (glowing) {
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		item.setItemMeta(meta);
//		Debug.Log("test: " + item.getItemMeta().getDisplayName());
		return item;
	}

	public static Enchantment ench(String str) {
		Enchantment enchant;
		try {
			if (str.toUpperCase() == null) {
				str.toUpperCase();
			}
			enchant = Enchantment.getByName(str);
			System.out.println("enchantment as string: " + str);
			return enchant;
		} catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		return null;
	}
}
