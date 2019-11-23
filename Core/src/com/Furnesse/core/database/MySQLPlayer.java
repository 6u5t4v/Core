package com.Furnesse.core.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;

public class MySQLPlayer {
	static Core plugin = Core.instance;
	
	public static boolean playerExists(UUID uuid) {
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.playerTable + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			
			ResultSet results = statement.executeQuery();
			if(results.next()) {
				Bukkit.broadcastMessage("§aPlayers was found");
				return true;
			}
			
			Bukkit.broadcastMessage("§cPlayers was NOT found");
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
		
	public static void createPlayer(final UUID uuid, Player player) {
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.playerTable + " WHERE UUID=?");
			
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();
			if(!playerExists(uuid)) {
				String sql = "INSERT INTO " + plugin.playerTable + "(uuid,username) VALUES (?,?)";
				PreparedStatement insert = plugin.getConnection().prepareStatement(sql);
				insert.setString(1, uuid.toString());
				insert.setString(2, player.getName());
				insert.executeUpdate();
				
				Bukkit.broadcastMessage("§aPlayer inserted");
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
