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

	public static Inventory dcMenu = Bukkit.createInventory(null, 45, "Deathchests menu");
	
	public static void openDcMenu(Player p) {
		

		if (DeathChestManager.getInstance().getPlayerDeathChests(p) != null) {
			int slot = 0;
			for (DeathChest dc : DeathChestManager.getInstance().getPlayerDeathChests(p)) {
				if (slot <= 26) {
					dcMenu.setItem(slot, dc.createListItem());
					slot++;
				}
			}
		}
		for (int i = 0; i < 9; i++) {
			dcMenu.setItem(27 + i, borderItem());
		}

		dcMenu.setItem(37, Items.PREV_ITEM.getItemStack().clone());
		dcMenu.setItem(43, Items.NEXT_ITEM.getItemStack().clone());

		p.openInventory(dcMenu);
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
		
		if(e.getClickedInventory().equals(dcMenu)) {
			e.setCancelled(true);
		}
	}
}
