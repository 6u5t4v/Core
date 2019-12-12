package com.Furnesse.core.deathchest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.utils.Settings;

public class DeathChestManager {
	private static DeathChestManager ourInstance = new DeathChestManager();

	public HashMap<UUID, DeathChest> getDeathChestsByUUID() {
		return this.deathChestsByUUID;
	}

	private HashMap<UUID, ArrayList<DeathChest>> deathChests = new HashMap<>();
	private HashMap<UUID, DeathChest> deathChestsByUUID = new HashMap<>();
	private HashMap<Player, OfflinePlayer> openedInventories = new HashMap<>();

	public static DeathChestManager getInstance() {
		return ourInstance;
	}

	public ArrayList<DeathChest> getPlayerDeathChests(OfflinePlayer p) {
		return this.deathChests.get(p.getUniqueId());
	}

//	public void loadDeathChests() {
//		FileConfiguration file = Core.instance.getConfigs().getDchestsConfig();
//
//		int loaded = 0;
//		for (String chestUuid : Core.instance.getConfigs().getDchestsConfig().getConfigurationSection("chests")
//				.getKeys(false)) {
//			if (chestUuid != null) {
//				OfflinePlayer player = file.getOfflinePlayer("chests." + chestUuid + ".player");
//				Location loc = file.getLocation("chests." + chestUuid + ".location");
//				List<ItemStack> items = (List<ItemStack>) file.get("chests." + chestUuid + ".items");
//				int timeleft = file.getInt("chests." + chestUuid + ".timeleft");
//
//				createDeathChest(UUID.fromString(chestUuid), player, loc, timeleft, items);
//				loaded++;
//			}
//		}
//
//		Core.instance.getLogger().info("loaded " + loaded + " deathchests");
//	}

	public void removeDeathChest(DeathChest dc) {
		ArrayList<DeathChest> list = this.deathChests.get(dc.getOwner().getUniqueId());
		list.remove(dc);
		if (list.isEmpty()) {
			this.deathChests.remove(dc.getOwner().getUniqueId());
		} else {
			this.deathChests.put(dc.getOwner().getUniqueId(), list);
		}
		this.deathChestsByUUID.remove(dc.getChestUUID());
//		Core.instance.getConfigs().getDchestsConfig().set("chests." + dc.getChestUUID().toString(), null);
//		Core.instance.getConfigs().saveConfigs();
	}

	public void clearDeathChests() {
		for(ArrayList<DeathChest> list : deathChests.values()) {
			for(DeathChest dc : list) {
				dc.removeDeathChest(false);
			}
		}
	}
	
	public DeathChest getDeathChestByInventory(Inventory inv) {
		for (ArrayList<DeathChest> list : this.deathChests.values()) {
			for (DeathChest dc : list) {
				if (dc.getChestInventory().equals(inv)) {
					return dc;
				}
			}
		}
		return null;
	}

	public DeathChest getDeathChestByLocation(Location loc) {
		for (ArrayList<DeathChest> list : this.deathChests.values()) {
			for (DeathChest dc : list) {
				if (dc.getLocation().equals(loc)) {
					return dc;
				}
			}
		}
		return null;
	}

	public boolean isInventoryDeathChestInv(Inventory inv) {
		return (getDeathChestByInventory(inv) != null);
	}

	public static boolean isInventoryEmpty(Inventory inv) {
		for (ItemStack item : inv.getContents()) {
			if (item != null)
				return false;
		}
		return true;
	}

	public static int getAmountOfItems(Inventory inv) {
		int amount = 0;
		for (ItemStack item : inv.getContents()) {
			if (item != null) {
				amount++;
			}
		}
		return amount;
	}

	public boolean createDeathChest(Player p, List<ItemStack> drops) {
		if (this.deathChests.get(p.getUniqueId()) == null) {
			this.deathChests.put(p.getUniqueId(), new ArrayList<>());
		}

		ArrayList<DeathChest> currentChests = this.deathChests.get(p.getUniqueId());

		DeathChest dc = new DeathChest(p, drops);

		currentChests.add(dc);
		this.deathChests.put(p.getUniqueId(), currentChests);
		this.deathChestsByUUID.put(dc.getChestUUID(), dc);

		if (Settings.getExpireTime() != -1) {
			dc.announce();
			dc.runRemoveTask();
		}

		return true;
	}

	private boolean createDeathChest(UUID chestUuid, OfflinePlayer p, Location loc, int timeLeft,
			List<ItemStack> items) {
		if (this.deathChests.get(p.getUniqueId()) == null) {
			this.deathChests.put(p.getUniqueId(), new ArrayList<>());
		}

		ArrayList<DeathChest> currentChests = this.deathChests.get(p.getUniqueId());

		DeathChest dc = new DeathChest(chestUuid, p, loc, timeLeft, items);
		currentChests.add(dc);

		this.deathChests.put(p.getUniqueId(), currentChests);
		this.deathChestsByUUID.put(dc.getChestUUID(), dc);

		return true;
	}

	public DeathChest getDeathChest(String id) {
		return this.deathChestsByUUID.get(UUID.fromString(id));
	}

	public void removeFromOpenedInventories(Player p) {
		this.openedInventories.remove(p);
	}

	public OfflinePlayer getOpenedInventory(Player p) {
		return this.openedInventories.get(p);
	}
}
