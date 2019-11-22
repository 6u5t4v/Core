package com.Furnesse.core.customitems;

import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;

public class CWeaponry {
	List<Map<Enchantment, Integer>> enchants;

	public CWeaponry(List<Map<Enchantment, Integer>> enchants) {
		this.enchants = enchants;
	}

	public List<Map<Enchantment, Integer>> getEnchants() {
		return enchants;
	}

	public void setEnchants(List<Map<Enchantment, Integer>> enchants) {
		this.enchants = enchants;
	}
}
