package com.Furnesse.core.sidebar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;
import com.Furnesse.core.utils.Settings;
import com.Furnesse.core.utils.SyntaxTranslator;
import com.Furnesse.core.utils.TPSMeter;

public class Sidebar {
	private Core plugin;

	private Map<Integer, Line> sidebarConfigLines;

	private Map<String, Scoreboard> scoreboards = new HashMap<>();

	private SyntaxTranslator syntaxTranslator;

	private ChatColor[] colors = ChatColor.values();

	private static Map<UUID, Boolean> displaySidebar = new HashMap<>();

	private String[] teamNameAppends = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f", "g" };

	public Sidebar(Core plugin) {
		this.plugin = plugin;
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin) plugin, (Runnable) new TPSMeter(plugin),
				1L, 0);
		sidebarUpdater();
	}

	public void sidebarUpdater() {
		(new BukkitRunnable() {
			public void run() {
				for (Player p : Sidebar.this.plugin.getServer().getOnlinePlayers()) {
					if (Sidebar.displaySidebar.get(p.getUniqueId()) == null)
						Sidebar.displaySidebar.put(p.getUniqueId(), Boolean.valueOf(true));
					if (p.hasPermission("core.sb.show")) {
						if (((Boolean) Sidebar.displaySidebar.get(p)).booleanValue() && Core.instance.usingSb) {
							Scoreboard scoreboard;
							Objective objective;
							if (Sidebar.this.scoreboards.get(p.getName()) == null) {
								scoreboard = Sidebar.this.plugin.getServer().getScoreboardManager().getNewScoreboard();
								objective = scoreboard.registerNewObjective("PersonalSidebar", "dummy");
								objective.setDisplaySlot(DisplaySlot.SIDEBAR);
								Sidebar.this.scoreboards.put(p.getName(), scoreboard);
							} else {
								scoreboard = (Scoreboard) Sidebar.this.scoreboards.get(p.getName());
								objective = scoreboard.getObjective("PersonalSidebar");
							}
							objective.setDisplayName(Lang.chat(syntaxTranslator.Translate(Settings.getBoardName(), p)));
							List<String> allLines = new ArrayList<>();

							for (int i = Settings.getLines().size() - 1; i >= 0; i--) {
								Team team = null;
								String playerName = p.getName();
								if (scoreboard.getTeam(
										String.valueOf(playerName.substring(0, Math.min(playerName.length(), 15)))
												+ Sidebar.this.teamNameAppends[i]) == null) {
									team = scoreboard.registerNewTeam(
											String.valueOf(playerName.substring(0, Math.min(playerName.length(), 15)))
													+ Sidebar.this.teamNameAppends[i]);
								} else {
									team = scoreboard.getTeam(
											String.valueOf(playerName.substring(0, Math.min(playerName.length(), 15)))

											+ Sidebar.this.teamNameAppends[i]);
								}
								if (!team.hasEntry(Sidebar.this.colors[i] + "" + ChatColor.RESET))
									team.addEntry(Sidebar.this.colors[i] + "" + ChatColor.RESET);

								team.setPrefix(Lang.chat(((String) allLines.get(i)).substring(0,
										Math.min(((String) allLines.get(i)).length(), 64))));

								objective.getScore(Sidebar.this.colors[i] + "" + ChatColor.RESET).setScore(i);
							}
							if (allLines.size() < scoreboard.getTeams().size())
								Sidebar.this.scoreboards = (Map) new HashMap<>();
							p.setScoreboard(scoreboard);
							continue;
						}
						p.setScoreboard(Sidebar.this.plugin.getServer().getScoreboardManager().getNewScoreboard());
					}
				}
			}
		}).runTaskTimer((Plugin) this.plugin, 0L, this.plugin.getConfig().getInt("SidebarUpdateSpeed"));
	}

	public static boolean toggleSidebar(Player p) {
		if (displaySidebar.get(p.getUniqueId()) == null) {
			displaySidebar.put(p.getUniqueId(), Boolean.valueOf(false));
			return false;
		}
		if (((Boolean) displaySidebar.get(p.getUniqueId())).booleanValue()) {
			displaySidebar.put(p.getUniqueId(), Boolean.valueOf(false));
			return false;
		}
		if (!((Boolean) displaySidebar.get(p.getUniqueId())).booleanValue()) {
			displaySidebar.put(p.getUniqueId(), Boolean.valueOf(true));
			return true;
		}
		return false;
	}
}
