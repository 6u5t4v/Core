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

	public int expireTime = 300;
	public List<String> disabledworlds = new ArrayList<>();
	public boolean spawnChestOnHighestBlock = true;
	public boolean dropItemsAfterExpire = false;
	public boolean voidSpawning = false;
	public boolean autoEquipArmor = true;
	public boolean lavaSpawning = true;
	public String deathChestInvTitle = "&c%player%'s &8Deathchest";

	public String host, database, username, password;
	public String playerTable = "player_data", deathchestTable = "stored_deathchests";
	public int port;
	public Connection connection;

	public boolean usingMySQL = false;
	public boolean usingSb = false;
	public boolean usingDc = false;
	public boolean usingRanks = false;
	public boolean usingChat = true;

	public void enableSystems() {
		usingMySQL = plugin.getConfig().getBoolean("database.enabled");
		usingSb = plugin.getConfig().getBoolean("scoreboard.enabled");
		usingDc = plugin.getConfig().getBoolean("deathchests.enabled");
		usingRanks = plugin.getConfig().getBoolean("using_ranks");
		usingChat = plugin.getConfig().getBoolean("chat.enabled");
		
		plugin.getLogger().info("MySQL: " + usingMySQL);
		plugin.getLogger().info("Scoreboard: " + usingSb);
		plugin.getLogger().info("Deathchests: " + usingDc);
		plugin.getLogger().info("Ranks: " + usingRanks);
		plugin.getLogger().info("Chat: " + usingChat);
	}

	public void setupDcVariables() {
		expireTime = plugin.getConfig().getInt("deathchests.expire_time");
		disabledworlds = plugin.getConfig().getStringList("deathchests.disabled_worlds");
		spawnChestOnHighestBlock = plugin.getConfig().getBoolean("deathchests.spawn_chest_on_highest_block");
		dropItemsAfterExpire = plugin.getConfig().getBoolean("deathchests.drop_items_after_expire");
		voidSpawning = plugin.getConfig().getBoolean("deathchests.void_spawning");
		autoEquipArmor = plugin.getConfig().getBoolean("deathchests.auto_equip_armor");
		lavaSpawning = plugin.getConfig().getBoolean("deathchests.spawn_in_lava");
		deathChestInvTitle = plugin.getConfig().getString("deathchests.deathchest_inv_title");

		plugin.getLogger().info("loaded settings for deathchests");
	}

	public List<String> lines = new ArrayList<>();
	public String boardName = "&b&lFurnessE";

	public void setupSbVarialbes() {
		lines = plugin.getConfig().getStringList("scoreboard.lines");
		boardName = plugin.getConfig().getString("scoreboard.title");

		plugin.getLogger().info("loaded settings for scoreboards");
	}
	
	public void setupMySQL() {
		host = plugin.getConfig().getString("database.mysql.host");
		port = plugin.getConfig().getInt("database.mysql.port");
		database = plugin.getConfig().getString("database.mysql.database");
		username = plugin.getConfig().getString("database.mysql.username");
		password = plugin.getConfig().getString("database.mysql.password");
		
		try {
			synchronized(this) {
				if(getConnection() != null && !getConnection().isClosed()) {
					return;
				}
				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, username, password));
				
				setupTables();
				plugin.getLogger().info("Succesfully loaded MySQL");
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void setupTables() {
		String table1 = "CREATE TABLE IF NOT EXISTS " + this.playerTable + "(uuid VARCHAR(200), username VARCHAR(16))";
		String table2 = "CREATE TABLE IF NOT EXISTS " + this.deathchestTable + "(uuid VARCHAR(200), owner VARCHAR(16), location VARCHAR(64))";
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
