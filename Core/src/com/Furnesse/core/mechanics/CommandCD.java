package com.Furnesse.core.mechanics;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Debug;
import com.Furnesse.core.utils.Lang;

public class CommandCD implements Listener {

	Core plugin;

	public CommandCD(Core plugin) {
		this.plugin = plugin;
	}

	public Map<String, Cooldown> cmdCooldown = new HashMap<>();

	public void loadCooldownTasks() {
		ConfigurationSection cdTasks = plugin.getConfig().getConfigurationSection("cooldownTasks");
		cmdCooldown.clear();
		if (cdTasks != null) {
			for (String taskName : cdTasks.getKeys(false)) {
				String command = plugin.getConfig().getString("cooldownTasks." + taskName + ".command");
				int disabledFor = plugin.getConfig().getInt("cooldownTasks." + taskName + ".disabledFor");
				int enabledFor = plugin.getConfig().getInt("cooldownTasks." + taskName + ".enabledFor");

				Cooldown cooldown = new Cooldown(taskName, command, disabledFor, enabledFor);
				cmdCooldown.put(command, cooldown);
				cooldown.startDisableTimer();
				Debug.Log("test: " + taskName + " command: " + command);
			}
		}
	}

	public Cooldown getCooldown(String command) {
		for (Cooldown cd : cmdCooldown.values()) {
			if (cd.getCommand().equalsIgnoreCase(command)) {
				return cd;
			}
		}
		return null;
	}

	@EventHandler
	public void onPlayerCallCooldownCommand(PlayerCommandPreprocessEvent e) {
		if (cmdCooldown.containsKey(e.getMessage())) {
			Cooldown cd = getCooldown(e.getMessage());
			if (cd.getDisabledTime() >= System.currentTimeMillis()) {
				e.getPlayer().sendMessage(Lang.chat(cd.getDisabledMsg()));
				e.setCancelled(true);
			}
		}
	}
}
