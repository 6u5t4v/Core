package com.Furnesse.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

import com.Furnesse.core.Core;

public class Utils {
	Core plugin;

	public Utils(Core plugin) {
		this.plugin = plugin;
	}

	public enum BroadcastType
	{
		WARN("§7(§4!§7)"), DEBUG("§7(§eDEBUG§7)"), INFO("§7(§aINFO§7)");

		private final String prefix;
		
		BroadcastType(String prefix) { this.prefix = prefix; }
		
		public String toString() { return this.prefix; }
	}
}
