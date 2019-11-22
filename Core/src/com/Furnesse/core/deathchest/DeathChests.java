package com.Furnesse.core.deathchest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
import com.Furnesse.core.utils.Debug;
import com.Furnesse.core.utils.Lang;
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
							setupDeathChest(null, uuid, owner, drops, loc);
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

	public void removeOldDeathChest(OfflinePlayer victim) {
		FileConfiguration locations = plugin.getConfigs().getLocsConfig();
		String uuid = victim.getUniqueId().toString();

		DeathChest dc = getDeathChestByOwner(victim.getName());

		plugin.utils.resetDcLoc(victim);

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

	public void setupDeathChest(Player player, String dcUuid, String name, ItemStack[] drops, Location loc) {
		Block block = loc.getBlock();
		Location bLoc = block.getLocation();
		if (block.getType() != Material.AIR || block.getType() != null)
			plugin.utils.getPlaceholders().put(bLoc, block.getType());

		block.setType(Material.CHEST);

		Inventory inventory = Bukkit.createInventory(null, drops.length <= 27 ? 27 : drops.length <= 45 ? 45 : 54,
				Lang.chat("&c" + name + "'s &8Death chest"));

		inventory.setContents(drops);

		if (player != null) {
			player.getInventory().clear();
		}

		droppedChests.put(bLoc, inventory);
		deathChests.add(new DeathChest(dcUuid, name, bLoc, drops));
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

			setupDeathChest(victim, victim.getUniqueId().toString(), victim.getName(), drops, loc);

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
		}
	}

//	  Events has been disabled COMING SOON NEEDS TO BE FIXED

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
				e.getPlayer().sendMessage(Lang.chat("&cYou cant break deathchests"));
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onMoveItems(InventoryClickEvent e) {
		Location location = droppedChests.inverse().get(e.getInventory());
		if (location == null) {
			return;
		}

		// THE Debug above gave this

//		HOTBAR_SWAP
//		[16:24:18 INFO]: NUMBER_KEY getClick
//		[16:24:18 INFO]: CHEST inv

		// meaning now we have top check for the hotbar swap in the chest inventory and
		// make sure its the chest that we need to (deathchest)
		if (e.getInventory().equals(droppedChests.get(location))) {

			if (e.getClickedInventory() == null)
				return;

			if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
				if (e.getCurrentItem() == null)
					return;
				if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null)
					return;
				
				Debug.Log("click player inventory");
				if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
					Debug.Log("we out here");
					return;
				}
				e.setCancelled(true);
			}

			if (e.getClickedInventory().equals(droppedChests.get(location))) {
				Debug.Log("click player deathchest inv");
				if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
					Debug.Log("click player inventory");
					e.setCancelled(true);
				}

			}
		}
	}

	@EventHandler
	public void onDeathChestClose(InventoryCloseEvent e) {
		Location location = droppedChests.inverse().get(e.getInventory());
		if (location == null)
			return;

		Debug.Log("location is not null");
		DeathChest dc = getDeathChestByLoc(location);
		Debug.Log("dc made");
		
		Player player = (Player) e.getPlayer();		

		if (dc == null)
			return;
		
		Debug.Log("dc is not null");

		ItemStack[] items = Stream.of(e.getInventory().getContents()).filter(Objects::nonNull)
				.toArray(ItemStack[]::new);
		Debug.Log("Successully loaded chest contents");

		if (items.length > 0) {
			Debug.Log("out here 4");
			
			if (!items.equals(dc.getDrops())) {
				Debug.Log("out here 5");
				
				dc.setDrops(items);
				plugin.getConfigs().getLocsConfig().set("Locations." + dc.getUuid() + ".drops", items);
				plugin.getConfigs().saveConfigs();
				Debug.Log("out here 6");
			}
		}

		if (items.length == 0) {
			Debug.Log("out here 7");
			try {
				Player chestOwner = Bukkit.getServer().getPlayer(UUID.fromString(dc.getUuid()));
				if (Bukkit.getPlayer(dc.getOwner()) != null) {
					if (!chestOwner.equals(player)) {
						chestOwner.sendMessage("Your deathchest has been claimed by " + player.getName());
					}
				}
				Debug.Log("out here 9");
				removeOldDeathChest(chestOwner);
				Debug.Log("out here 10");
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}

	}

	public void removeAll() {
		plugin.utils.resetDcLocs();

		plugin.getConfigs().getLocsConfig().set("Locations", null);
		plugin.getConfigs().saveConfigs();

		Bukkit.broadcastMessage("Removed " + deathChests.size() + " has been removed");
		deathChests.clear();
		droppedChests.clear();
	}
}
