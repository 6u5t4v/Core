package com.Furnesse.core.utils;

import org.bukkit.ChatColor;

import com.Furnesse.core.Core;

public class Lang {
	
	static Core plugin = Core.instance;
	
	public static String chat(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
