package com.Furnesse.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.Furnesse.core.api.CombatLogAPI;
import com.Furnesse.core.chat.ChatFormats;
import com.Furnesse.core.combatlog.CombatLog;
import com.Furnesse.core.combatlog.listeners.EntityDamageByEntityListener;
import com.Furnesse.core.combatlog.listeners.PlayerCommandPreProcessListener;
import com.Furnesse.core.combatlog.listeners.PlayerDeathListener;
import com.Furnesse.core.combatlog.listeners.PlayerJoinListener;
import com.Furnesse.core.combatlog.listeners.PlayerKickListener;
import com.Furnesse.core.combatlog.listeners.PlayerMoveListener;
import com.Furnesse.core.combatlog.listeners.PlayerQuitListener;
import com.Furnesse.core.combatlog.listeners.PlayerTagListener;
import com.Furnesse.core.combatlog.listeners.PlayerToggleFlightListener;
import com.Furnesse.core.combatlog.listeners.PlayerUntagListener;
import com.Furnesse.core.commands.CombatLogCMD;
import com.Furnesse.core.commands.CoreCMD;
import com.Furnesse.core.commands.CustomCMD;
import com.Furnesse.core.commands.DeathChestCMD;
import com.Furnesse.core.commands.ItemsCMD;
import com.Furnesse.core.commands.RankCMD;
import com.Furnesse.core.config.Message;
import com.Furnesse.core.config.Settings;
import com.Furnesse.core.customcommands.CustomCommand;
import com.Furnesse.core.customcommands.CustomCommands;
import com.Furnesse.core.customitems.CustomItems;
import com.Furnesse.core.deathchest.DeathChestListener;
import com.Furnesse.core.deathchest.DeathChestManager;
import com.Furnesse.core.deathchest.DeathChestsGUI;
import com.Furnesse.core.listeners.ChatEvent;
import com.Furnesse.core.listeners.CraftingRecipes;
import com.Furnesse.core.listeners.PlayerListeners;
import com.Furnesse.core.mechanics.CommandCD;
import com.Furnesse.core.mechanics.Cooldown;
import com.Furnesse.core.rank.RankManager;
import com.Furnesse.core.sidebar.Sidebar;
import com.Furnesse.core.utils.Debug;
import com.Furnesse.core.utils.FileManager;
import com.Furnesse.core.utils.Time;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.angeschossen.lands.api.integration.LandsIntegration;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import net.milkbowl.vault.economy.Economy;

public class Core extends JavaPlugin implements Listener {
	public static Core instance;

	public static String jarFile;

	private static final Logger log = Logger.getLogger("Minecraft");
//	private static Permission perms = null;
	private static Economy econ = null;
//	private static Chat chat = null;

	public final NamespacedKey customitemKey = new NamespacedKey(this, "CoreCustomItem");
	
	public WorldGuardPlugin wgAPI;
	public LandsIntegration landsAPI;
	public CombatLogAPI combatLogAPI;
	private HeadDatabaseAPI headAPI;

	private RankManager rankMan = new RankManager(this);
	private CustomCommands commands = new CustomCommands(this);
	private Sidebar sb;
	private CustomItems customItems = new CustomItems(this);
	private ChatFormats chatFormat = new ChatFormats(this);
	private Settings settings = new Settings(this);
	private CommandCD cmdCD = new CommandCD(this);
	private CombatLog combatLog = new CombatLog(this);

	private FileManager fileManager = new FileManager(this);

	public boolean landsHook = false;
	public boolean worldguardHook = false;

	public int combatlogs = 0;

	public void onEnable() {
		this.getLogger().info("<------<< Furnesse CORE " + this.getDescription().getVersion() + " >>------>");
		instance = this;

		registerConfigs();

		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		settings.enableSystems();

		checkHooks();
		setupConfigurations();


		if(settings.usingCl)
			combatLogAPI = new CombatLogAPI();
		
		registerListeners();
		registerPlaceholders();
		registerCommands();
		disableRecipes();

		this.getLogger().info("<------------------------------->");
	}

	private void checkHooks() {
		PluginManager pm = getServer().getPluginManager();
		if (pm.getPlugin("HeadDatabase") != null) {
			headAPI = new HeadDatabaseAPI();
			this.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) this);
		} else {
			headAPI = null;
		}

		if (settings.usingCl) {
			if (settings.cl_usingLandsHook) {
				if (pm.getPlugin("Lands") != null) {
					String version = pm.getPlugin("Lands").getDescription().getVersion();
					this.landsAPI = new LandsIntegration(this, true);
					this.landsHook = true;
					this.getLogger().info("Hooked into Lands " + version + " successfully");
				}
			}

			if (settings.cl_usingWorldguardHook) {
				if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
					this.worldguardHook = true;
					this.getLogger().info("Hooked into WorldGuard successfully");
				}
			}
		}
	}

	private void registerConfigs() {
		fileManager.getConfig("config.yml").copyDefaults(true).save();
		fileManager.getConfig("lang.yml").copyDefaults(true).save();
		fileManager.getConfig("customcommands.yml").copyDefaults(true).save();
		fileManager.getConfig("customitems.yml").copyDefaults(true).save();
		if (settings.usingDc)
			fileManager.getConfig("deathchests.yml").copyDefaults(true).save();
		if (settings.usingChat || settings.usingRanks || settings.usingSb)
			fileManager.getConfig("players.yml").copyDefaults(true).save();
		if (settings.usingRanks)
			fileManager.getConfig("ranks.yml").copyDefaults(true).save();
	}

	public void reloadPlugin() {
		for (FileManager.Config c : FileManager.configs.values()) {
			c.reload();
		}
		Message.reload();

		settings.enableSystems();
		setupConfigurations();
		
		registerCommands();
		registerListeners();

		disableRecipes();
	}

	@SuppressWarnings("unused")
	private void test() {
		String[] params = { "cooldown_shop_remaining", "cooldown_shop_disabledtime", "cooldown_shop_enabledtime",
				"cooldown_shop_isopen" };

		for (String param : params) {
			if (param.startsWith("cooldown_") && param.endsWith("_remaining")) {
				String taskName = param.substring(9, param.length() - 10);
				Cooldown cd = cmdCD.cmdCooldown.get(taskName);
				Debug.Log("taskname: " + taskName + " | test: " + cd.getTaskname());
			}
			if (param.startsWith("cooldown_") && param.endsWith("_disabledtime")) {
				String taskName = param.substring(9, param.length() - 13);
				Cooldown cd = cmdCD.cmdCooldown.get(taskName);
				Debug.Log("taskname: " + taskName + " | test: " + cd.getTaskname());
			}
			if (param.startsWith("cooldown_") && param.endsWith("_enabledtime")) {
				String taskName = param.substring(9, param.length() - 12);
				Cooldown cd = cmdCD.cmdCooldown.get(taskName);
				Debug.Log("taskname: " + taskName + " | test: " + cd.getTaskname());
			}
			if (param.startsWith("cooldown_") && param.endsWith("_isopen")) {
				String taskName = param.substring(9, param.length() - 7);
				Cooldown cd = cmdCD.cmdCooldown.get(taskName);
				Debug.Log("taskname: " + taskName + " | test: " + cd.getTaskname());
			}

		}
	}

	@EventHandler
	public void onDatabaseLoad(DatabaseLoadEvent event) {
		try {
			ItemStack item = headAPI.getItemHead("23282");
			getLogger().log(Level.INFO, headAPI.getItemID(item));
		} catch (NullPointerException e) {
			getLogger().log(Level.SEVERE, "Could not find the head you were looking for.");
		}
	}

	private void registerCommands() {
		registerCustomCommands();

		if (settings.usingDc) {
			getCommand("cdeathchest").setExecutor(new DeathChestCMD());
		}

		if (settings.usingRanks)
			getCommand("crank").setExecutor(new RankCMD());
		getCommand("core").setExecutor(new CoreCMD());
		getCommand("citems").setExecutor(new ItemsCMD());
		if (settings.usingCl)
			getCommand("tag").setExecutor(new CombatLogCMD());
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
				pluginCommand.setPermissionMessage(Message.NO_PERMISSION.getChatMessage());
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
		pm.registerEvents(new PlayerListeners(), this);
		if (settings.usingChat)
			pm.registerEvents(new ChatEvent(), this);
		if (settings.usingDc) {
			pm.registerEvents(new DeathChestListener(), this);
			pm.registerEvents(new DeathChestsGUI(), this);
		}
		pm.registerEvents(cmdCD, this);
		if (settings.usingCl) {
			pm.registerEvents(new EntityDamageByEntityListener(), this);
			pm.registerEvents(new PlayerCommandPreProcessListener(), this);
			pm.registerEvents(new PlayerDeathListener(), this);
			pm.registerEvents(new PlayerJoinListener(), this);
			pm.registerEvents(new PlayerKickListener(), this);
			pm.registerEvents(new PlayerMoveListener(), this);
			pm.registerEvents(new PlayerQuitListener(), this);
			pm.registerEvents(new PlayerTagListener(), this);
			pm.registerEvents(new PlayerToggleFlightListener(), this);
			pm.registerEvents(new PlayerUntagListener(), this);
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

				if (params.startsWith("cooldown_") && params.endsWith("_remaining")) {
					String taskName = params.substring(9, params.length() - 10);
					if (cmdCD.cmdCooldown.get(taskName) == null) {
						getLogger().severe("No valid task with the name " + taskName + ". Check you config.yml");
						return null;
					}
					Cooldown cd = cmdCD.cmdCooldown.get(taskName);
					return Time.convertSecondsToCooldown((int) cd.getTimeRemaning());
				}

				if (params.startsWith("cooldown_") && params.endsWith("_disabledtime")) {
					String taskName = params.substring(9, params.length() - 13);
					if (cmdCD.cmdCooldown.get(taskName) == null) {
						getLogger().severe("No valid task with the name " + taskName + ". Check you config.yml");
						return null;
					}
					Cooldown cd = cmdCD.cmdCooldown.get(taskName);
					return cd.getDisabledTime();
				}

				if (params.startsWith("cooldown_") && params.endsWith("_enabledtime")) {
					String taskName = params.substring(9, params.length() - 12);
					if (cmdCD.cmdCooldown.get(taskName) == null) {
						getLogger().severe("No valid task with the name " + taskName + ". Check you config.yml");
						return null;
					}
					Cooldown cd = cmdCD.cmdCooldown.get(taskName);
					return cd.getDisabledTime();
				}

				if (params.startsWith("cooldown_") && params.endsWith("_oncooldown")) {
					String taskName = params.substring(9, params.length() - 11);
					if (cmdCD.cmdCooldown.get(taskName) == null) {
						getLogger().severe("No valid task with the name " + taskName + ". Check you config.yml");
						return null;
					}
					Cooldown cd = cmdCD.cmdCooldown.get(taskName);
					String str;
					if (cd.isCmdOnCooldown())
						str = "&c&lUNAVAILABLE";
					else
						str = "&a&lOPEN";
					return str;
				}

//				if (usingRanks) {
//					if (params.equalsIgnoreCase("rank_prefix")) {
//						return getRanks().getPRank(player).getPrefix();
//					}
//
//					if (params.equalsIgnoreCase("rank_suffix")) {
//						return getRanks().getPRank(player).getSuffix();
//					}
//
//					if (params.equalsIgnoreCase("rank")) {
//						return getRanks().getPRank(player).getName();
//					}
//				}
				return null;
			}

		});
	}

	private void setupConfigurations() {
		commands.loadCustomCommands();
		customItems.loadItems();
		cmdCD.loadCooldownTasks();
		if (settings.usingCl)
			combatLog.enableTimer();
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
		if (settings.usingDc && DeathChestManager.getInstance().getDeathChestsByUUID().values() != null)
			DeathChestManager.getInstance().clearDeathChests();

		if (fileManager.getConfig("player.yml") != null)
			this.fileManager.saveConfig("players.yml");

		combatLog.taggedPlayers.clear();

		this.getLogger().info("Has been disabled v" + this.getDescription().getVersion());
	}

	public Economy getEconomy() {
		return econ;
	}

	public RankManager getRanks() {
		return rankMan;
	}

	public CustomCommands getCommands() {
		return commands;
	}

	public Sidebar getSidebar() {
		return sb;
	}

	public HeadDatabaseAPI getHeadAPI() {
		return headAPI;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public CustomItems getCustomItems() {
		return customItems;
	}

	public ChatFormats getChatFormat() {
		return chatFormat;
	}

	public Settings getSettings() {
		return settings;
	}

	public CommandCD getCommandCD() {
		return cmdCD;
	}

	public CombatLog getCombatLog() {
		return combatLog;
	}
}
