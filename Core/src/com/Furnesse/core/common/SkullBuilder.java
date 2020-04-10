package com.Furnesse.core.common;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullBuilder extends ItemstackBuilder {

	private static final String TEXTURES_JSON = "{ textures: { SKIN: { url: \"%s\" } } }";

	private String owner;

	protected SkullBuilder(String owner) {
		super(new ItemStack(Material.PLAYER_HEAD, 1));

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
			meta.setOwner(owner);
		}

		stack.setItemMeta(meta);

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
