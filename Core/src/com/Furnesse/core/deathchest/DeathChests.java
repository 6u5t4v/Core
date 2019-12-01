package com.Furnesse.core.deathchest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Debug;
import com.Furnesse.core.utils.Lang;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class DeathChests {
	Core plugin;

	public DeathChests(Core plugin) {
		this.plugin = plugin;
	}

	public BiMap<Location, Inventory> dcInvs = HashBiMap.create();
	public Map<Location, DeathChest> droppedChests = new HashMap<>();

	public void loadDeathChests() {
		FileConfiguration locsConfig = plugin.getConfigs().getDchestsConfig();
		droppedChests.clear();
		dcInvs.clear();

		ConfigurationSection locations = locsConfig.getConfigurationSection("Locations");
		int loaded = 0;
		if (locations != null) {
			for (String uuid : locations.getKeys(false)) {
				if (uuid != null) {
					try {
						String worldName = plugin.getConfigs().getDchestsConfig()
								.getString("Locations." + uuid + ".location.world");
						World world = Bukkit.getWorld(worldName);
						int x, y, z;
						x = locsConfig.getInt("Locations." + uuid + ".location.x");
						y = locsConfig.getInt("Locations." + uuid + ".location.y");
						z = locsConfig.getInt("Locations." + uuid + ".location.z");

						List<?> list = locsConfig.getList("Locations." + uuid + ".drops");
						List<ItemStack> items = new ArrayList<>();
						for (int i = 0; i < list.size(); i++) {
							ItemStack item = (ItemStack) list.get(i);
							if (item == null) {
								continue;
							}
							items.add(item);
						}

//						ItemStack[] drops = items.toArray(new ItemStack[0]);

						String owner = locsConfig.getString("Locations." + uuid + ".owner");
						if (world != null) {
							Location loc = new Location(world, x, y, z);
							spawnDeathChest(null, owner, uuid, world, loc, items);
							loaded++;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
			plugin.getLogger().info("Loaded: " + loaded + " deathchests");
		}
	}

	public void spawnDeathChest(Player player, String playerName, String uuid, World world, Location loc,
			List<ItemStack> drops) {
		int numDrops = drops.size();

		Block block = loc.getBlock();

		Inventory dcInv;
		dcInv = Bukkit.createInventory(null, numDrops <= 27 ? 27 : numDrops <= 45 ? 45 : 54,
				Lang.chat("&c" + playerName + "'s &8Death chest"));

		for (ItemStack item : drops) {
			if (item == null)
				continue;
			if (item.getType() == Material.AIR)
				continue;

			dcInv.addItem(item);
		}

		if (block.getType() != Material.AIR) {
			plugin.utils.getPlaceholders().put(block.getLocation(), block.getType());
			Debug.Log("saved previous block");
		}
		block.setType(Material.PLAYER_HEAD);
		Skull skull = (Skull) block.getState();
		skull.setOwner(playerName);
		skull.update();

		DeathChest deathchest = new DeathChest(uuid, playerName, block.getLocation(), drops, dcInv);
		setupDeathchest(deathchest);

		if (player != null) {
			for (String str : plugin.getConfigs().getLangConfig().getStringList("deathcoords")) {
				player.sendMessage(Lang.chat(str).replace("%world%", world.getName())
						.replace("%x%", String.valueOf(block.getX())).replace("%y%", String.valueOf(block.getY()))
						.replace("%z%", String.valueOf(block.getZ())));
			}
		}
	}

	public void setupDeathchest(DeathChest dc) {
		FileConfiguration dcConfig = plugin.getConfigs().getDchestsConfig();
//		Block block = dc.getLoc().getBlock();
//		Location loc = block.getLocation();

		droppedChests.put(dc.getLoc(), dc);
		dcInvs.put(dc.getLoc(), dc.getInv());

		String world = dc.getLoc().getWorld().getName();
		dcConfig.set("Deathchests." + dc.getUuid() + ".Owner", dc.getOwner());
		dcConfig.set("Deathchests." + dc.getUuid() + "." + world + ".X", dc.getLoc().getBlockX());
		dcConfig.set("Deathchests." + dc.getUuid() + "." + world + ".Y", dc.getLoc().getBlockY());
		dcConfig.set("Deathchests." + dc.getUuid() + "." + world + ".Z", dc.getLoc().getBlockZ());
		dcConfig.set("Deathchests." + dc.getUuid() + ".Drops", dc.getDrops());
		plugin.getConfigs().saveConfigs();
	}

	public void removeDeathChest(DeathChest dc) {
		if (dc != null) {
			plugin.getConfigs().getDchestsConfig().set("Deathchests." + dc.getUuid(), null);
			plugin.getConfigs().saveConfigs();

			droppedChests.remove(dc.getLoc());
			dcInvs.remove(dc.getLoc());
		}
	}

	public void clearDeathChests() {
		if (droppedChests == null)
			return;

		plugin.utils.resetDcLocs();
		droppedChests.forEach((loc, deathchest) -> removeDeathChest(deathchest));
	}
}
