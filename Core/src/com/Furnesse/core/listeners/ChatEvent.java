package com.Furnesse.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

import me.clip.placeholderapi.PlaceholderAPI;

public class ChatEvent implements Listener {

	Core plugin = Core.instance;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {		
		String msg = e.getMessage();
		String format = plugin.getChatFormat().getPlayerFormat(e.getPlayer()).getFormat();
		
		format = PlaceholderAPI.setPlaceholders(e.getPlayer(), format);
		e.setFormat(Lang.chat(format.replace("%msg%", msg)).replace("%", "%%"));
		e.setMessage(e.getFormat());
	}
}
