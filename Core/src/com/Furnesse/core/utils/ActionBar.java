package com.Furnesse.core.utils;

import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle.EnumTitleAction;

public class ActionBar {

	public static void sendActionBar(Player player, String message) {
		
		CraftPlayer p = (CraftPlayer) player;

		IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.ACTIONBAR, cbc);
		p.getHandle().playerConnection.sendPacket(packet);
	}
}
