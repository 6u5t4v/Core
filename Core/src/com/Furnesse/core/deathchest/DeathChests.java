package com.Furnesse.core.deathchest;

import java.util.ArrayList;
import java.util.List;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

public class DeathChests {
	static Core plugin = Core.instance;

	private static int expireTime = 300;
	private static List<String> disabledworlds = new ArrayList<>();

	private static boolean spawnChestOnHighestBlock = true;

	private static boolean dropItemsAfterExpire = false;

	private static boolean voidSpawning = false;
	private static boolean autoEquipArmor = true;
	private static boolean lavaSpawning = true;
//	private static boolean debugMode = true;
	private static String deathChestInvTitle = "&7%player%'s DeathChest";

	public static void setupDcVariables() {
		expireTime = plugin.getConfig().getInt("deathchests.expire_time");
		disabledworlds = plugin.getConfig().getStringList("deathchests.disabled_worlds");
		spawnChestOnHighestBlock = plugin.getConfig().getBoolean("deathchests.spawn_chest_on_highest_block");
		dropItemsAfterExpire = plugin.getConfig().getBoolean("deathchests.drop_items_after_expire");
		voidSpawning = plugin.getConfig().getBoolean("deathchests.void_spawning");
		autoEquipArmor = plugin.getConfig().getBoolean("deathchests.auto_equip_armor");
		lavaSpawning = plugin.getConfig().getBoolean("deathchests.spawn_in_lava");
		deathChestInvTitle = Lang.chat(plugin.getConfig().getString("deathchests.deathchest_inv_title"));

		plugin.getLogger().info("loaded settings for deathchests");
	}

	public static List<String> getDisabledworlds() {
		return disabledworlds;
	}

	public static boolean isSpawnChestOnHighestBlock() {
		return spawnChestOnHighestBlock;
	}

	public static boolean isDropItemsAfterExpire() {
		return dropItemsAfterExpire;
	}

	public static String getDeathChestInvTitle() {
		return deathChestInvTitle;
	}

	public static boolean isVoidSpawning() {
		return voidSpawning;
	}

	public static boolean isAutoEquipArmor() {
		return autoEquipArmor;
	}

	public static boolean isLavaSpawning() {
		return lavaSpawning;
	}
	
	public static int getExpireTime() {
		return expireTime;
	}
}
