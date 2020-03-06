package com.Furnesse.core.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.Furnesse.core.Core;

public class Settings {
	Core plugin = Core.instance;

	public Settings(Core plugin) {
		this.plugin = plugin;
	}

	// WHICH SYSTEM SHOULD THE SERVER BE USING
	public boolean usingMySQL = false;
	public boolean usingSb = false;
	public boolean usingDc = false;
	public boolean usingRanks = false;
	public boolean usingChat = true;
	public boolean usingCl = true;

	// MYSQL SETTINGS
	public String host, database, username, password;
	public String playerTable = "player_data", deathchestTable = "stored_deathchests";
	public int port;
	public Connection connection;
	// DEATHCHESTS SETTINGS
	public int dc_expireTime = 300;
	public List<String> dc_disabledworlds = new ArrayList<>();
	public boolean dc_spawnChestOnHighestBlock = true;
	public boolean dc_dropItemsAfterExpire = false;
	public boolean dc_voidSpawning = false;
	public boolean dc_autoEquipArmor = true;
	public boolean dc_lavaSpawning = true;
	public String dc_deathChestInvTitle = "&c%player%'s &8Deathchest";
	// SCOREBOARD SETTINGS
	public List<String> sb_lines = new ArrayList<>();
	public String sb_boardName = "&b&lFurnessE";
	// COMBATLOG SETTINGS
	public List<String> cl_disabledWorlds = new ArrayList<>();
	public int cl_tagDuration = 10;
	public boolean cl_actionBar = true;
	public List<String> cl_disabledOnTag = new ArrayList<>();
	public boolean cl_antiFallDamage = true;
	public final int cl_tempFallDamageDisabledTime = 10;
	public List<String> cl_blockedCommands = new ArrayList<>();
	public boolean cl_instantKill = false;
	public boolean cl_spawnNpc = true;
	public int cl_npcAvailableTime = 10;
	public List<String> cl_logoutCommands = new ArrayList<>();
	public boolean cl_removeTagInSafezone = true;
	public boolean cl_usingLandsHook = true;
	public boolean cl_usingWorldguardHook = true;

	public void enableSystems() {
		usingMySQL = plugin.getConfig().getBoolean("database.enabled");
		usingSb = plugin.getConfig().getBoolean("scoreboard.enabled");
		usingDc = plugin.getConfig().getBoolean("deathchests.enabled");
		usingRanks = plugin.getConfig().getBoolean("using_ranks");
		usingChat = plugin.getConfig().getBoolean("chat.enabled");
		usingCl = plugin.getConfig().getBoolean("combatlog.enabled");

		plugin.getLogger().info("MySQL enabled: " + usingMySQL);
		plugin.getLogger().info("Scoreboard enabled: " + usingSb);
		plugin.getLogger().info("Deathchests enabled: " + usingDc);
		plugin.getLogger().info("Ranks enabled: " + usingRanks);
		plugin.getLogger().info("Chat enabled: " + usingChat);
		plugin.getLogger().info("Combatlog enabled:" + usingCl);

		setupSettings();
	}

	public void setupSettings() {
		if (usingMySQL) {
			host = plugin.getConfig().getString("database.mysql.host");
			port = plugin.getConfig().getInt("database.mysql.port");
			database = plugin.getConfig().getString("database.mysql.database");
			username = plugin.getConfig().getString("database.mysql.username");
			password = plugin.getConfig().getString("database.mysql.password");

			try {
				synchronized (this) {
					if (getConnection() != null && !getConnection().isClosed()) {
						return;
					}
					Class.forName("com.mysql.jdbc.Driver");
					setConnection(DriverManager.getConnection(
							"jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, username, password));

					setupTables();
					plugin.getLogger().info("Succesfully loaded MySQL");
				}
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (usingDc) {
			dc_expireTime = plugin.getConfig().getInt("deathchests.expire_time");
			dc_disabledworlds = plugin.getConfig().getStringList("deathchests.disabled_worlds");
			dc_spawnChestOnHighestBlock = plugin.getConfig().getBoolean("deathchests.spawn_chest_on_highest_block");
			dc_dropItemsAfterExpire = plugin.getConfig().getBoolean("deathchests.drop_items_after_expire");
			dc_voidSpawning = plugin.getConfig().getBoolean("deathchests.void_spawning");
			dc_autoEquipArmor = plugin.getConfig().getBoolean("deathchests.auto_equip_armor");
			dc_lavaSpawning = plugin.getConfig().getBoolean("deathchests.spawn_in_lava");
			dc_deathChestInvTitle = plugin.getConfig().getString("deathchests.deathchest_inv_title");

			plugin.getLogger().info("loaded settings for deathchests");
		}

		if (usingSb) {
			sb_lines = plugin.getConfig().getStringList("scoreboard.lines");
			sb_boardName = plugin.getConfig().getString("scoreboard.title");

			plugin.getLogger().info("loaded settings for scoreboards");
		}

		if (usingCl) {
			for (String worldName : plugin.getConfig().getStringList("combatlog.disabled_worlds")) {
				cl_disabledWorlds.add(worldName);
			}

			cl_tagDuration = plugin.getConfig().getInt("combatlog.tag_duration");
			cl_actionBar = plugin.getConfig().getBoolean("combatlog.actionbar");

			for (String remove : plugin.getConfig().getStringList("combatlog.remove_on_tag")) {
				cl_disabledOnTag.add(remove);
			}

			cl_antiFallDamage = plugin.getConfig().getBoolean("combatlog.temp_anti_fall_damage");

			for (String blocked : plugin.getConfig().getStringList("combatlog.blocked_commands")) {
				cl_blockedCommands.add(blocked);
			}

			cl_instantKill = plugin.getConfig().getBoolean("combatlog.combat_logout.kill");
			cl_spawnNpc = plugin.getConfig().getBoolean("combatlog.combat_logout.spawn_npc");
			cl_npcAvailableTime = plugin.getConfig().getInt("combatlog.combat_logout.npc_available_time");

			for (String cmd : plugin.getConfig().getStringList("combatlog.combat_logout.commands")) {
				cl_logoutCommands.add(cmd);
			}

			cl_removeTagInSafezone = plugin.getConfig().getBoolean("combatlog.remove_tag_in_safezone");
			cl_usingLandsHook = plugin.getConfig().getBoolean("combatlog.hooks.lands");
			cl_usingWorldguardHook = plugin.getConfig().getBoolean("combatlog.hooks.worldguard");
		}
	}

	private void setupTables() {
		String table1 = "CREATE TABLE IF NOT EXISTS " + this.playerTable + "(uuid VARCHAR(200), username VARCHAR(16))";
		String table2 = "CREATE TABLE IF NOT EXISTS " + this.deathchestTable
				+ "(uuid VARCHAR(200), owner VARCHAR(16), location VARCHAR(64))";
		try {
			PreparedStatement playerStmt = connection.prepareStatement(table1);
			PreparedStatement deathchestsStmt = connection.prepareStatement(table2);

			playerStmt.executeUpdate();
			deathchestsStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
