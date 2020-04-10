package com.Furnesse.core.customitems;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.Furnesse.core.common.ItemStacks;
import com.Furnesse.core.common.ItemstackBuilder;

public class BigChunkMeat extends CustomItemstack implements Listener {
	private static final BigChunkMeat instance = new BigChunkMeat();

	private ItemstackBuilder bigchunkmeat;

	public String keyValue = "bigchunkmeat";

	public static BigChunkMeat get() {
		return instance;
	}

	@Override
	public void register() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("items.bigchunkmeat");

		this.bigchunkmeat = ItemStacks.toItemStackBuilder(section)
				.withItemFlag(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS })
				.withPDCString(key, keyValue);
		
		if (section.getBoolean("glow")) {
			this.bigchunkmeat.withEnchantment(Enchantment.OXYGEN, 1);
		}

		if (section.getBoolean("recipe.enabled")) {
			Bukkit.addRecipe(getRecipe(section, keyValue));
		}
	}

	@Override
	public ItemStack getItemstack() {
		return this.bigchunkmeat.build();
	}

	@EventHandler
	public void consumeMeat(PlayerItemConsumeEvent e) {
		if (isHoldingItem(e.getItem(), keyValue)) {
			e.getPlayer().setFoodLevel(20);
		}
	}
}
