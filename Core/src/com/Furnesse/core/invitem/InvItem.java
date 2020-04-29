package com.Furnesse.core.invitem;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class InvItem {

	private ItemStack item;
	private List<String> commands;

	public InvItem(ItemStack item, List<String> commands) {
		this.item = item;
		this.commands = commands;
	}

	public ItemStack getItem() {
		return item;
	}

	public List<String> getCommands() {
		return commands;
	}
}
