package com.Furnesse.core.utils;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

import com.Furnesse.core.Core;

import me.clip.placeholderapi.PlaceholderAPI;

public class Scoreboard implements Listener {

	private static Core plugin = Core.instance;

	private static ArrayList<UUID> playerSb = new ArrayList<UUID>();

	public static void setScoreboard(Player player) {
		if (!plugin.usingSb) {
			return;
		}

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();

		String boardName = plugin.getConfig().getString("scoreboard.title");
		boardName = PlaceholderAPI.setPlaceholders(player, boardName);
		@SuppressWarnings("deprecation")
		Objective obj = board.registerNewObjective("Server", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		if (boardName == "" || boardName == null)
			boardName = " ";
		obj.setDisplayName(Lang.chat(boardName));

		if (plugin.lines != null) {

			int val = plugin.lines.size() + 1;
			for (String line : plugin.lines) {
				val--;
				line = PlaceholderAPI.setPlaceholders(player, line);
				if (line.length() > 40) {
					plugin.getLogger().severe("Couldn't load: " + line
							+ " Make sure you have the placeholder installed, if not do /papi ecloud download <placeholder>");
					continue;
				}
				Score name = obj.getScore(Lang.chat(line));
				name.setScore(val);
				obj.getScore(line).setScore(val);

			}
		}
		player.setScoreboard(board);
		playerSb.add(player.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				if (player == null || !player.isOnline() || playerSb.contains(player.getUniqueId())) {
					cancel();
					return;
				}

				updateScoreboard(player);
			}
		}.runTaskTimer(plugin, 10, 100);

	}

	public static void toggleScoreboard(Player player) {
		UUID uuid = player.getUniqueId();
		if (playerSb.contains(uuid)) {
			removeScoreboard(player);
		} else {
			setScoreboard(player);
		}
	}

	public static void removeScoreboard(Player player) {
		UUID uuid = player.getUniqueId();
		if (playerSb.contains(uuid)) {
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			playerSb.remove(uuid);
		}
	}

	public static void updateScoreboard(Player player) {
		if (!plugin.usingSb) {
			return;
		}

		for (String str : player.getScoreboard().getEntries()) {
			player.sendMessage("test: " + str);
			player.getScoreboard().resetScores(str);
		}

		org.bukkit.scoreboard.Scoreboard score = player.getScoreboard();
		int val = plugin.lines.size() + 1;
		for (String line : plugin.lines) {
			val--;
			score.getObjective(DisplaySlot.SIDEBAR).getScore(line).setScore(val);
		}

	}
}
