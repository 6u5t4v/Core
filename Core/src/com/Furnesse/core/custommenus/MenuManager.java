package com.Furnesse.core.custommenus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

import com.Furnesse.core.Core;

public class MenuManager implements Listener{
	
	Core plugin;
	public MenuManager (Core plugin) {
		this.plugin = plugin;
	}
	
	public List<Menu> menus = new ArrayList<>();
	
	public void loadMenus() {
		ConfigurationSection section = plugin.getFileManager().getConfig("menus.yml").get().getConfigurationSection("");
		
		for(String menuName : section.getKeys(false)) {
			if(menuName != null) {
				section = section.getConfigurationSection(menuName);
				
				menus.add(new Menu(menuName, section));
			}
		}
	}
}
