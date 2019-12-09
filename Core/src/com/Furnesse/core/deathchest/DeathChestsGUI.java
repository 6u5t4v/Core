package com.Furnesse.core.deathchest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.Furnesse.core.Core;

public class DeathChestsGUI implements Listener{

	static Core plugin = Core.instance;

	public static Inventory dcMenu(Player p) {
		Inventory inv = Bukkit.createInventory(null, 45, "Deathchests menu");

		if (DeathChestManager.getInstance().getPlayerDeathChests(p) != null) {
			int slot = 0;
			for (DeathChest dc : DeathChestManager.getInstance().getPlayerDeathChests(p)) {
				if (slot <= 26) {
					inv.setItem(slot, dc.createListItem());
					slot++;
				}
			}
		}
		for (int i = 0; i < 9; i++) {
			inv.setItem(27 + i, borderItem());
		}

		inv.setItem(37, Items.PREV_ITEM.getItemStack().clone());
		inv.setItem(43, Items.NEXT_ITEM.getItemStack().clone());

		return inv;
	}

	private static ItemStack borderItem() {
		ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}
	
	public void onMenuInteract(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if(e.getClickedInventory().equals(dcMenu(p))) {
			e.setCancelled(true);
		}
	}
}
