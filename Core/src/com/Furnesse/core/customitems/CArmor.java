package com.Furnesse.core.customitems;

import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;

public class CArmor {
	List<Map<Enchantment, Integer>> enchants;
	int armor;

	public CArmor(List<Map<Enchantment, Integer>> enchants, int armor) {
		this.enchants = enchants;
		this.armor = armor;
	}

	public List<Map<Enchantment, Integer>> getEnchants() {
		return enchants;
	}

	public void setEnchants(List<Map<Enchantment, Integer>> enchants) {
		this.enchants = enchants;
	}

	public int getArmor() {
		return armor;
	}

	public void setArmor(int armor) {
		this.armor = armor;
	}
}
