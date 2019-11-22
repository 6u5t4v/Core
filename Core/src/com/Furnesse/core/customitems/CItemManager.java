package com.Furnesse.core.customitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

import net.md_5.bungee.api.ChatColor;

public class CItemManager {

	Core plugin;

	public CItemManager(Core plugin) {
		this.plugin = plugin;
	}

	public List<CItem> customItems = new ArrayList<CItem>();

	public void loadCustomItems() {
		customItems.clear();

		int loaded = 0;
		for (String cItem : plugin.getConfigs().getCItemsConfig().getKeys(false)) {
			if (cItem == null)
				return;
			try {
				boolean cItemEnabled = plugin.getConfigs().getCItemsConfig().getBoolean(cItem + ".enabled");
				if (cItemEnabled) {
					String id = cItem.toString();
					boolean canBeRepaired = plugin.getConfigs().getCItemsConfig().getBoolean(cItem + ".canBeRepaired");
					String name = plugin.getConfigs().getCItemsConfig().getString(cItem + ".name");
					Material mat = Material
							.getMaterial(plugin.getConfigs().getCItemsConfig().getString(cItem + ".material"));
					int durability = plugin.getConfigs().getCItemsConfig().getInt(cItem + ".durability");
					List<String> lore = plugin.getConfigs().getCItemsConfig().getStringList(cItem + ".lore");
					CItemTypes type = CItemTypes
							.valueOf(plugin.getConfigs().getCItemsConfig().getString(cItem + ".type"));

					if (type == null) {
						plugin.getLogger().severe(ChatColor.RED + "Make sure you are using a valid type for " + cItem);
						continue;
					}

					boolean hasRecipe = plugin.getConfigs().getCItemsConfig().getBoolean(cItem + ".recipe.enabled");

					CItem customItem = new CItem(id, cItemEnabled, canBeRepaired, hasRecipe, name, mat, durability,
							lore, type, null, null, null);

					switch (type) {
					case armor:
						if (plugin.getConfigs().getCItemsConfig().getStringList(cItem + ".enchants") != null) {
							int armorAmount = plugin.getConfigs().getCItemsConfig().getInt(cItem + ".armor");
							customItem.setArmor(new CArmor(getEnchantments(cItem), armorAmount));
						}
						break;
					case block:
						break;
					case item:
						break;
					case weapon:
						if (plugin.getConfigs().getCItemsConfig().getStringList(cItem + ".enchants") != null) {
							customItem.setWeapon(new CWeaponry(getEnchantments(cItem)));
						}
						break;
					}

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

	public List<Map<Enchantment, Integer>> getEnchantments(String item) {
		Map<Enchantment, Integer> enchant = new HashMap<>();
		List<Map<Enchantment, Integer>> enchants = new ArrayList<>();
		for (String str : plugin.getConfigs().getCItemsConfig().getStringList(item + ".enchants")) {
			String enchantName = str.split(":")[0];
			int enchantlvl = Integer.parseInt(str.split(":")[1]);
			if (Enchantment.getByKey(NamespacedKey.minecraft(enchantName)) != null) {
				Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
				enchant.put(enchantment, enchantlvl);
				enchants.add(enchant);
			}else {
				System.out.println("Make sure you are using the vanilla enchantments & lowercase.");
				continue;
			}
		}

		return enchants;
	}

	public CItem getCItem(String str) {
		for (CItem item : customItems) {
			if (item.getId().equalsIgnoreCase(str)) {
				return item;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public ItemStack createItem(CItem cItem, int amount) {
		ItemStack item = new ItemStack(cItem.getMat(), amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Lang.chat(cItem.getName()));
		switch (cItem.getType()) {
		case weapon:
			if (cItem.getWeapon().enchants != null) {
				for (Map<Enchantment, Integer> enchantment : cItem.getWeapon().enchants) {
					enchantment.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
					enchantment.forEach((enchant, level) -> System.out.println("added lvl: " + level + " " + enchantment));
				}
			}
			break;
		case armor:
			if (cItem.getWeapon().enchants != null) {
				for (Map<Enchantment, Integer> enchantment : cItem.getArmor().enchants) {
					enchantment.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
					enchantment.forEach((enchant, level) -> System.out.println("added lvl: " + level + " " + enchantment));
					
				}
			}

			AttributeModifier modifier = new AttributeModifier("generic.armor.toughness", cItem.getArmor().armor,
					Operation.ADD_NUMBER);
			meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, modifier);
			break;
		case block:
			break;
		case item:
			break;
		}
		if (cItem.isCanBeRepaired()) {
			item.setDurability((short) cItem.getDurability());
		} else {
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> lore = new ArrayList<>();
		for (String str : cItem.getLore()) {
			lore.add(Lang.chat(str));
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public void giveCItem(CommandSender sender, Player target, CItem cItem, int amount) {
		if (cItem == null) {
			sender.sendMessage("Not a valid item, try /items list or /items");
			return;
		}
		if (amount <= 0) {
			target.getInventory().addItem(createItem(cItem, 64));
			return;
		}

		target.getInventory().addItem(createItem(cItem, amount));

	}
}