package com.Furnesse.core.custommenus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.common.ItemStacks;
import com.Furnesse.core.common.ItemstackBuilder;
import com.Furnesse.core.utils.Debug;
import com.Furnesse.core.utils.Lang;

public class Menu {

	private String name;
	private String invtitle;
	private int slots;
	private String opencmd;
	private String permission;
	private List<ItemStack> contents;

	private Inventory menu;

	public Menu(String name, ConfigurationSection section) {
		this.name = name;
		this.invtitle = section.getString("title");
		this.slots = section.getInt("slots");
		this.opencmd = section.getString("command");
		this.permission = section.getString("permission");

		this.setContents(section);

		menu = Bukkit.createInventory(null, slots, Lang.chat(invtitle));
	}

	private void setContents(ConfigurationSection section) {
		section = section.getConfigurationSection("items");
		for (String cate : section.getKeys(false)) {
			ItemstackBuilder builder = ItemStacks.toItemStackBuilder(section);
			ItemStack stack = builder.build();
			
			if(stack == null)
				continue;
			
			if(section.get("slot") != null) {
				try {
					menu.setItem(section.getInt("slot"), stack);
				} catch (Exception e) {
					Debug.Log("slot cant be greater than " + (slots - 1));
					continue;
				}
				continue;
			}
			
			if(section.get("slots") != null) {
				section.getIntegerList("slots").forEach(slot -> {
					try {
						menu.setItem(slot, stack);
						
					} catch (Exception e) {
						Debug.Log("slot cant be greater than " + (slots - 1));
					}
				});
			}
		}
	}

	public String getName() {
		return name;
	}

	public String getInvtitle() {
		return invtitle;
	}

	public int getSlots() {
		return slots;
	}

	public String getOpencmd() {
		return opencmd;
	}

	public String getPermission() {
		return permission;
	}

	public List<ItemStack> getContents() {
		return contents;
	}

	public Inventory getMenu() {
		return menu;
	}
}
