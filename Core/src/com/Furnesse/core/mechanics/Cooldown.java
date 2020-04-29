package com.Furnesse.core.mechanics;

import java.time.Duration;
import java.time.Instant;

import org.bukkit.Bukkit;

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

	private int taskId;

	public Cooldown(String taskname, String command, String disabledTime, String enabledTime) {
		this.taskname = taskname;
		this.command = command;
		this.disabledTime = disabledTime;
		this.enabledTime = enabledTime;
		this.eventStartMsg = plugin.getConfig().getString("cooldownTasks." + taskname + ".eventStartMsg");
		this.eventEndMsg = plugin.getConfig().getString("cooldownTasks." + taskname + ".eventEndMsg");
		this.disabledMsg = plugin.getConfig().getString("cooldownTasks." + taskname + ".disabledMsg");
	}

	public void stopTask() {
		Bukkit.getScheduler().cancelTask(this.taskId);
		this.cmdOnCooldown = false;
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

	public void startTimer(boolean isDisabled) {
		long cooldown = isDisabled ? Time.convertCooldownToSeconds(disabledTime)
				: Time.convertCooldownToSeconds(enabledTime);

		cmdOnCooldown = isDisabled;
		this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			Instant start = Instant.now();
			Duration period = Duration.ofSeconds(cooldown);

			public void run() {
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
					Bukkit.getScheduler().cancelTask(taskId);
					startTimer(cmdOnCooldown);
				}
//				Debug.Log("time remaning " + timeRemaining + " | " + cmdOnCooldown);
			}
		}, 20, 20);
	}
}
