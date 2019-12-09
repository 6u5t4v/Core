package com.Furnesse.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.Furnesse.core.Events.PlayerEvents;
import com.Furnesse.core.Events.Scoreboard;
import com.Furnesse.core.chat.ChatFormats;
import com.Furnesse.core.commands.CoreCMD;
import com.Furnesse.core.commands.CustomCMD;
import com.Furnesse.core.commands.DeathChestCMD;
import com.Furnesse.core.commands.ItemsCMD;
import com.Furnesse.core.commands.RankCMD;
import com.Furnesse.core.customcommands.CustomCommand;
import com.Furnesse.core.customcommands.CustomCommands;
import com.Furnesse.core.customitems.CItemManager;
import com.Furnesse.core.deathchest.DeathChestListener;
import com.Furnesse.core.deathchest.DeathChestManager;
import com.Furnesse.core.deathchest.DeathChests;
import com.Furnesse.core.deathchest.DeathChestsGUI;
import com.Furnesse.core.listeners.ChatEvent;
import com.Furnesse.core.listeners.CraftingRecipes;
import com.Furnesse.core.rank.RankManager;
import com.Furnesse.core.utils.Configs;
import com.Furnesse.core.utils.Lang;
import com.Furnesse.core.utils.Utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Core extends JavaPlugin {
	public static Core instance;

	private static final Logger log = Logger.getLogger("Minecraft");
	private static Permission perms = null;
	private static Economy econ = null;
	private static Chat chat = null;

	private Configs configs = new Configs(this);
	private RankManager rankMan = new RankManager(this);
	private CustomCommands commands = new CustomCommands(this);
	private Scoreboard sb = new Scoreboard(this);
//	private DeathChests deathChests = new DeathChests(this);

	public CItemManager cItemMan = new CItemManager(this);
	public ChatFormats chatFormat = new ChatFormats(this);

	public Utils utils = new Utils(this);

	public String host, database, username, password;
	public String playerTable = "player_data", deathchestTable = "stored_deathchests";
	public int port;
	private Connection connection;
	
	public boolean usingMySQL;
	
	public List<String> enabledWorlds = new ArrayList<String>();
	public List<String> dcEnabledWorlds = new ArrayList<String>();
	public Map<Material, Integer> matTimeout = new HashMap<Material, Integer>();
	public boolean usingSb;
	public boolean usingDc;
//	public int minItems;
	public boolean usingRanks;
	public boolean usingChat;
	public List<String> lines = new ArrayList<>();
	public String boardName;
	
	public void onEnable() {
		this.getLogger().info("<------<< Furnesse CORE >>------>");
		instance = this;
		
		configs.createCustomConfig();
		configs.saveConfigs();

		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		setupEconomy();
		setupConfigurations();
		
		registerListeners();
		registerPlaceholders();
		registerCommands();
		disableRecipes();
		
		DeathChestManager.getInstance().loadDeathChests();
		
		this.getLogger().info("Has been enabled v" + this.getDescription().getVersion());
		this.getLogger().info("<------------------------------->");
	}
	
	private void setupMySQL() {
		host = getConfig().getString("database.mysql.host");
		port = getConfig().getInt("database.mysql.port");
		database = getConfig().getString("database.mysql.database");
		username = getConfig().getString("database.mysql.username");
		password = getConfig().getString("database.mysql.password");
		
		try {
			synchronized(this) {
				if(getConnection() != null && !getConnection().isClosed()) {
					return;
				}
				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, username, password));
				
				setupTables();
				this.getLogger().info("§aSuccesfully loaded MySQL");
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
		String table2 = "CREATE TABLE IF NOT EXISTS  " + this.deathchestTable + "(uuid VARCHAR(200), owner VARCHAR(16), location VARCHAR(64))";
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

	private void registerCommands() {		
		registerCustomCommands();
		
		if(usingDc) {
			getCommand("deathchests").setExecutor(new DeathChestCMD());
		}
		
		if (usingRanks)
			getCommand("rank").setExecutor(new RankCMD());
		getCommand("core").setExecutor(new CoreCMD());
		getCommand("items").setExecutor(new ItemsCMD());
	}

	private void registerCustomCommands() {
		try {
			Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class,
					Plugin.class);
			constructor.setAccessible(true);
			Field field = SimplePluginManager.class.getDeclaredField("commandMap");
			field.setAccessible(true);
			CommandMap commandMap = (CommandMap) field.get((SimplePluginManager) getServer().getPluginManager());
			for (CustomCommand ccmd : commands.getCommands()) {
				PluginCommand pluginCommand = constructor.newInstance(ccmd.getCmd(), this);
				pluginCommand.setAliases(ccmd.getAliases());
				pluginCommand.setPermission(ccmd.getPerm());
				pluginCommand.setPermissionMessage(Lang.NO_PERMISSION);
				commandMap.register(getDescription().getName(), pluginCommand);
				getCommand(ccmd.getCmd()).setExecutor(new CustomCMD());
//				System.out.println("loaded custom command: " + ccmd.getCmd());
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| NoSuchMethodException | InstantiationException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new CraftingRecipes(), this);
		pm.registerEvents(new PlayerEvents(), this);
		if(usingChat)
			pm.registerEvents(new ChatEvent(), this);
		if(usingDc) {
			pm.registerEvents(new DeathChestListener(), this);
			pm.registerEvents(new DeathChestsGUI(), this);
		}
	}

	private boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	@SuppressWarnings("unused")
	private boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	private void registerPlaceholders() {
		PlaceholderAPI.registerPlaceholderHook("core", new PlaceholderHook() {
			@Override
			public String onRequest(OfflinePlayer player, String params) {
				if (player != null && player.isOnline()) {
					return onPlaceholderRequest(player.getPlayer(), params);
				}
				return null;
			}

			@Override
			public String onPlaceholderRequest(Player player, String params) {
				if (player == null) {
					return null;
				}
				if (usingRanks) {
					if (params.equalsIgnoreCase("rank_prefix")) {
						return getRanks().getPRank(player).getPrefix();
					}

					if (params.equalsIgnoreCase("rank_suffix")) {
						return getRanks().getPRank(player).getSuffix();
					}

					if (params.equalsIgnoreCase("rank")) {
						return getRanks().getPRank(player).getName();
					}
				}
				return null;
			}

		});
	}

	public void setupConfigurations() {
		usingMySQL = getConfig().getBoolean("database.enabled");
		usingSb = getConfig().getBoolean("scoreboard.enabled");
		usingDc = getConfig().getBoolean("deathchests.enabled");
		usingRanks = getConfig().getBoolean("use-ranks");
		usingChat = getConfig().getBoolean("chat.enabled");		
		lines = getConfig().getStringList("scoreboard.lines");
//		minItems = getConfig().getInt("deathchests.deathchest-min-items");
		boardName = getConfig().getString("scoreboard.title");

		if(usingMySQL) {
			this.getLogger().info("Enabling MySQL");
			setupMySQL();
		}
		
		if (usingChat) {
			this.getLogger().info("Enabling Chat");
//			setupChat();
			chatFormat.loadChatFormats();
		}

		if (usingRanks) {
			this.getLogger().info("Enabling Ranks");
			rankMan.loadRanks();
			setupPermissions();
		}

		if (usingSb) {
			this.getLogger().info("Enabling Scoreboard");
		}

		if (usingDc) {
			this.getLogger().info("Enabling DeathChests");
			DeathChests.setupDcVariables();
		}
		
		commands.loadCustomCommands();
		cItemMan.loadCustomItems();
	}

	public static List<Material> disabledRecipes = new ArrayList<>();

	private void disableRecipes() {
		int amount = 0;
		for (String val : getConfig().getStringList("disable-recipes")) {
			try {
				Material mat = Material.getMaterial(val);
				disabledRecipes.add(mat);
				amount++;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		this.getLogger().info("Disabled " + amount + " recipe(s)");
		
	}

	public void onDisable() {
		
		this.getLogger().info("Has been disabled v" + this.getDescription().getVersion());
	}

	public Economy getEconomy() {
		return econ;
	}

	public Permission getPermissions() {
		return perms;
	}

	public Chat getChat() {
		return chat;
	}

	public Configs getConfigs() {
		return configs;
	}

	public RankManager getRanks() {
		return rankMan;
	}

	public CustomCommands getCommands() {
		return commands;
	}

	public Scoreboard getScoreboard() {
		return sb;
	}
}
