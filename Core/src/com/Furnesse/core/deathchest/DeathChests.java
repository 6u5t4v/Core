package com.Furnesse.core.deathchest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;
import com.Furnesse.core.utils.Utils;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class DeathChests implements Listener {

	Core plugin;

	public DeathChests(Core plugin) {
		this.plugin = plugin;
	}

	public static BiMap<Location, Inventory> droppedChests = HashBiMap.create();
//	public Map<Location, Inventory> droppedChests = new HashMap<>();
	public List<DeathChest> deathChests = new ArrayList<DeathChest>();

	public void loadDeathChests() {
		FileConfiguration locsConfig = plugin.getConfigs().getLocsConfig();
		droppedChests.clear();
		deathChests.clear();

		ConfigurationSection locations = locsConfig.getConfigurationSection("Locations");
		int loaded = 0;
		if (locations != null) {
			for (String uuid : locations.getKeys(false)) {
				if (uuid != null) {
					try {
						String worldName = plugin.getConfigs().getLocsConfig()
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
//							System.out.println("Current item:" + item.getType() + " " + item.getAmount());
							items.add(item);
						}
						ItemStack[] drops = items.toArray(new ItemStack[0]);
						String owner = locsConfig.getString("Locations." + uuid + ".owner");
						if (world != null) {
							Location loc = new Location(world, x, y, z);
							setupDeathChest(null, owner, drops, loc);
							deathChests.add(new DeathChest(owner, loc, drops));
							loaded++;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
//			int placed = 0;
//			if (placed == loaded) {
//				plugin.getLogger().info("All deathchests has been successfully loaded");
//			} else {
//				plugin.getLogger().severe("Not all deathchests got loaded!?");
//			}
			plugin.getLogger().info("Loaded a total of: " + loaded + " deathchests");
		}
	}

	public void removeOldDeathChest(OfflinePlayer victim) {
		FileConfiguration locations = plugin.getConfigs().getLocsConfig();
		String uuid = victim.getUniqueId().toString();

		DeathChest dc = getDeathChestByOwner(victim.getName());

		if (Utils.getPlaceholders().get(dc.getLoc()) != null) {
			dc.getLoc().getBlock().setType(Utils.getPlaceholders().get(dc.getLoc()));
			Utils.getPlaceholders().remove(dc.getLoc());
		} else {
			dc.getLoc().getBlock().setType(Material.AIR);
		}

		locations.set("Locations." + uuid, null);
		plugin.getConfigs().saveConfigs();

		droppedChests.remove(dc.getLoc());
		deathChests.remove(dc);
	}

	public DeathChest getDeathChestByOwner(String name) {
		for (DeathChest dc : deathChests) {
			if (dc.getOwner().equals(name)) {
				return dc;
			}
		}
		return null;
	}

	public DeathChest getDeathChestByLoc(Location loc) {
		for (DeathChest dc : deathChests) {
			if (dc.getLoc().equals(loc)) {
				return dc;
			}
		}
		return null;
	}

	public boolean hasDeathChest(Player player) {
		FileConfiguration locations = plugin.getConfigs().getLocsConfig();
		if (locations.contains("Locations." + player.getUniqueId().toString())) {
			return true;
		}
		return false;
	}

	public void setupDeathChest(Player player, String name, ItemStack[] drops, Location loc) {
		Block block = loc.getBlock();
		Location bLoc = block.getLocation();
		if (block.getType() != Material.AIR)
			Utils.getPlaceholders().put(bLoc, block.getType());

		block.setType(Material.CHEST);

		Inventory inventory = Bukkit.createInventory(null, drops.length <= 27 ? 27 : drops.length <= 45 ? 45 : 54,
				Lang.chat("&c" + name + "'s &8Death chest"));

		inventory.setContents(drops);

		if (player != null) {
			player.getInventory().clear();
		}

		droppedChests.put(bLoc, inventory);
		System.out.println("Successfully set the deathchest at: " + loc.getBlockX() + " " + loc.getBlockY() + " "
				+ loc.getBlockZ() + droppedChests.get(bLoc).getLocation());
	}

	public void createDeathChest(Player victim, ItemStack[] drops) {
		FileConfiguration locations = plugin.getConfigs().getLocsConfig();
		Block block = victim.getLocation().getBlock();
		Location loc = block.getLocation();

//		ItemStack[] drops = Stream.of(victim.getInventory().getContents()).filter(Objects::nonNull)
//				.toArray(ItemStack[]::new);

		if (drops.length >= plugin.minItems) {
			if (hasDeathChest(victim)) {
				removeOldDeathChest(victim);
				victim.sendMessage("Your new deathchest has replaced your old one.");
			}

			setupDeathChest(victim, victim.getName(), drops, loc);

			victim.sendMessage("§7You can find your deathchest at: §ax" + loc.getBlockX() + "§7, §ay" + loc.getBlockY()
					+ "§7, §az" + loc.getBlockZ() + "§7. §cIf you die again this chest will be removed");

			String uuid = victim.getUniqueId().toString();

			locations.set("Locations." + uuid + ".location.world", loc.getWorld().getName());
			locations.set("Locations." + uuid + ".location.x", loc.getX());
			locations.set("Locations." + uuid + ".location.y", loc.getY());
			locations.set("Locations." + uuid + ".location.z", loc.getZ());
			locations.set("Locations." + uuid + ".owner", victim.getName());
			List<ItemStack> items = new ArrayList<ItemStack>();
			for (ItemStack item : drops) {
				items.add(item);
			}
			locations.set("Locations." + uuid + ".drops", items);
			plugin.getConfigs().saveConfigs();
			deathChests.add(new DeathChest(victim.getName(), loc, drops));
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			if (block.getType() == Material.CHEST) {
				Inventory deathChest = droppedChests.get(block.getLocation());
				if (deathChest == null) {
					return;
				}
				e.setCancelled(true);
				if (deathChest.getViewers().size() == 0) {
					e.getPlayer().openInventory(deathChest);

				} else {
					for (HumanEntity viewer : deathChest.getViewers()) {
						e.getPlayer().sendMessage("This chest is currently occupied by " + viewer.getName());
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		if (block.getType() == Material.CHEST) {
			Inventory deathChest = droppedChests.get(block.getLocation());
			if (deathChest != null) {
				e.getPlayer().sendMessage("You cant break deathchests");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDeathChestInteract(InventoryClickEvent e) {
		Location location = droppedChests.inverse().get(e.getInventory());
		if (location == null) {
			return;
		}

		if (e.getInventory().equals(droppedChests.get(location))) {
			if (e.getCurrentItem() != null || e.getCurrentItem().getType() != Material.AIR)
				return;

			if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
				if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					e.getWhoClicked().sendMessage("dfghd");
					e.setCancelled(true);
				}
			}

			if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				e.getWhoClicked().sendMessage("yewet");
			}
		}
	}

	@EventHandler
	public void onDeathChestClose(InventoryCloseEvent e) {
		Location location = droppedChests.inverse().get(e.getInventory());

		if (location != null) {
			long items = Stream.of(e.getInventory().getContents()).filter(Objects::nonNull).count();

			if (items == 0) {
				Player player = (Player) e.getPlayer();
					
				OfflinePlayer chestOwner;
				
				if (chestOwner != null) {
					if (chestOwner.getName() != player.getName()) {

						chestOwner.sendMessage("Your chest has been claimed by " + player.getName());
					}
				}

				removeOldDeathChest(chestOwner);

			}
		}
	}

	public void removeAll() {
		for (DeathChest dc : deathChests) {
			Location loc = dc.getLoc();
			if (Utils.getPlaceholders().get(loc) != null) {
				loc.getBlock().setType(Utils.getPlaceholders().get(loc));
				Utils.getPlaceholders().remove(loc);
				continue;
			}
			loc.getBlock().setType(Material.AIR);

		}

		plugin.getConfigs().getLocsConfig().set("Locations", null);
		plugin.getConfigs().saveConfigs();

		Bukkit.broadcastMessage("Removed " + deathChests.size() + " has been removed");
		deathChests.clear();
		droppedChests.clear();
	}
}
