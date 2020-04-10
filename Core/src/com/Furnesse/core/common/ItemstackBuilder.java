package com.Furnesse.core.common;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ItemstackBuilder {

	// Stack
	private Material material;
	private int amount = 1;
	private short durability = 0;

	// Meta
	private String displayName;
	private List<String> lore = Lists.newArrayList();
	private boolean unbreakable = false;
	private final Set<ItemFlag> itemFlags = Sets.newHashSet();

	private final Map<Enchantment, Integer> enchantments = Maps.newHashMap();

	// PERSISTENTDATA
	private final List<Applier> persistentAppliers = Lists.newArrayList();

	/**
	 * @param material item's material.
	 */
	public ItemstackBuilder(Material material) {
		withMaterial(material);
	}

	/**
	 * @param stack the stack to unpack.
	 */
	public ItemstackBuilder(ItemStack stack) {
		this(stack.getType());

		amount = stack.getAmount();
		durability = stack.getDurability();

		if (stack.hasItemMeta()) {
			ItemMeta meta = stack.getItemMeta();

			displayName = meta.getDisplayName();
			lore = meta.hasLore() ? meta.getLore() : Lists.newArrayList();
			unbreakable = meta.isUnbreakable();
			itemFlags.addAll(meta.getItemFlags());
		}

		enchantments.putAll(stack.getEnchantments());
	}

	protected ItemstackBuilder(ItemstackBuilder builder) {
		this(builder.getMaterial());

		amount = builder.amount;
		durability = builder.durability;

		displayName = builder.displayName;
		lore = Lists.newArrayList(builder.lore);
		unbreakable = builder.unbreakable;
		itemFlags.addAll(Lists.newArrayList(builder.itemFlags));
		enchantments.putAll(Maps.newHashMap(builder.enchantments));

//        nbtAppliers.addAll(Lists.newArrayList(builder.nbtAppliers));
	}

	/**
	 * @return builds the {@link ItemStackBuilder}'s data into a valid
	 *         {@link ItemStack}.
	 */
	public ItemStack build() {
		ItemStack stack = new ItemStack(material, amount, durability);
		ItemMeta meta = stack.getItemMeta();

		if (meta != null) {
			meta.setDisplayName(displayName);
			meta.setLore(lore);

			meta.setUnbreakable(unbreakable);

			meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
			stack.setItemMeta(meta);
		}

		stack.addUnsafeEnchantments(enchantments);

		// NBTs must be applied AFTER meta is applied.
		for (Applier applier : persistentAppliers) {
			stack = applier.apply(stack);
		}

		return stack;
	}

	/**
	 * @return a copy of this {@link ItemStackBuilder}.
	 */
	public ItemstackBuilder deepClone() {
		return new ItemstackBuilder(this);
	}

	/**
	 * @param material the stack type.
	 * @return this item stack builder.
	 *
	 * @see Material
	 */
	public ItemstackBuilder withMaterial(Material material) {
		this.material = material;
		return this;
	}

	/**
	 * @param amount the stack amount.
	 * @return this item stack builder.
	 */
	public ItemstackBuilder withAmount(int amount) {
		this.amount = amount;
		return this;
	}

	/**
	 * @param durability the stack data value.
	 * @return this item stack builder.
	 */
	public ItemstackBuilder withDurability(short durability) {
		this.durability = durability;
		return this;
	}

	/**
	 * @param displayName the stack display name.
	 * @return this item stack builder.
	 */
	public ItemstackBuilder withDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	/**
	 * Colours and sets the item stack display name.
	 *
	 * @param displayName the stack display name.
	 * @return this item stack builder.
	 *
	 * @see Lang#color(String)
	 */
	public ItemstackBuilder withDisplayNameColoured(String displayName) {
		this.displayName = Lang.color(displayName);
		return this;
	}

	/**
	 * @param lore the stack lore.
	 * @return this item stack builder.
	 */
	public ItemstackBuilder withLore(Iterable<? extends String> lore) {
		this.lore = Lists.newArrayList(lore);
		return this;
	}

	/**
	 * Colours and sets the item stack lore.
	 *
	 * @param lore the stack lore.
	 * @return this item stack builder.
	 *
	 * @see Lang#color(Iterable)
	 */
	public ItemstackBuilder withLoreColoured(Iterable<? extends String> lore) {
		this.lore = Lang.color(lore);
		return this;
	}

	/**
	 * @param unbreakable if the stack is unbreakable.
	 * @return this item stack builder.
	 */
	public ItemstackBuilder withUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	/**
	 * @param flag the stack item flags.
	 * @return this item stack builder.
	 *
	 * @see ItemFlag
	 */
	public ItemstackBuilder withItemFlag(ItemFlag... flag) {
		itemFlags.addAll(Arrays.asList(flag));
		return this;
	}

	/**
	 * Adds, and overrides existing enchantment type, an enchantment to the stack.
	 *
	 * @param enchantment the enchantment type.
	 * @param level       the enchantment level.
	 * @return this item stack builder.
	 *
	 * @see Enchantment
	 */
	public ItemstackBuilder withEnchantment(Enchantment enchantment, int level) {
		enchantments.put(enchantment, level);
		return this;
	}

	/**
	 * @param enchantments map of enchantments and their levels.
	 * @return this item stack.
	 *
	 * @see Enchantment
	 */
	public ItemstackBuilder withEnchantments(Map<Enchantment, Integer> enchantments) {
		this.enchantments.putAll(enchantments);
		return this;
	}

	/**
	 * @param key   the NBT key.
	 * @param value the NBT string value.
	 * @return this item stack builder.
	 *
	 * @see NBTItemStack
	 */
	public ItemstackBuilder withPDCString(NamespacedKey key, String value) {
		persistentAppliers.add(item ->
		{
			ItemMeta meta = item.getItemMeta();
			meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
			item.setItemMeta(meta);
//			Debug.log("key " + meta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
			return item;
		});

		return this;
	}

	/**
	 * @param key   the NBT key.
	 * @param value the NBT integer value.
	 * @return this item stack builder.
	 *
	 * @see NBTItemStack
	 */
	public ItemstackBuilder withPDCInteger(NamespacedKey key, int value) {
		persistentAppliers.add(item ->
		{
			ItemMeta meta = item.getItemMeta();

			meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
			item.setItemMeta(meta);
			return item;
		});

		return this;
	}

	/**
	 * @param key   the NBT key.
	 * @param value the NBT double value.
	 * @return this item stack builder.
	 *
	 * @see NBTItemStack
	 */
	public ItemstackBuilder withPDCDouble(NamespacedKey key, double value) {
		persistentAppliers.add(item ->
		{
			ItemMeta meta = item.getItemMeta();

			meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, value);
			item.setItemMeta(meta);
			return item;
		});

		return this;
	}

	/**
	 * @param key   the NBT key.
	 * @param value the NBT boolean value.
	 * @return this item stack builder.
	 *
	 * @see NBTItemStack
	 */
	public ItemstackBuilder withPDCBoolean(NamespacedKey key, boolean value) {
		persistentAppliers.add(item ->
		{
			ItemMeta meta = item.getItemMeta();

			meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, String.valueOf(value));
			item.setItemMeta(meta);
			return item;
		});

		return this;
	}

	/**
	 * @return stack type.
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * @return stack amount.
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @return stack data value.
	 */
	public short getDurability() {
		return durability;
	}

	/**
	 * @return stack display name.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return stack lore.
	 */
	public List<String> getLore() {
		return lore;
	}

	/**
	 * @return {@code true} if the stack is unbreakable.
	 */
	public boolean isUnbreakable() {
		return unbreakable;
	}

	/**
	 * @return set of item flags applied to the stack.
	 *
	 * @see ItemFlag
	 */
	public Set<ItemFlag> getItemFlags() {
		return itemFlags;
	}

	/**
	 * @return map of enchantments and their levels.
	 */
	public Map<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

	protected List<Applier> getPersistentAppliers() {
		return persistentAppliers;
	}

	interface Applier {

		/**
		 * @return the {@link ItemStack} with the applied contents.
		 */
		ItemStack apply(ItemStack item);
	}
}
