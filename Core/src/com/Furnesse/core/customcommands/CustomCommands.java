package com.Furnesse.core.customcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.Furnesse.core.Core;

public class CustomCommands {

	private Core plugin;

	public CustomCommands(Core plugin) {
		this.plugin = plugin;
	}

	private List<CustomCommand> customCommands = new ArrayList<CustomCommand>();
	
	public void loadCustomCommands() {
		ConfigurationSection availableCmds = plugin.fileManager.getConfig("customcommands.yml").get().getConfigurationSection("Commands");
		customCommands.clear();
		int amount = 0;
		if(availableCmds != null) {
			for(String cmd : availableCmds.getKeys(false)) {
				if(cmd != null) {
					try {
						String name = cmd.toString();
						String perm = plugin.fileManager.getConfig("customcommands.yml").get().getString("Commands." + cmd + ".permission");
						List<String> msg = plugin.fileManager.getConfig("customcommands.yml").get().getStringList("Commands." + cmd + ".message");
						List<String> aliases = plugin.fileManager.getConfig("customcommands.yml").get().getStringList("Commands." + cmd + ".aliases");
						
						CustomCommand customCommand = new CustomCommand(name, perm, msg, aliases);
						customCommands.add(customCommand);
						amount++;
						
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
			plugin.getLogger().info("Loaded a total of: " + amount + " custom commands");
		}
	}
	
	public List<CustomCommand> getCommands() {
		return customCommands;
	}

	public CustomCommand getCommand(String name) {
		for (CustomCommand cmd : customCommands) {
			if (cmd.getCmd().equals(name)) {
				return cmd;
			}
		}
		return null;
	}
}
