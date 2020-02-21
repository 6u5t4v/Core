package com.Furnesse.core.mechanics;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;
import com.Furnesse.core.utils.Time;

public class CommandCD implements Listener {

	private Core plugin;

	public CommandCD(Core plugin) {
		this.plugin = plugin;
	}

	public Map<String, Cooldown> cmdCooldown = new HashMap<String, Cooldown>();

	public void loadCooldownTasks() {
		ConfigurationSection cdTasks = plugin.getConfig().getConfigurationSection("cooldownTasks");
		cmdCooldown.clear();
		if (cdTasks != null) {
			for (String taskName : cdTasks.getKeys(false)) {
				if (taskName != null) {
					String command = plugin.getConfig().getString("cooldownTasks." + taskName + ".command");
					String disabledFor = plugin.getConfig().getString("cooldownTasks." + taskName + ".disabledFor");
					String enabledFor = plugin.getConfig().getString("cooldownTasks." + taskName + ".enabledFor");
					
					cmdCooldown.put(command, new Cooldown(taskName, command, disabledFor, enabledFor));
					cmdCooldown.get(command).startTimer(true);
				}
			}
		}
	}

	public Map<String, Cooldown> getCooldownTasks() {
		return cmdCooldown;
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
		if (cmdCooldown.containsKey(e.getMessage().substring(1))) {
			Player p = e.getPlayer();
			String cmd = e.getMessage().substring(1);

			Cooldown cd = getCooldown(cmd);
			if (cd.isCmdOnCooldown()) {
				e.setCancelled(true);
				p.sendMessage(Lang.chat(cd.getDisabledMsg().replace("%enabledfor%", cd.getEnabledTime())
						.replace("%disabledfor%", cd.getDisabledTime())
						.replace("%timeRemaining%", Time.convertSecondsToCooldown((int) cd.getTimeRemaning()))));
			}
		}
	}
}
