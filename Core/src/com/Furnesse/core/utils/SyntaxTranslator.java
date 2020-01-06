package com.Furnesse.core.utils;

import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.misc.PlaceholderAPIHook;

import me.clip.placeholderapi.PlaceholderAPI;

public class SyntaxTranslator {
	private Core plugin;

	private PlaceholderAPIHook placeholderAPIHook;

	private boolean usePlaceholderAPI;

	public SyntaxTranslator(Core plugin) {
		this.plugin = plugin;
		this.placeholderAPIHook = new PlaceholderAPIHook(plugin);
		this.usePlaceholderAPI = this.placeholderAPIHook.usePlaceholderAPI();
	}

	public String Translate(String string, Player player) {
		String returnString = "";
		if (this.usePlaceholderAPI)
			string = PlaceholderAPI.setPlaceholders(player, string);
		string = returnString;
		return returnString;
	}
}
