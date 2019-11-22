package com.Furnesse.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
import com.Furnesse.core.commands.ItemsCMD;
import com.Furnesse.core.commands.RankCMD;
import com.Furnesse.core.customcommands.CustomCommand;
import com.Furnesse.core.customcommands.CustomCommands;
import com.Furnesse.core.customitems.CItemManager;
import com.Furnesse.core.database.MySQL;
import com.Furnesse.core.deathchest.DeathChests;
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
	private MySQL mysql = new MySQL(this);
	private DeathChests deathChests = new DeathChests(this);

	public CItemManager cItemMan = new CItemManager(this);
	public ChatFormats chatFormat = new ChatFormats(this);

	public Utils utils = new Utils(this);

	public List<String> enabledWorlds = new ArrayList<String>();
	public List<String> dcEnabledWorlds = new ArrayList<String>();

	public Map<Material, Integer> matTimeout = new HashMap<Material, Integer>();

	public void onEnable() {
		instance = this;
		configs.createCustomConfig();
		configs.saveConfigs();
		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		setupEconomy();
		serverOptions();
		registerListeners();
		registerPlaceholders();
		registerCommands();
		commands.loadCustomCommands();
		cItemMan.loadCustomItems();
		disableRecipes();
		this.getLogger().info("Has been enabled v" + this.getDescription().getVersion());
	}

	public boolean usingSb = true;
	public boolean usingDc;
	public boolean usingRanks;
	public boolean usingChat;
	public List<String> lines = new ArrayList<>();

	public int minItems;

	public String boardName;

	private void registerCommands() {
		if (usingRanks)
			getCommand("rank").setExecutor(new RankCMD());
		getCommand("core").setExecutor(new CoreCMD());
		getCommand("items").setExecutor(new ItemsCMD());

		registerCustomCommands();
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
//		pm.registerEvents(new ChatEvent(), this);
		pm.registerEvents(new DeathChests(this), this);
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

	public void serverOptions() {
		usingSb = getConfig().getBoolean("scoreboard.enabled");
		usingDc = getConfig().getBoolean("deathchests.enabled");
		usingRanks = getConfig().getBoolean("use-ranks");
		usingChat = getConfig().getBoolean("chat.enabled");

		lines = getConfig().getStringList("scoreboard.lines");
		minItems = getConfig().getInt("deathchests.deathchest-min-items");
		boardName = getConfig().getString("scoreboard.title");

		if (usingChat) {
			this.getLogger().info("Using CORE Chat");
			setupChat();
			chatFormat.loadChatFormats();
		}

		if (usingRanks) {
			this.getLogger().info("Using CORE Ranks");
			rankMan.loadRanks();
			setupPermissions();
		}

		if (usingSb) {
			this.getLogger().info("Using CORE Scoreboard");
		}

		if (usingDc) {
			this.getLogger().info("Using CORE DeathChests");
			for (String world : getConfig().getStringList("deathchests.enabled-worlds")) {
				if (world != null) {
					dcEnabledWorlds.add(world);
				}
			}
			deathChests.loadDeathChests();
		}
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
		utils.resetDcLocs();

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

	public DeathChests getDeathChest() {
		return deathChests;
	}

	public MySQL getMysql() {
		return mysql;
	}
}
