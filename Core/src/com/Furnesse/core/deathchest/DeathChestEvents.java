package com.Furnesse.core.deathchest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Debug;
import com.Furnesse.core.utils.Lang;

public class DeathChestEvents implements Listener {
	Core plugin;

	public DeathChestEvents(Core plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		World world = player.getWorld();

		if (plugin.usingDc) {
			if (plugin.dcEnabledWorlds.contains(world.getName())) {
				int numDrops = e.getDrops().size();
				if (numDrops >= plugin.minItems) {					
					List<ItemStack> drops = new ArrayList<>();
					for(ItemStack item : e.getDrops()) {
						drops.add(item);
					}
					e.getDrops().clear();
					
					plugin.getDeathChest().spawnDeathChest(
							player, 
							player.getName(), 
							player.getUniqueId().toString(),
							world, 
							player.getLocation(), 
							drops);
				} else {
					player.sendMessage(
							"§cNot enough items to generate deathchest (min itemstacks = 5) \n§cHurry up and retrieve them!");
					return;
				}
			}
		} else {
			if (player.hasPermission("core.deathcoords")) {
				Block block = e.getEntity().getLocation().getBlock();
				for (String str : plugin.getConfigs().getLangConfig().getStringList("deathcoords")) {
					player.sendMessage(Lang.chat(str).replace("%world%", world.getName())
							.replace("%x%", String.valueOf(block.getX())).replace("%y%", String.valueOf(block.getY()))
							.replace("%z%", String.valueOf(block.getZ())));
				}
			}
		}
	}

	@EventHandler
	public void onDcInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null || e.getClickedBlock().getType() == Material.AIR)
			return;
		
		Block block = e.getClickedBlock();
		Location bLoc = block.getLocation();
		
		if (plugin.getDeathChest().droppedChests.get(bLoc) != null) {
			e.setCancelled(true);
			DeathChest dc = plugin.getDeathChest().droppedChests.get(bLoc);
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Player player = e.getPlayer();

				player.openInventory(dc.getInv());
			}

			if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				Player player = e.getPlayer();

				player.sendMessage("Owner of deatchest: " + dc.getOwner());
				player.sendMessage("Amount of items: " + dc.getDrops().size());
			}
		}
	}

	@EventHandler
	public void onDcBreakBlock(BlockBreakEvent e) {
		Block block = e.getBlock();

		if (plugin.getDeathChest().droppedChests.get(block.getLocation()) != null) {
			e.setCancelled(true);
			if (e.getPlayer() != null) {
				Player player = e.getPlayer();
				block.getState().update();
				player.sendMessage("§cYou cannont destroy han active");
			}
		}
	}

	@EventHandler
	public void onDcInvMove(InventoryClickEvent e) {
		Location loc = plugin.getDeathChest().dcInvs.inverse().get(e.getInventory());

		if (plugin.getDeathChest().droppedChests.get(loc) != null) {

			if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
				if (e.getCurrentItem() == null)
					return;
				if (e.getCurrentItem().getType() == Material.AIR)
					return;

				if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
				}
				e.setCancelled(true);
			}

			if (e.getClickedInventory().equals(plugin.getDeathChest().droppedChests.get(loc).getInv())) {
				if (e.getAction() == InventoryAction.HOTBAR_SWAP)
					e.setCancelled(true);

				if (e.getAction() == InventoryAction.CLONE_STACK)
					e.setCancelled(true);

			}
		}
	}

	@EventHandler
	public void onDcExit(InventoryCloseEvent e) {
		Location loc = plugin.getDeathChest().dcInvs.inverse().get(e.getInventory());

		if (plugin.getDeathChest().droppedChests.get(loc) != null) {
			DeathChest dc = plugin.getDeathChest().droppedChests.get(loc);
			final int oldItemAmount = dc.getDrops().size();

			ItemStack[] invContents = Stream.of(e.getInventory().getContents()).filter(Objects::nonNull)
					.toArray(ItemStack[]::new);
			
			if (invContents.length != oldItemAmount) {
				Debug.Log("not same as when opened " + invContents.length);
				List<ItemStack> drops = new ArrayList<>();
				e.getInventory().forEach((item) -> drops.add(item));
				dc.setDrops(drops);
				plugin.getConfigs().getDchestsConfig().set("Deathchests." + dc.getUuid() + ".Drops", dc.getDrops());
				Debug.Log("old amount: " + oldItemAmount + "\n now: " + dc.getDrops().size());
			}

			if (dc.getInv().getContents().length == 0) {
				// Send message to owner of chest as this dc has been claimed

				plugin.getDeathChest().removeDeathChest(dc);
			}
		}

	}
}
