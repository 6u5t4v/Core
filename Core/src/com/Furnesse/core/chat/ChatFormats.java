package com.Furnesse.core.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
			for (String cFormat : availableChatFormats.getKeys(false)) {
				if (cFormat != null) {
					try {
						String name = cFormat.toString();
						String format = plugin.getConfig().getString("chat.formats." + cFormat + ".format");
						String clickCmd = plugin.getConfig().getString("chat.formats." + cFormat + ".click-command");
						List<String> tooltip = plugin.getConfig().getStringList("chat.formats." + cFormat + ".tooltip");

						ChatFormat chatformat = new ChatFormat(name, format, clickCmd, tooltip);
						chatFormats.add(chatformat);

//						System.out.println("0: " + chatformat.getName());
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
			if (format.getName().equals("default")) {
				return format;
			}
		}
		return null;
	}

	public void createDefaultFormat() {
		String format = "&8[&f%player_world%&8] %core_rank_prefix% &7%player_name% &8» &7%msg%";
		String clickCmd = "/msg %player%";
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("sdfgsdfg");
		tooltip.add("sdfgsdfg");

		plugin.getConfig().set("chat.formats.default.format", format);
		plugin.getConfig().set("chat.formats.default.click-command", clickCmd);
		plugin.getConfig().set("chat.formats.default.tooltip", tooltip);
		plugin.saveConfig();

		chatFormats.add(new ChatFormat("default", format, clickCmd, tooltip));
	}

	public TextComponent create(ChatFormat format) {

		TextComponent textComp = new TextComponent();
		textComp.setHoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to message player").create()));
		textComp.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, format.getClickCmd()));
		return null;

	}

	public void initFormat(Player player) {
		for (ChatFormat format : chatFormats) {
			if (format != null) {
				if (player.hasPermission("core.chat." + format.getName()) || format.getName().equals("default")) {
					System.out.println("permission");
					pFormat.put(player.getName(), format);
					return;
				}
			}
		}
		System.out.println("test");
		pFormat.put(player.getName(), getDefaultFormat());
		System.out.println("1: default");
	}
}
