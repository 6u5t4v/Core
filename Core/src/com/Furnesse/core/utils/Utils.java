package com.Furnesse.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import com.Furnesse.core.Core;
import com.Furnesse.core.deathchest.DeathChest;

public class Utils {
	Core plugin;
	
	public Utils (Core plugin) {this.plugin = plugin;}
	
	Map<Location, Material> placeholders = new HashMap<>();

	public void resetDcLocs() {
		if (placeholders == null)
			return;
		
		if (!placeholders.isEmpty()) {
			placeholders.forEach((location, material) -> location.getBlock().setType(material));
			Core.instance.getLogger().info("All deathchest has been deleted");
		}else {
			plugin.getLogger().severe("No active deathchests");
		}
	}
	
//	public void resetDcLoc() {
//		for (DeathChest dc : plugin.getDeathChest().deathChests) {
//			Location loc = dc.getLoc();
//			if (plugin.utils.getPlaceholders().get(loc) != null) {
//				loc.getBlock().setType(plugin.utils.getPlaceholders().get(loc));
//				continue;
//			}
//			loc.getBlock().setType(Material.AIR);
//		}
//	}
	
	public void resetDcLoc(OfflinePlayer owner) {
		DeathChest dc = plugin.getDeathChest().getDeathChestByOwner(owner.getName());
		
		if (placeholders.get(dc.getLoc()) != null || placeholders.get(dc.getLoc()) != Material.AIR) {
			dc.getLoc().getBlock().setType(placeholders.get(dc.getLoc()));
		} else {
			dc.getLoc().getBlock().setType(Material.AIR);
		}
	}

	public Map<Location, Material> getPlaceholders() {
		return placeholders;
	}
}
