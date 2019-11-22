package com.Furnesse.core.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

import me.clip.placeholderapi.PlaceholderAPI;

public class ChatFormats {

	Core plugin;

	public ChatFormats(Core plugin) {
		this.plugin = plugin;
	}

	private List<ChatFormat> chatFormats = new ArrayList<>();

	public Map<String, ChatFormat> pFormat = new HashMap<>();

	public void loadChatFormats() {
		ConfigurationSection availableChatFormats = plugin.getConfig().getConfigurationSection("chat.formats");
		chatFormats.clear();
		int amount = 0;
		if (availableChatFormats != null) {
			for (String format : availableChatFormats.getKeys(false)) {
				if (format != null) {
					try {
						String formatName = format.toString();
						String prefix = plugin.getConfig().getString("chat.formats." + format + ".prefix");
						String displayname = plugin.getConfig().getString("chat.formats." + format + ".displayname");
						String suffix = plugin.getConfig().getString("chat.formats." + format + ".suffix");
						String message = plugin.getConfig().getString("chat.formats." + format + ".message");
						String clickCmd = plugin.getConfig().getString("chat.formats." + format + ".click-command");

						ChatFormat chatformat = new ChatFormat(formatName, prefix, suffix, displayname, message,
								clickCmd);
						System.out.println("0: " + chatformat.getFormatName());
						chatFormats.add(chatformat);
						amount++;

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
			plugin.getLogger().info("Loaded a total of: " + amount + " chat formats");
		} else {
			createDefaultFormat();
			plugin.getLogger().info("No available chatformats, creating a new Default format...");
		}
	}

	public ChatFormat getPlayerFormat(Player player) {
		return pFormat.get(player.getName());
	}

	public ChatFormat getDefaultFormat() {
		for (ChatFormat format : chatFormats) {
			if (format.getFormatName().equals("default")) {
				return format;
			}
		}
		return null;
	}

	public void createDefaultFormat() {
		String prefix = "&7[&f%player_world%&7]";
		String displayname = "%core_rank_prefix% &7%player_name%";
		String suffix = " &8» ";
		String message = "&f";
		String clickCmd = "/msg %player%";
		plugin.getConfig().set("chat.formats.default.prefix", prefix);
		plugin.getConfig().set("chat.formats.default.displayname", displayname);
		plugin.getConfig().set("chat.formats.default.suffix", suffix);
		plugin.getConfig().set("chat.formats.default.message", message);
		plugin.getConfig().set("chat.formats.default.click-command", clickCmd);
		plugin.saveConfig();

		chatFormats.add(new ChatFormat("default", prefix, suffix, displayname, message, clickCmd));
	}

	public void setFormat(Player player, ChatFormat format) {
		String displayname = format.getDisplayname();
		String prefix = format.getPrefix();
		String suffix = format.getSuffix();

		displayname = PlaceholderAPI.setPlaceholders(player, displayname);
		prefix = PlaceholderAPI.setPlaceholders(player, prefix);
		suffix = PlaceholderAPI.setPlaceholders(player, suffix);

		plugin.getChat().setPlayerPrefix(player, prefix);
		plugin.getChat().setPlayerSuffix(player, suffix + format.getMessage());

		player.setDisplayName(Lang.chat(displayname));
		pFormat.put(player.getName(), format);

	}

	public void initFormat(Player player) {
		for (ChatFormat format : chatFormats) {
			if (format != null) {
				if (player.hasPermission("core.chat." + format.getFormatName())) {
					setFormat(player, format);
					return;
				}

			}
		}
		setFormat(player, getDefaultFormat());
		System.out.println("1: default");
	}
}
