package com.Furnesse.core.utils;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.Furnesse.core.Core;

import me.clip.placeholderapi.PlaceholderAPI;

public class ScoreboardOLD implements Listener {

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

				if (line.length() > 16) {
					line = line.substring(16);
					
					Team lineTeam = board.registerNewTeam(line);
					lineTeam.addEntry(line);
					
					lineTeam.setPrefix(Lang.chat(line).substring(0, 8));
					lineTeam.setSuffix(Lang.chat(line).substring(8));
					continue;
				}

				Team lineTeam = board.registerNewTeam(line);
				lineTeam.addEntry(line);
				lineTeam.setPrefix(Lang.chat(line));
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
		}.runTaskTimer(plugin, 20 * 4, 20);

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

		org.bukkit.scoreboard.Scoreboard score = player.getScoreboard();
		int val = plugin.lines.size() + 1;
		for (String line : plugin.lines) {
			val--;
			line = PlaceholderAPI.setPlaceholders(player, line);

			if (line.length() <= 16) {
				score.getTeam(line).setPrefix(Lang.chat(line));
				continue;
			}

			if (line.length() > 32) {
				line = line.substring(32);
			}

			score.getTeam(line).setPrefix(Lang.chat(line).substring(0, 16));
			score.getTeam(line).setSuffix(Lang.chat(line).substring(16));
		}

	}
}
