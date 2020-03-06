package com.Furnesse.core.combatlog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.Furnesse.core.Core;
import com.Furnesse.core.Events.PlayerTagEvent;
import com.Furnesse.core.config.Message;
import com.Furnesse.core.utils.ActionBar;
import com.Furnesse.core.utils.Debug;

public class PlayerTagListener implements Listener {

	Core plugin = Core.instance;

	@EventHandler
	public void onTagPlayer(PlayerTagEvent e) {
		Player victim = e.getVictim();
		Player damager = e.getDamager();

		tagVictim(victim, damager);
		tagDamager(damager, victim);

		Debug.Log(damager.getName() + " has tagged " + victim.getName());
	}

	public void tagDamager(Player damager, Player victim) {
		if (plugin.getSettings().cl_disabledWorlds.contains(damager.getWorld().getName())) {
			return;
		}

		if (!plugin.getCombatLog().taggedPlayers.containsKey(damager.getUniqueId())) {
			plugin.getCombatLog().taggedPlayers.put(damager.getUniqueId(),
					Long.valueOf(plugin.getCombatLog().getCurrentTime()));
			damager.sendMessage(Message.TAGGER.getMessage().replace("%player%", victim.getName()));

			if (plugin.getSettings().cl_actionBar) {
				ActionBar.sendActionBar(damager, Message.ACTIONBAR_IN_COMBAT.getMessage().replace("%time%",
						String.valueOf(plugin.getCombatLog().tagTimeRemaining(damager.getUniqueId()))));
//				damager.spigot().sendMessage(ChatMessageType.ACTION_BAR,
//						TextComponent.fromLegacyText(Message.ACTIONBAR_IN_COMBAT.getMessage().replace("%time%",
//								String.valueOf(plugin.getCombatLog().tagTimeRemaining(damager.getUniqueId())))));
			}

			if (plugin.getSettings().cl_disabledOnTag.contains("fly")) {
				plugin.getCombatLog().removeFly(damager);
			}
		} else {
			plugin.getCombatLog().taggedPlayers.replace(damager.getUniqueId(),
					Long.valueOf(plugin.getCombatLog().getCurrentTime()));

			plugin.getCombatLog().removeFly(damager);
		}
	}

	public void tagVictim(Player victim, Player damager) {
		if (plugin.getSettings().cl_disabledWorlds.contains(victim.getWorld().getName())) {
			return;
		}

		if (!plugin.getCombatLog().taggedPlayers.containsKey(victim.getUniqueId())) {
			plugin.getCombatLog().taggedPlayers.put(victim.getUniqueId(),
					Long.valueOf(plugin.getCombatLog().getCurrentTime()));
			victim.sendMessage(Message.TAGGED.getMessage().replace("%player%", damager.getName()));

			if (plugin.getSettings().cl_actionBar) {
				ActionBar.sendActionBar(victim, Message.ACTIONBAR_IN_COMBAT.getMessage().replace("%time%",
						String.valueOf(plugin.getCombatLog().tagTimeRemaining(victim.getUniqueId()))));
				
//				victim.spigot().sendMessage(ChatMessageType.ACTION_BAR,
//						TextComponent.fromLegacyText(Message.ACTIONBAR_IN_COMBAT.getMessage().replace("%time%",
//								String.valueOf(plugin.getCombatLog().tagTimeRemaining(victim.getUniqueId())))));
			}

			if (plugin.getSettings().cl_disabledOnTag.contains("fly")) {
				plugin.getCombatLog().removeFly(victim);
			}
		} else {
			plugin.getCombatLog().taggedPlayers.replace(victim.getUniqueId(),
					Long.valueOf(plugin.getCombatLog().getCurrentTime()));

			plugin.getCombatLog().removeFly(victim);
		}
	}
}
