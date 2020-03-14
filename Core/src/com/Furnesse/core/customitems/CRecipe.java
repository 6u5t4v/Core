package com.Furnesse.core.customitems;

import java.util.Map;

import org.bukkit.Material;

public class CRecipe {
	private String[] shape;
	private Map<Character, Material> ingredients;

	public CRecipe(String[] shape, Map<Character, Material> ingredients) {
		this.shape = shape;
		this.ingredients = ingredients;
	}

	public Map<Character, Material> getIngredients() {
		return ingredients;
	}

	public String[] getShape() {
		return shape;
	}
}
