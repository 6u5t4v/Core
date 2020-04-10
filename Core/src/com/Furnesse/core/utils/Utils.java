package com.Furnesse.core.utils;

import java.util.logging.Level;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import com.Furnesse.core.Core;

public class Utils {
	static Core plugin = Core.instance;

	public static Enchantment isEnchantment(String str) {
		Enchantment enchant;
		try {
			str.toUpperCase();

			if (Enchantment.getByKey(NamespacedKey.minecraft(str)) != null) {
				enchant = Enchantment.getByKey(NamespacedKey.minecraft(str));
				return enchant;
			}
		} catch (NullPointerException e) {
			plugin.getLogger().log(Level.SEVERE, "No enchantment with the name " + str, e);
		}
		return null;
	}

}
