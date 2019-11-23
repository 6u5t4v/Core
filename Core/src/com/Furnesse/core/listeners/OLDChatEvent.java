package com.Furnesse.core.listeners;

import org.bukkit.event.Listener;

public class OLDChatEvent implements Listener {

//	Core plugin = Core.instance;
//
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void onChat(AsyncPlayerChatEvent e) {
//		if (plugin.chatFormats == null || plugin.chatFormats.isEmpty()) {
//			return;
//		}
//
//		Player player = e.getPlayer();
////		Rank rank = plugin.getRanks().getPlayerRank(player);
//		String msg = e.getMessage();
//
//		for (String cFormat : plugin.chatFormats) {
//			String format = plugin.getConfig().getString("chat.formats." + cFormat + ".format");
//			if (player.hasPermission("core.chat." + cFormat) || cFormat.equals("default")) {
//				format = PlaceholderAPI.setPlaceholders(player, format);
//				e.setFormat(Lang.chat(format).replace("%msg%", msg));
//				e.setMessage(e.getFormat());
//			}
//		}
//	}
}	
