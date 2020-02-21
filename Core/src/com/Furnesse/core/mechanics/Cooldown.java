package com.Furnesse.core.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Debug;
import com.Furnesse.core.utils.Lang;

public class Cooldown {
	Core plugin = Core.instance;

	private String taskname;
	private String command;
	private boolean cmdOnCooldown;
	private long disabledTime;
	private long enabledTime;
	private String broadcastMsg;
	private String disabledMsg;

	private long timeRemaning;

	public Cooldown(String taskname, String command, long disabledTime, long enabledTime) {
		this.taskname = taskname;
		this.command = command;
		this.disabledTime = disabledTime;
		this.enabledTime = enabledTime;
		this.broadcastMsg = Lang.chat(plugin.getConfig().getString("cooldownTasks." + taskname + ".broadcastMsg")
				.replace("%enabledfor%", String.valueOf(this.enabledTime))
				.replace("%disabledfor%", String.valueOf(this.disabledTime)));
		this.disabledMsg = Lang.chat(plugin.getConfig().getString("cooldownTasks." + taskname + ".disabledMsg")
				.replace("%enabledfor%", String.valueOf(this.enabledTime))
				.replace("%disabledfor%", String.valueOf(this.disabledTime)));

		this.cmdOnCooldown = true;
	}

	public long getTimeRemaning() {
		return timeRemaning;
	}

	public void setTimeRemaning(long timeRemaning) {
		this.timeRemaning = timeRemaning;
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

	public long getDisabledTime() {
		return disabledTime;
	}

	public long getEnabledTime() {
		return enabledTime;
	}

	// true = !true
	// false = !false
	public void startTimer(boolean isDisabled) {
		long cooldown;
		if (isDisabled)
			cooldown = disabledTime;
		else
			cooldown = enabledTime;

		new BukkitRunnable() {
			long start = System.currentTimeMillis() / 1000;

			public void run() {
				long currentTime = System.currentTimeMillis() / 1000;
				long delta = currentTime - start;
				timeRemaning = delta;
				if (timeRemaning >= cooldown) {
					cmdOnCooldown = !isDisabled;
					Bukkit.broadcastMessage(Lang.chat(broadcastMsg));
					this.cancel();
					startTimer(cmdOnCooldown);
				}
				Debug.Log("time remaning " + timeRemaning + " | " + cmdOnCooldown);
			}
		}.runTaskTimer(plugin, 20, 20);
	}
}
