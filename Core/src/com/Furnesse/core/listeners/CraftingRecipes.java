package com.Furnesse.core.listeners;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.Core;

public class CraftingRecipes implements Listener {

	Core plugin = Core.instance;
	
	@EventHandler
	public void craftItem(PrepareItemCraftEvent e) {
		try {
			if (e.getRecipe() == null) {
				return;
			}

			if (e.getRecipe().getResult() == null) {
				return;
			}

			if (e.getRecipe().getResult().getType() == null) {
				return;
			}

			Material itemType = e.getRecipe().getResult().getType();
			if (itemType != null) {
				if (plugin.disabledRecipes.contains(itemType)) {
					e.getInventory().setResult(new ItemStack(Material.AIR));
					for (HumanEntity he : e.getViewers()) {
						if (he instanceof Player) {
							he.sendMessage("§cThis is not a craftable item\nYou may buy it at /shop");
						}
					}
				}
			}
		} catch (NullPointerException e2) {
			e2.printStackTrace();
		}

	}
}
