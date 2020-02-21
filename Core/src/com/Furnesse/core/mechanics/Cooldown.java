package com.Furnesse.core.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.Furnesse.core.Core;
import com.Furnesse.core.utils.Debug;
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

	private long timeRemaning;

	public Cooldown(String taskname, String command, String disabledTime, String enabledTime) {
		this.taskname = taskname;
		this.command = command;
		this.disabledTime = disabledTime;
		this.enabledTime = enabledTime;
		this.broadcastMsg = plugin.getConfig().getString("cooldownTasks." + taskname + ".broadcastMsg");
		this.disabledMsg = plugin.getConfig().getString("cooldownTasks." + taskname + ".disabledMsg");
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

	public String getDisabledTime() {
		return disabledTime;
	}

	public String getEnabledTime() {
		return enabledTime;
	}

	// true = !true
	// false = !false
	public void startTimer(boolean isDisabled) {
		long cooldown = isDisabled ? Time.convertCooldownToSeconds(disabledTime)
				: Time.convertCooldownToSeconds(enabledTime);

		cmdOnCooldown = isDisabled;
		new BukkitRunnable() {
			long start = System.currentTimeMillis() / 1000;

			public void run() {
				timeRemaning = System.currentTimeMillis() / 1000 - start;

				if (timeRemaning >= cooldown) {
					setCmdOnCooldown(!isDisabled);
					Bukkit.broadcastMessage(Lang.chat(
							broadcastMsg.replace("%enabledfor%", enabledTime).replace("%disabledfor%", disabledTime)));
					this.cancel();
					startTimer(cmdOnCooldown);
				}
//				Debug.Log("time remaning " + timeRemaning + " | " + cmdOnCooldown);
			}
		}.runTaskTimer(plugin, 20, 20);
	}
}
