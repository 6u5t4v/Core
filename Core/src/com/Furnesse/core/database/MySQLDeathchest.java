package com.Furnesse.core.database;

public class MySQLDeathchest {
//	static Core plugin = Core.instance;
//
//	public static boolean deathchestExists(UUID uuid) {
//		try {
//			PreparedStatement statement = plugin.getConnection()
//					.prepareStatement("SELECT * FROM " + plugin.deathchestTable + " WHERE uuid=?");
//			statement.setString(1, uuid.toString());
//
//			ResultSet results = statement.executeQuery();
//			if (results.next()) {
//				Bukkit.broadcastMessage("§aPlayers was found");
//				return true;
//			}
//
//			Bukkit.broadcastMessage("§cPlayers was NOT found");
//		} catch (SQLException e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	public static void removeDeathChests(UUID uuid) {
//		try {
//			String sql = "DELETE FROM " + plugin.deathchestTable + "WHERE uuid=" + uuid.toString();
//			PreparedStatement stmt = plugin.getConnection().prepareStatement(sql);
//			stmt.executeUpdate();
//
//			Bukkit.broadcastMessage("§aDeathChest deleted");
//
//		} catch (SQLException e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//	}
//
//	public static void createDeathchest(final UUID uuid, Player player, Location loc) {
//		String dcLoc = loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
//		try {
//
//			String sql = "INSERT INTO " + plugin.deathchestTable + "(uuid,owner,location) VALUES (?,?,?)";
//			PreparedStatement insert = plugin.getConnection().prepareStatement(sql);
//			insert.setString(1, uuid.toString());
//			insert.setString(2, player.getName());
//			insert.setString(2, dcLoc);
//			insert.executeUpdate();
//
//			Bukkit.broadcastMessage("§aCreated deathchest to mysql, loc: " + dcLoc);
//
//		} catch (SQLException e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//	}
//
//	public static void getDeathchestLoc(UUID uuid) {
//		if (deathchestExists(uuid)) {
//			try {
//				String sql = "SELECT location FROM " + plugin.deathchestTable + " WHERE uuid=" + uuid.toString();
//				PreparedStatement stmt = plugin.getConnection().prepareStatement(sql);
//				ResultSet results = stmt.executeQuery();
//
//				if (!results.next()) {
//					System.out.println("Couldnt find a deatchest at this location");
//				} else {
//					System.out.println("Success");
//					System.out.println(results.getString("location"));
//				}
//			} catch (SQLException e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
//		}else {
//			Debug.Log("deathchest does not exist");
//		}
//	}
}
