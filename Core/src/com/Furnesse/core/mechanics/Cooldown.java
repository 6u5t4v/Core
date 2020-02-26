package com.Furnesse.core.mechanics;

import java.time.Duration;
import java.time.Instant;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Lang;
import com.Furnesse.core.utils.Time;

public class Cooldown {
	Core plugin = Core.instance;

	private String taskname;
	private String command;

	private boolean cmdOnCooldown;

	private String disabledTime;
	private String enabledTime;

	private String eventStartMsg;
	private String eventEndMsg;
	private String disabledMsg;

	private long timeRemaining;

	public Cooldown(String taskname, String command, String disabledTime, String enabledTime) {
		this.taskname = taskname;
		this.command = command;
		this.disabledTime = disabledTime;
		this.enabledTime = enabledTime;
		this.eventStartMsg = plugin.getConfig().getString("cooldownTasks." + taskname + ".eventStartMsg");
		this.eventEndMsg = plugin.getConfig().getString("cooldownTasks." + taskname + ".eventEndMsg");
		this.disabledMsg = plugin.getConfig().getString("cooldownTasks." + taskname + ".disabledMsg");
	}

	public long getTimeRemaning() {
		return timeRemaining;
	}

	public void setTimeRemaning(long timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public String getTaskname() {
		return taskname;
	}

	public String getDisabledMsg() {
		return disabledMsg;
	}

	public String getCommand() {
		return command;
	}

	public boolean isCmdOnCooldown() {
		return cmdOnCooldown;

//		long lastCheck = System.currentTimeMillis() / 1000;
//		this.dTimeRemaning = lastCheck;
//		if (!(lastCheck >= disabledTime)) {
//			cmdOnCooldown = true;
//		} else {
//			cmdOnCooldown = false;
//		}
//		return cmdOnCooldown;
	}

	public void setCmdOnCooldown(boolean cmdOnCooldown) {
		this.cmdOnCooldown = cmdOnCooldown;
	}

	public String getDisabledTime() {
		return disabledTime;
	}

	public String getEnabledTime() {
		return enabledTime;
	}

	public void startTimer(boolean isDisabled, boolean run) {
		long cooldown = isDisabled ? Time.convertCooldownToSeconds(disabledTime)
				: Time.convertCooldownToSeconds(enabledTime);

		cmdOnCooldown = isDisabled;
		new BukkitRunnable() {
			Instant start = Instant.now();
			Duration period = Duration.ofSeconds(cooldown);

			public void run() {
				if (!run)
					this.cancel();
				Duration passed = Duration.between(start, Instant.now());
				Duration remaining = period.minus(passed);

				timeRemaining = remaining.getSeconds();
				if (timeRemaining <= 0) {
					if (isDisabled)
						Bukkit.broadcastMessage(Lang.chat(eventStartMsg.replace("%enabledfor%", enabledTime)
								.replace("%disabledfor%", disabledTime).replace("%timeRemaining%", disabledTime)));
					else
						Bukkit.broadcastMessage(Lang.chat(eventEndMsg.replace("%enabledfor%", enabledTime)
								.replace("%disabledfor%", disabledTime).replace("%timeRemaining%", disabledTime)));

					cmdOnCooldown = !isDisabled;
					this.cancel();
					startTimer(cmdOnCooldown, true);
				}
//				Debug.Log("time remaning " + timeRemaining + " | " + cmdOnCooldown);
			}
		}.runTaskTimer(plugin, 20, 20);
	}
}
