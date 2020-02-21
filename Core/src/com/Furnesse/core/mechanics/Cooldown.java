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
	private String broadcastMsg;
	private String disabledMsg;

	private long timeRemaining;

	public Cooldown(String taskname, String command, String disabledTime, String enabledTime) {
		this.taskname = taskname;
		this.command = command;
		this.disabledTime = disabledTime;
		this.enabledTime = enabledTime;
		this.broadcastMsg = plugin.getConfig().getString("cooldownTasks." + taskname + ".broadcastMsg");
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

	public void startTimer(boolean isDisabled) {
		long cooldown = isDisabled ? Time.convertCooldownToSeconds(disabledTime)
				: Time.convertCooldownToSeconds(enabledTime);

		cmdOnCooldown = isDisabled;
		new BukkitRunnable() {
			Instant start = Instant.now();
			Duration period = Duration.ofSeconds(cooldown);

			public void run() {
				Duration passed = Duration.between(start, Instant.now());
				Duration remaining = period.minus(passed);

				timeRemaining = remaining.getSeconds();
				if (remaining.isZero() || remaining.isNegative()) {
					setCmdOnCooldown(!isDisabled);
					Bukkit.broadcastMessage(Lang.chat(
							broadcastMsg.replace("%enabledfor%", enabledTime).replace("%disabledfor%", disabledTime)));
					this.cancel();
					startTimer(cmdOnCooldown);
				}
//				Debug.Log("time remaning " + timeRemaining + " | " + cmdOnCooldown);
			}
		}.runTaskTimer(plugin, 20, 20);
	}
}
