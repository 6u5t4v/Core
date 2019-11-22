package com.Furnesse.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.chat.ChatFormat;
import com.Furnesse.core.utils.Lang;

import me.clip.placeholderapi.PlaceholderAPI;

public class ChatEvent implements Listener {

	Core plugin = Core.instance;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if (!plugin.usingChat) {
			return;
		}
		
		Player player = e.getPlayer();

		ChatFormat format = plugin.chatFormat.getPlayerFormat(player);
		
		String prefix = format.getPrefix();
		String suffix = format.getSuffix();
		
		prefix = PlaceholderAPI.setPlaceholders(player, prefix).split("%")[0];
		suffix = PlaceholderAPI.setPlaceholders(player, suffix).split("%")[0];
		
		String message = Lang.chat(prefix + player.getDisplayName() + suffix + format.getMessage() + e.getMessage());
		e.setFormat(message);
		e.setMessage(e.getFormat());

	}
}
