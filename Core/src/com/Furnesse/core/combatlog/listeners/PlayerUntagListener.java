package com.Furnesse.core.combatlog.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Furnesse.core.Core;
import com.Furnesse.core.Events.PlayerUntagEvent;
import com.Furnesse.core.config.Message;
import com.Furnesse.core.utils.ActionBar;

public class PlayerUntagListener implements Listener {
	Core plugin = Core.instance;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUntag(PlayerUntagEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();

		switch (e.getCause()) {
		case DEATH:
			p.sendMessage(Message.UNTAG.getMessage());
			break;
		case SAFE_AREA:
			if (plugin.getSettings().cl_actionBar) {
				ActionBar.sendActionBar(p, Message.ACTION_UNTAG.getMessage());
			}

			p.sendMessage(Message.UNTAG.getMessage());
			break;
		case TIME_EXPIRE:
			if (plugin.getSettings().cl_actionBar) {
				ActionBar.sendActionBar(p, Message.ACTION_UNTAG.getMessage());

//				p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
//						TextComponent.fromLegacyText(Message.ACTION_UNTAG.getMessage()));
			}
			p.sendMessage(Message.UNTAG.getMessage());
			break;
		case COMBATLOG:
			break;
		case KICK:
			break;
		case LAGOUT:
			break;
		}

		plugin.getCombatLog().taggedPlayers.remove(uuid);
	}
}
