package com.Furnesse.core.deathchest;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;

import com.Furnesse.core.Core;
import com.Furnesse.core.config.Message;

public class DeathChestListener implements Listener {

	Core plugin = Core.instance;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExplode(EntityExplodeEvent e) {
		Iterator<Block> it = e.blockList().iterator();
		while (it.hasNext()) {
			Block b = it.next();
			if (b.getState() instanceof Chest) {
				Chest c = (Chest) b.getState();
				if (DeathChestManager.getInstance().getDeathChestByLocation(c.getLocation()) != null) {
					it.remove();
				}
			}
		}
	}

	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		Player p = (Player) e.getPlayer();

		DeathChestManager.getInstance().removeFromOpenedInventories(p);

		DeathChest dc = DeathChestManager.getInstance().getDeathChestByInventory(inv);
		if (dc != null) {
			if (dc.isChestEmpty()) {
				dc.removeDeathChest(true);
			} else {
				p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 0.5F, 1.0F);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if (plugin.getSettings().usingDc) {
			if (!plugin.getSettings().disabledworlds.contains(p.getLocation().getWorld().getName())
					&& e.getDrops().size() > 0) {

				if ((e.getEntity().getLastDamageCause() != null
						&& e.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID
						&& !plugin.getSettings().voidSpawning)
						|| (p.getLocation().getBlock().getType() == Material.LAVA
								&& !plugin.getSettings().lavaSpawning)) {
					return;
				}

				if (DeathChestManager.getInstance().createDeathChest(p, e.getDrops())) {
					e.getDrops().clear();
					e.setKeepInventory(true);
					p.getInventory().setArmorContents(null);
					p.getInventory().clear();
					p.updateInventory();
				}
			}
		}

		if (p.hasPermission("core.deathcoords") || plugin.getSettings().usingDc) {
			p.sendMessage(
					Message.DEATHCOORDS.getChatMessage().replace("%xloc%", String.valueOf(p.getLocation().getBlockX()))
							.replace("%yloc%", String.valueOf(p.getLocation().getBlockY()))
							.replace("%zloc%", String.valueOf(p.getLocation().getBlockZ()))
							.replace("%world%", p.getLocation().getWorld().getName()));
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if (!(plugin.getSettings().expireTime == -1)) {
			return;
		}

		Player p = e.getPlayer();
		ArrayList<DeathChest> chests = DeathChestManager.getInstance().getPlayerDeathChests((OfflinePlayer) p);
		if (chests != null) {
			for (DeathChest dc : chests) {
				if (!dc.isAnnounced()) {
					dc.announce();
					dc.runRemoveTask();
				}
			}
		}
	}

	@EventHandler
	public void onDeathChestBreak(BlockBreakEvent e) {
		if (e.getBlock().getState() instanceof Chest) {
			Player p = e.getPlayer();
			DeathChest dc = DeathChestManager.getInstance().getDeathChestByLocation(e.getBlock().getLocation());
			if (dc != null) {
				e.setCancelled(true);
				p.sendMessage(Message.DEATHCHEST_CANNOT_BREAK.getChatMessage());

			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTryingOpen(PlayerInteractEvent e) {
		if (e.getClickedBlock() == null || !(e.getClickedBlock().getState() instanceof Chest)) {
			return;
		}

		Player p = e.getPlayer();
		Block b = e.getClickedBlock();

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			DeathChest dc = DeathChestManager.getInstance().getDeathChestByLocation(b.getLocation());
			if (dc != null) {
				e.setCancelled(true);

				if (p.isSneaking()) {
					dc.fastLoot(p);
					return;
				}

				p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5F, 1.0F);
				p.openInventory(dc.getChestInventory());
			}
		}
	}

	@EventHandler
	public void onHopperMove(InventoryMoveItemEvent e) {
		if (e.getDestination().getType() == InventoryType.HOPPER
				&& DeathChestManager.getInstance().isInventoryDeathChestInv(e.getSource()))
			e.setCancelled(true);
	}
}
