package com.Furnesse.core.common;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.Furnesse.core.Core;

public class SkullBuilder extends ItemstackBuilder {

	private String owner;

	private static ItemStack getHeadType(String owner) {
		ItemStack stack = owner.startsWith("hdb-") && Core.instance.getHeadAPI().getItemHead(owner.split("-")[1])!= null ? Core.instance.getHeadAPI().getItemHead(owner) : new ItemStack(Material.PLAYER_HEAD, 1);
		return stack;
	}
	
	protected SkullBuilder(String owner) {
		super(getHeadType(owner));

		this.owner = owner;
	}

	protected SkullBuilder(ItemStack stack) {
		super(stack);
	}

	protected SkullBuilder() {
		this((String) null);
	}

	@Override
	public ItemStack build() {
		ItemStack stack = super.build();
		SkullMeta meta = (SkullMeta) stack.getItemMeta();

		if (owner != null) {
			if(owner.startsWith("hdb-")) {
				String[] head = owner.split("-", 2);
				try {
					stack = Core.instance.getHeadAPI().getItemHead(head[1]);
				} catch (Exception e) {
					Core.instance.getLogger().log(Level.SEVERE, "Cant find a head with id " + head[1], e);
				}
			}else {
				meta.setOwner(owner);				
				stack.setItemMeta(meta);
			}
		}


		// NBTs must be applied AFTER meta is applied.
		for (Applier applier : this.getPersistentAppliers()) {
			stack = applier.apply(stack);
		}

		return stack;
	}

	/**
	 * @param owner skull owner (player name).
	 * @return this skull builder.
	 */
	public SkullBuilder withOwner(String owner) {
		this.owner = owner;
		return this;
	}
	
	public SkullBuilder withHDBId(String hdbId) {
		this.owner = hdbId;
		return this;
	}

	/**
	 * @param player skull owner.
	 * @return this skull builder.
	 */
	public SkullBuilder withOwner(OfflinePlayer player) {
		this.owner = player.getName();
		return this;
	}

	/**
	 * @param uuid skull owner's unique id.
	 * @return this skull builder.
	 */
	public SkullBuilder withOwner(UUID uuid) {
		this.owner = Bukkit.getOfflinePlayer(uuid).getName();
		return this;
	}
}
