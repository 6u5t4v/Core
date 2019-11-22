package com.Furnesse.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

public class ChatEvent implements Listener {

	Core plugin = Core.instance;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if (!plugin.usingChat) {
			return;
		}
		
		String msg = e.getMessage();
		String format = plugin.chatFormat.getPlayerFormat(e.getPlayer()).getFormat();
		
		format.replace("%", "%%");
		e.setFormat(Lang.chat(format + msg));
	}
}
