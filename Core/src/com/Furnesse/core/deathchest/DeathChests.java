package com.Furnesse.core.deathchest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

	public BiMap<Location, Inventory> dcInvs = HashBiMap.create();
	public Map<Location, DeathChest> droppedChests = new HashMap<>();
//	public List<DeathChest> deathChests = new ArrayList<DeathChest>();

	public void spawnDeathChest(Player player, List<ItemStack> drops, World world) {

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		World world = player.getWorld();

		if (plugin.usingDc) {
			if (plugin.dcEnabledWorlds.contains(world.getName())) {
				int numDrops = e.getDrops().size();

				if (numDrops >= plugin.minItems) {
					Inventory dcInv;
					dcInv = Bukkit.createInventory(null, numDrops <= 27 ? 27 : numDrops <= 45 ? 45 : 54,
							Lang.chat("&c" + player.getName() + "'s &8Death chest"));

					for (ItemStack item : e.getDrops()) {
						if (item == null)
							continue;
						if (item.getType() == Material.AIR)
							continue;

						dcInv.addItem(item);
					}

					e.getDrops().clear();

					Block block = world.getBlockAt(player.getLocation());

					if (block.getType() != Material.AIR) {
						plugin.utils.getPlaceholders().put(block.getLocation(), block.getType());
						Debug.Log("saved previous block");
					}

					block.setType(Material.PLAYER_HEAD);

					Skull skull = (Skull) block.getState();
					skull.setOwner(player.getName());
					skull.update();

					DeathChest deathchest = new DeathChest(player.getUniqueId().toString(), player.getName(),
							block.getLocation(), dcInv.getContents(), dcInv);

					droppedChests.put(block.getLocation(), deathchest);
					dcInvs.put(block.getLocation(), dcInv);

					player.sendMessage("§c§lYou died at §e" + player.getLocation().getBlockX() + ", "
							+ player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ());
				} else {
					player.sendMessage(
							"§cNot enough items to generate deathchest (min itemstacks = 5) \n§cHurry up and retrieve them!");
					return;
				}
			}
		}
	}

	public void removeDeathChest(DeathChest dc) {
		droppedChests.remove(dc.getLoc());
		dcInvs.remove(dc.getLoc());
	}

	public void clearDeathChests() {
		if (droppedChests == null)
			return;

		plugin.utils.resetDcLocs();

		droppedChests.clear();
	}

	@EventHandler
	public void onDcInteract(PlayerInteractEvent e) {
		Block block = e.getClickedBlock();
		Location bLoc = block.getLocation();

		if (droppedChests.get(bLoc) != null) {
			e.setCancelled(true);
			DeathChest dc = droppedChests.get(bLoc);
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Player player = e.getPlayer();

				player.openInventory(dc.getInv());
			}

			if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				Player player = e.getPlayer();

				player.sendMessage("Owner of deatchest: " + dc.getOwner());
				player.sendMessage("Amount of items: " + dc.getDrops().length);
			}
		}
	}

	@EventHandler
	public void onDcBreakBlock(BlockBreakEvent e) {
		Block block = e.getBlock();

		if (droppedChests.get(block.getLocation()) != null) {
			e.setCancelled(true);
			if (e.getPlayer() != null) {
				Player player = e.getPlayer();
				player.sendMessage("§cYou cannont destroy han active");
			}
		}
	}

	@EventHandler
	public void onDcInvMove(InventoryClickEvent e) {
		Location loc = dcInvs.inverse().get(e.getInventory());

		if (droppedChests.get(loc) != null) {

			if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
				if (e.getCurrentItem() == null)
					return;
				if (e.getCurrentItem().getType() == Material.AIR)
					return;

				if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
				}
				e.setCancelled(true);
			}

			if (e.getClickedInventory().equals(droppedChests.get(loc).getInv())) {
				if (e.getAction() == InventoryAction.HOTBAR_SWAP)
					e.setCancelled(true);

				if (e.getAction() == InventoryAction.CLONE_STACK)
					e.setCancelled(true);

			}
		}
	}

	@EventHandler
	public void onDcExit(InventoryCloseEvent e) {
		Location loc = dcInvs.inverse().get(e.getInventory());

		if (droppedChests.get(loc) != null) {
			DeathChest dc = droppedChests.get(loc);
			final int oldItemAmount = dc.getDrops().length;

			Debug.Log("old amount: " + oldItemAmount + "\n now: " + e.getInventory().getContents().length);

			if (e.getInventory().getContents().length != oldItemAmount) {
				// Update items in config
				dc.setDrops(e.getInventory().getContents());
				Debug.Log("NEW AMOUNT:" + dc.getDrops().length);
			}

			if (dc.getInv().getContents().length == 0) {
				// Send message to owner of chest as this dc has been claimed

				removeDeathChest(dc);
			}
		}

	}
}
