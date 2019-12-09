package com.Furnesse.core.deathchest;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

public class DeathChest {

	private UUID chestUUID;
	private OfflinePlayer player;
	private Location location;
	private BlockState replacedBlock;
	private Inventory chestInventory;
	private BukkitTask removeTask;
	private boolean announced;
	private int timeLeft;

	public DeathChest(Player p, List<ItemStack> items) {
		this.chestUUID = UUID.randomUUID();
		this.player = (OfflinePlayer) p;
		this.timeLeft = DeathChests.getExpireTime();

		setupChest(p.getLocation(), items);

		this.announced = false;
	}

	public DeathChest(UUID chestUuid, OfflinePlayer p, Location loc, int timeLeft, List<ItemStack> items) {
		this.chestUUID = chestUuid;
		this.player = p;
		this.timeLeft = timeLeft;

		setupChest(loc, items);

		announce();
		runRemoveTask();
	}

	public ItemStack createListItem() {
		ItemStack returnItem = Items.DEATHCHEST_LIST_ITEM.getItemStack().clone();
		SkullMeta meta = (SkullMeta) returnItem.getItemMeta();
		meta.setOwner(this.player.getName());
		meta.setDisplayName(meta.getDisplayName().replaceAll("%player%", this.player.getName()));
		List<String> lore = meta.getLore();
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i,
					((String) lore.get(i)).replaceAll("%xloc%", String.valueOf(this.location.getBlockX()))
							.replaceAll("%yloc%", String.valueOf(this.location.getBlockY()))
							.replaceAll("%zloc%", String.valueOf(this.location.getBlockZ()))
							.replaceAll("%world%", this.location.getWorld().getName()));
		}
		meta.setLore(lore);
		returnItem.setItemMeta(meta);
		return returnItem;
	}

	public OfflinePlayer getOwner() {
		return this.player;
	}

	private void setupChest(Location loc, List<ItemStack> items) {
		if (DeathChests.isSpawnChestOnHighestBlock() || loc.getY() <= 0.0D) {
			loc = loc.getWorld().getHighestBlockAt(loc).getLocation();
		}

		if (loc.getY() > 255.0D) {
			loc.setY(255.0D);
		}

		if (loc.getBlock().getType() == Material.CHEST) {
			loc.setY(loc.getY() + 1.0D);
		}

		this.replacedBlock = loc.getBlock().getState();

		if (this.replacedBlock.getType() == Material.CHEST) {
			this.replacedBlock.setType(Material.AIR);
		}

		loc.getBlock().setType(Material.CHEST);
		this.location = loc.getBlock().getLocation();

		this.chestInventory = Bukkit.createInventory(null, (items.size() > 27) ? 54 : 27,
				Lang.chat(DeathChests.getDeathChestInvTitle().replaceAll("%player%", this.player.getName())));

		for (ItemStack i : items) {
			if (i == null)
				continue;
			this.chestInventory.addItem(new ItemStack[] { i });
		}
	}

	public void runRemoveTask() {
		if (this.timeLeft == -1) {
			return;
		}

		this.removeTask = (new BukkitRunnable() {
			public void run() {
				if (DeathChest.this.timeLeft == 0) {
					DeathChest.this.removeDeathChest(true);
					cancel();
				} else {
					DeathChest.this.timeLeft--;
					if (DeathChest.this.location.getBlock().getType() != Material.CHEST) {
						DeathChest.this.location.getBlock().setType(Material.CHEST);
						DeathChest.this.location.getBlock().getState().update(true);
					}
				}
			}

		}).runTaskTimer((Plugin) Core.instance, 20L, 20L);

	}

	public boolean isChestEmpty() {
		return DeathChestManager.isInventoryEmpty(this.chestInventory);
	}

	private void removeChests(boolean closeInventories) {
		if (closeInventories) {
			for (HumanEntity entity : this.chestInventory.getViewers()) {
				entity.closeInventory();
			}
		}

		if (DeathChests.isDropItemsAfterExpire()) {
			for (ItemStack item : this.chestInventory.getContents()) {
				if (item != null) {
					this.location.getWorld().dropItemNaturally(this.location, item);
				}
			}
			this.chestInventory.clear();
			this.location.getWorld().playSound(this.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
		}

		this.replacedBlock.update(true);
	}

	public void removeDeathChest(boolean closeInventories) {
		if (this.removeTask != null) {
			this.removeTask.cancel();
		}

		removeChests(closeInventories);

		if (this.player.isOnline()) {
			this.player.getPlayer().sendMessage(Lang.DEATHCHEST_DISSAPEARED);
		}

		DeathChestManager.getInstance().removeDeathChest(this);
	}

	public boolean isAnnounced() {
		return this.announced;
	}

	public void announce() {
		if (this.location.getBlock().getType() != Material.CHEST) {
			this.location.getBlock().setType(Material.CHEST);
		}

		if (!getOwner().isOnline()) {
			return;
		}

		this.player.getPlayer()
				.sendMessage(Lang.DEATHCOORDS.replace("%xloc%", String.valueOf(location.getBlockX()))
						.replace("%yloc%", String.valueOf(location.getBlockY()))
						.replace("%zloc%", String.valueOf(location.getBlockZ()))
						.replace("%world%", location.getWorld().getName()));

		if (this.timeLeft != -1) {
			this.player.getPlayer()
					.sendMessage(Lang.DEATHCHEST_EXPIRE_TIME.replace("%time%", String.valueOf(this.timeLeft)));
		}

		this.announced = true;
	}

	public void fastLoot(Player p) {
		if (p.hasPermission("core.dc.fastloot")) {
			for (ItemStack i : this.chestInventory.getContents()) {
				if (i != null) {
					if (p.getInventory().firstEmpty() == -1) {
						break;
					}

					if (DeathChests.isAutoEquipArmor()
							&& (Items.isHelmet(i.getType()) || Items.isChestPlate(i.getType())
									|| Items.isLeggings(i.getType()) || Items.isBoots(i.getType()))) {
						if (!autoEquip(p, i)) {
							p.getInventory().addItem(new ItemStack[] { i });
						}
						this.chestInventory.remove(i);
					} else {

						this.chestInventory.remove(i);
						p.getInventory().addItem(new ItemStack[] { i });
					}
				}
			}
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

			if (isChestEmpty()) {
				removeDeathChest(true);
			} else {
				p.sendMessage(Lang.DEATHCHEST_FASTLOOT_COMPLETE.replace("%amount%", String.valueOf(DeathChestManager.getAmountOfItems(this.chestInventory))));
			}
		} else {
			
			p.sendMessage(Lang.NO_PERMISSION);
		}
	}

	private boolean autoEquip(Player p, ItemStack i) {
		if (Items.isHelmet(i.getType()) && p.getInventory().getHelmet() == null) {
			p.getInventory().setHelmet(i);
			return true;
		}
		if (Items.isChestPlate(i.getType()) && p.getInventory().getChestplate() == null) {
			p.getInventory().setChestplate(i);
			return true;
		}
		if (Items.isLeggings(i.getType()) && p.getInventory().getLeggings() == null) {
			p.getInventory().setLeggings(i);
			return true;
		}
		if (Items.isBoots(i.getType()) && p.getInventory().getBoots() == null) {
			p.getInventory().setBoots(i);
			return true;
		}
		return false;
	}

	public UUID getChestUUID() {
		return this.chestUUID;
	}

	public Inventory getChestInventory() {
		return this.chestInventory;
	}

	public Location getLocation() {
		return this.location;
	}

	public void save() {
		Core.instance.getConfigs().getDchestsConfig().set("chests." + this.chestUUID.toString() + ".location",
				this.location.serialize());
		Core.instance.getConfigs().getDchestsConfig().set("chests." + this.chestUUID.toString() + ".player",
				getOwner().getUniqueId().toString());
		Core.instance.getConfigs().getDchestsConfig().set("chests." + this.chestUUID.toString() + ".items",
				this.chestInventory.getContents());
		Core.instance.getConfigs().getDchestsConfig().set("chests." + this.chestUUID.toString() + ".timeleft",
				Integer.valueOf(this.timeLeft));
		Core.instance.getConfigs().saveConfigs();
	}

	public Player getPlayer() {
		return this.player.getPlayer();
	}

	public void removeChest() {
		this.location.getBlock().setType(Material.AIR);
	}

	public int getTimeLeft() {
		return this.timeLeft;
	}

	public int getItemCount() {
		int i = 0;
		for (ItemStack item : this.chestInventory.getContents()) {
			if (item != null) {
				i++;
			}
		}
		return i;
	}
}
