package com.Furnesse.core.customitems;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;

import com.Furnesse.core.Core;

public class CRecipe {
	Core plugin = Core.instance;

	String[] pattern;
	List<Map<Character, Material>> ingredients;

	public CRecipe(String[] pattern, List<Map<Character, Material>> ingredients) {
		this.pattern = pattern;
		this.ingredients = ingredients;
	}

	public List<Map<Character, Material>> getIngredients() {
		return ingredients;
	}

	public String[] getPattern() {
		return pattern;
	}
}
