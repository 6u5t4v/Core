package com.Furnesse.core.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemStacks {
	public static ItemstackBuilder builder(Material material) {
		return material == Material.PLAYER_HEAD ? new SkullBuilder() : new ItemstackBuilder(material);
	}

	/**
	 * Returns a new object of the provided item stack builder, with the same
	 * attributes.
	 *
	 * @param builder the builder to copy.
	 * @return a copy of the provided item stack builder.
	 *
	 * @see ItemStackBuilder
	 */
	public static ItemstackBuilder builder(ItemstackBuilder builder) {
		return new ItemstackBuilder(builder);
	}

	/**
	 * Wraps the provided stack into an item stack builder.
	 *
	 * @param stack the stack to wrap into a builder.
	 * @return wrapped item stack builder.
	 *
	 * @see ItemStackBuilder
	 */
	public static ItemstackBuilder builder(ItemStack stack) {
		return stack.getType() == Material.PLAYER_HEAD ? new SkullBuilder(stack) : new ItemstackBuilder(stack);
	}

	/**
	 * @return item stack builder with type {@link Material#AIR}.
	 *
	 * @see ItemStackBuilder
	 */
	public static ItemstackBuilder builder() {
		return builder(Material.AIR);
	}

	public static SkullBuilder skullBuilder(String owner) {
		return new SkullBuilder(owner);
	}

	/**
	 * @return blank skull builder.
	 *
	 * @see SkullBuilder
	 */
	public static SkullBuilder skullBuilder() {
		return skullBuilder(null);
	}

	/**
	 * @param section resource section to unwrap.
	 * @return item stack builder with attributes provided by the resource section.
	 *
	 * @see ResourceSection
	 * @see ItemStackBuilder
	 */
	public static ItemstackBuilder toItemStackBuilder(ConfigurationSection section) {
		if (section == null) {
			return null;
		}

		int amount = section.getInt("amount", 1);

		String material = section.getString("material", "AIR");
		if (material.startsWith("hdb-") || material.startsWith("player-")) {
			return skullBuilder(section.getString("material"))
					.withAmount(amount < 1 ? 1 : amount > 64 ? 64 : amount)
					.withDurability((short) section.getInt("damage", (short) 0))
					.withDisplayNameColoured(section.getString("name"))
					.withLoreColoured(section.getStringList("lore"))
					.withUnbreakable(section.getBoolean("unbreakable"));
		}

		return builder().withMaterial(Material.matchMaterial(material))
				.withAmount(amount < 1 ? 1 : amount > 64 ? 64 : amount)
				.withDurability((short) section.getInt("damage", (short) 0))
				.withDisplayNameColoured(section.getString("name")).withLoreColoured(section.getStringList("lore"))
				.withUnbreakable(section.getBoolean("unbreakable"));
	}

	public static PersistentDataContainer toPDCItemStack(ItemStack stack) {
		return stack.getItemMeta().getPersistentDataContainer();
	}

	/**
	 * Unwraps a {@link ResourceSection} into a {@link ItemStack}.
	 *
	 * @param section resource section to unwrap.
	 * @return unwrapped item stack.
	 *
	 * @see ResourceSection
	 */
	public static ItemStack toItemStack(ConfigurationSection section) {
		return toItemStackBuilder(section).build();
	}

	public static boolean isBlank(ItemStack stack) {
		return stack == null || stack.getType() == Material.AIR;
	}

	/**
	 * @param stack the stack to test.
	 * @return {@code true} if the stack is a player head.
	 */
	public static boolean isPlayerHead(ItemStack stack) {
		return stack.getType() == Material.PLAYER_HEAD;
	}

	// Serialisation
	public static String serializeItems(ItemStack[] items) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeInt(items.length);

			for (ItemStack item : items) {
				dataOutput.writeObject(item);
			}

			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			// Ignored
		}

		return null;
	}

	public static String serializeItem(ItemStack item) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			dataOutput.writeObject(item);
			dataOutput.close();

			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			// Ignored
		}

		return null;
	}

	public static ItemStack[] deserializeItems(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] items = new ItemStack[dataInput.readInt()];

			// Read the serialized inventory
			for (int i = 0; i < items.length; i++) {
				items[i] = (ItemStack) dataInput.readObject();
			}

			dataInput.close();
			return items;
		} catch (Exception e) {
			// Ignored
		}

		return null;
	}

	public static ItemStack deserializeItem(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack item = (ItemStack) dataInput.readObject();

			dataInput.close();
			return item;
		} catch (Exception e) {
			// Ignored
		}

		return null;
	}
}
