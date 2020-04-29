package com.Furnesse.core.invitem;

import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.Furnesse.core.Core;
import com.Furnesse.core.common.ItemStacks;
import com.Furnesse.core.common.ItemstackBuilder;
import com.Furnesse.core.utils.Utils;

public class InvItemManager implements Listener {

	Core plugin;

	public InvItemManager(Core plugin) {
		this.plugin = plugin;
	}

	public HashMap<Integer, InvItem> invitems = new HashMap<>();

	public void register() {
		FileConfiguration file = plugin.getFileManager().getConfig("config.yml").get();

		if (file.getBoolean("static_inv_items.enabled")) {

			ConfigurationSection section = file.getConfigurationSection("static_inv_items.items");
			for (String item : section.getKeys(false)) {
				if (item != null) {

					section = section.getConfigurationSection(item);

					ItemstackBuilder builder;
					builder = ItemStacks.toItemStackBuilder(section).withPDCString(plugin.customitemKey, item)
							.withItemFlag(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

					if (!section.getStringList("enchantments").isEmpty()) {
						for (String ench : section.getStringList("enchantments")) {
							String[] split = ench.split(",");
							String enchant = split[0];
							int level = Integer.valueOf(split[1]);

							Enchantment enchantment = Utils.isEnchantment(enchant);
							if (enchantment != null) {
								builder.withEnchantment(enchantment, level);
							}
						}
					}

					int slot = section.getInt("slot");
					List<String> cmds = section.getStringList("click_commands");

					invitems.put(slot, new InvItem(builder.build(), cmds));
				}
			}
		}
	}

	public void setInvItems(Player p) {
		invitems.forEach((slot, invitem) ->
		{
			p.getInventory().setItem(slot, invitem.getItem());
		});
	}

	@EventHandler
	public void invItemInteract(InventoryClickEvent e) {
		if (e.getClickedInventory() == null || e.getCurrentItem() == null)
			return;

		if (invitems.containsKey(e.getSlot())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void invItemInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			ItemStack stack = e.getItem();
			if (!ItemStacks.toPDCItemStack(stack).has(plugin.customitemKey, PersistentDataType.STRING))
				return;

			if (ItemStacks.toPDCItemStack(stack).get(plugin.customitemKey, PersistentDataType.STRING).equals("menu")) {
				e.setCancelled(true);
				return;
			}
		}
	}
}
