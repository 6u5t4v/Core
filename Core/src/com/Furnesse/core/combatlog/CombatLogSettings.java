package com.Furnesse.core.combatlog;

import java.util.ArrayList;
import java.util.List;

import com.Furnesse.core.Core;

public class CombatLogSettings {

	Core plugin = Core.instance;

	public static List<String> disabledWorlds = new ArrayList<>();
	public static int tagDuration = 10;
	public static boolean actionBar = true;
	public static List<String> disabledOnTag = new ArrayList<>();
	public static boolean antiFallDamage = true;
	public static final int tempFallDamageDisabledTime = 10;
	public static List<String> blockedCommands = new ArrayList<>();
	public static boolean instantKill = false;
	public static boolean spawnNpc = true;
	public static int npcAvailableTime = 10;
	public static List<String> logoutCommands = new ArrayList<>();

	public void loadCombatLogSettings() {
		for (String worldName : plugin.getConfig().getStringList("combatlog.disabled_worlds")) {
			disabledWorlds.add(worldName);
		}

		tagDuration = plugin.getConfig().getInt("combatlog.tag_duration");
		actionBar = plugin.getConfig().getBoolean("combatlog.actionbar");

		for (String remove : plugin.getConfig().getStringList("combatlog.remove_on_tag")) {
			disabledOnTag.add(remove);
		}

		antiFallDamage = plugin.getConfig().getBoolean("combatlog.temp_anti_fall_damage");

		for (String blocked : plugin.getConfig().getStringList("combatlog.blocked_commands")) {
			blockedCommands.add(blocked);
		}

		instantKill = plugin.getConfig().getBoolean("combatlog.combat_logout.kill");
		spawnNpc = plugin.getConfig().getBoolean("combatlog.combat_logout.spawn_npc");
		npcAvailableTime = plugin.getConfig().getInt("combatlog.combat_logout.npc_available_time");

		for (String cmd : plugin.getConfig().getStringList("combatlog.combat_logout.commands")) {
			logoutCommands.add(cmd);
		}
	}
}
