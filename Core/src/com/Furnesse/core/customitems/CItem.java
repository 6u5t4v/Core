package com.Furnesse.core.customitems;

import java.util.List;

import org.bukkit.Material;

public class CItem {
	private String id;
	private boolean enabled;
	private boolean canBeRepaired;
	private boolean hasRecipe;
	private String name;
	private Material mat;
	private int durability;
	private CItemTypes type;
	private List<String> lore;
	private CArmor armor;
	private CWeaponry weapon;
	private CRecipe recipe;

	public CItem(String id, boolean enabled, boolean canBeRepaired, boolean hasRecipe, String name, Material mat,
			int durability, List<String> lore, CItemTypes type, CArmor armor, CWeaponry weapon, CRecipe recipe) {
		this.id = id;
		this.enabled = enabled;
		this.canBeRepaired = canBeRepaired;
		this.hasRecipe = hasRecipe;
		this.name = name;
		this.mat = mat;
		this.durability = durability;
		this.lore = lore;
		this.type = type;
		this.armor = armor;
		this.weapon = weapon;
		this.recipe = recipe;
	}

	public boolean isHasRecipe() {
		return hasRecipe;
	}

	public void setHasRecipe(boolean hasRecipe) {
		this.hasRecipe = hasRecipe;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDurability() {
		return durability;
	}

	public void setDurability(int durability) {
		this.durability = durability;
	}

	public CArmor getArmor() {
		return armor;
	}

	public void setArmor(CArmor armor) {
		this.armor = armor;
	}

	public CWeaponry getWeapon() {
		return weapon;
	}

	public void setWeapon(CWeaponry weapon) {
		this.weapon = weapon;
	}

	public boolean isCanBeRepaired() {
		return canBeRepaired;
	}

	public void setCanBeRepaired(boolean canBeRepaired) {
		this.canBeRepaired = canBeRepaired;
	}

	public Material getMat() {
		return mat;
	}

	public void setMat(Material mat) {
		this.mat = mat;
	}

	public CItemTypes getType() {
		return type;
	}

	public void setType(CItemTypes type) {
		this.type = type;
	}

	public CRecipe getRecipe() {
		return recipe;
	}

	public void setRecipe(CRecipe recipe) {
		this.recipe = recipe;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//		public CItemTypes getType() {
//			return type;
//		}
//
//		public void setType(CItemTypes type) {
//			this.type = type;
//		}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

}
