package com.Furnesse.core.combatlog.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.Events.PlayerTagEvent;

public class EntityDamageByEntityListener implements Listener {

	Core plugin = Core.instance;

	@EventHandler
	public void onPlayerDamageByPlayer(EntityDamageByEntityEvent e) {
		Entity victim = e.getEntity();
		Entity damager = e.getDamager();
		if (victim instanceof Player && damager instanceof Player) {
			PlayerTagEvent event = new PlayerTagEvent((Player) damager, (Player) victim,
					plugin.getSettings().cl_tagDuration);
			plugin.getServer().getPluginManager().callEvent(event);
		}

		if (victim instanceof Player && damager instanceof Projectile) {
			Projectile proj = (Projectile) damager;
			if (proj.getShooter() instanceof Player) {
				
				PlayerTagEvent event = new PlayerTagEvent((Player) ((Projectile) damager).getShooter(), (Player) victim,
						plugin.getSettings().cl_tagDuration);
				plugin.getServer().getPluginManager().callEvent(event);
			}
		}
	}
}
