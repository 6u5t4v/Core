package com.Furnesse.core.interfaces;

import org.bukkit.entity.Player;

import com.Furnesse.core.chat.ChatFormat;

public interface Chat {

	public String getPrefix(Player p);

	public String getSuffix(Player p);
	
	public ChatFormat getChatFormat(Player p);
}
