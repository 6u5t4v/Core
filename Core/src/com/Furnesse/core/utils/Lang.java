package com.Furnesse.core.utils;

import org.bukkit.ChatColor;

import com.Furnesse.core.Core;

public class Lang {
	
	static Core plugin = Core.instance;
	
	public static String chat(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public static String PREFIX = chat(plugin.getConfigs().getLangConfig().getString("prefix"));
	public static String NO_PERMISSION = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("no-permission"));
	public static String RELOADED = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("reloaded"));
	public static String CREATED_RANK = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("rank.created-rank"));
	public static String DELETED_RANK = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("rank.deleted-rank"));
	public static String SETPREFIX = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("rank.setprefix"));
	public static String SETSUFFIX = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("rank.setsuffix"));
	public static String ADDED_PERM = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("rank.added-perm"));
	public static String REMOVED_PERM = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("rank.removed-perm"));
	public static String SET_USER_RANK = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("rank.set-user-rank"));
	public static String ADD_USER_PERM = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("rank.add-user-perm"));
	public static String REMOVE_USER_PERM = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("removed-user-perm"));
	public static String RECEIVED_RANK = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("rank.received-rank"));
	public static String RECEIVED_PERM = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("rank.received-perm"));
	public static String LOSTED_PERM = PREFIX + chat(" " + plugin.getConfigs().getLangConfig().getString("rank.losted-perm"));
}
