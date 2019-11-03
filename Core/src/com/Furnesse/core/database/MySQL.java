package com.Furnesse.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.Furnesse.core.Core;

public class MySQL {

	Core plugin;
	
	public MySQL (Core plugin) {
		this.plugin = plugin;
	}

	private Connection connection;
	private String host, database, username, password;
	private int port;

	public void loadDatabase() {
		host = plugin.getConfig().getString("database.mysql.host");
		port = plugin.getConfig().getInt("database.mysql.port");
		database = plugin.getConfig().getString("database.mysql.database");
		username = plugin.getConfig().getString("database.mysql.username");
		password = plugin.getConfig().getString("database.mysql.password");

		try {
			openConnection();
			Statement statement = connection.createStatement();
		} catch (ClassNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void openConnection() throws SQLException, ClassNotFoundException {
		if (connection != null && !connection.isClosed())
			return;

		synchronized (this) {
			if (connection != null && !connection.isClosed())
				return;

			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
		}
	}
}
