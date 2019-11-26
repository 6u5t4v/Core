package com.Furnesse.core.Events;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;

import me.clip.placeholderapi.PlaceholderAPI;

public class Scoreboard {

	private Core plugin;

	public Scoreboard(Core plugin) {
		this.plugin = plugin;
	}

	private ArrayList<UUID> playerSb = new ArrayList<UUID>();	

	public void setScoreboard(Player player) {
		if (!plugin.usingSb) {
			return;
		}

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
		String boardName = plugin.boardName;
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

				Score score = obj.getScore(line);
				score.setScore(val);

				player.sendMessage(Lang.chat(line));
				
				Team team = board.registerNewTeam("scoreboard");
				team.addEntry(line);

				if (line.length() < 40) {
					team.setPrefix(Lang.chat(line));
					return;
				}

				if (line.length() > 40) {
					line = line.substring(40);
				}

				team.setPrefix(Lang.chat(line.substring(0, 40)));
				team.setSuffix(Lang.chat(line.substring(40)));
				obj.getScore(Lang.chat(line)).setScore(val);
			}

			player.setScoreboard(board);
			playerSb.add(player.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					if (player == null || !player.isOnline() || !playerSb.contains(player.getUniqueId())) {
						cancel();
						return;
					}

					updateScoreboard(player);
				}
			}.runTaskTimer(plugin, 10, 100);
		}
	}

	public void toggleScoreboard(Player player) {
		UUID uuid = player.getUniqueId();
		if (playerSb.contains(uuid)) {
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			playerSb.remove(uuid);
		} else {
			setScoreboard(player);
		}
	}

	public void removeScoreboard(Player player) {
		UUID uuid = player.getUniqueId();
		if (playerSb.contains(uuid)) {
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			playerSb.remove(uuid);
		}
	}

	public void updateScoreboard(Player player) {
		if (!plugin.usingSb) {
			return;
		}

		org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();

		for (String line : plugin.lines) {
			line = PlaceholderAPI.setPlaceholders(player, line);

			if (line.length() < 40) {
				board.getTeam(line).setPrefix(Lang.chat(line));
				return;
			}

			if (line.length() > 40) {
				line = line.substring(40);
			}

			board.getTeam(line).setPrefix(Lang.chat(line.substring(0, 40)));
			board.getTeam(line).setSuffix(Lang.chat(line.substring(40)));
		}

	}
}
