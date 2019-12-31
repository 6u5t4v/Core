package com.Furnesse.core.misc;

import com.Furnesse.core.Core;

public class PlaceholderAPIHook {
	private Core plugin;

	public PlaceholderAPIHook(Core plugin) {
		this.plugin = plugin;
	}

	public boolean usePlaceholderAPI() {
		if (this.plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
			System.out.println("PlaceholderAPI not found, using independent config settings.");
			return false;
		}
		return true;
	}
}
