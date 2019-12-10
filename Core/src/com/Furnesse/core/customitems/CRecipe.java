package com.Furnesse.core.customitems;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;

import com.Furnesse.core.Core;

public class CRecipe {
	Core plugin = Core.instance;

	String[] pattern;
	List<Map<Character, Material>> ingredient;

	public CRecipe(String[] pattern, List<Map<Character, Material>> ingredient) {
		this.pattern = pattern;
		this.ingredient = ingredient;
	}

	public List<Map<Character, Material>> getIngredient() {
		return ingredient;
	}

	public void setIngredient(List<Map<Character, Material>> ingredient) {
		this.ingredient = ingredient;
	}
}
