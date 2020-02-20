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

	private long dTimeRemaning;
	private long eTimeRemaning;

	public Cooldown(String taskname, String command, long disabledTime, long enabledTime) {
		this.taskname = taskname;
		this.command = command;
		this.disabledTime = disabledTime;
		this.enabledTime = enabledTime;
		this.dTimeRemaning = this.disabledTime;
		this.dTimeRemaning = this.enabledTime;
		this.broadcastMsg = Lang.chat(plugin.getConfig().getString("cooldownTasks." + taskname + ".broadcastMsg")
				.replace("%enabledfor%", String.valueOf(this.enabledTime))
				.replace("%disabledfor%", String.valueOf(this.disabledTime)));
		this.disabledMsg = Lang.chat(plugin.getConfig().getString("cooldownTasks." + taskname + ".disabledMsg")
				.replace("%enabledfor%", String.valueOf(this.enabledTime))
				.replace("%disabledfor%", String.valueOf(this.disabledTime)));

		this.cmdOnCooldown = true;
	}

//	public void startCooldown() {
//		new BukkitRunnable() {
//			long timer = getDisabledTime();
//
//			public void run() {
//				if (timer <= 0) {
//					this.cancel();
//				}
//				timer--;
//				setdTimeRemaning(timer);
//			}
//		}.runTaskTimer(plugin, this.disabledTime * 20, 20);
//	}

	public long getdTimeRemaning() {
		return dTimeRemaning;
	}

	public void setdTimeRemaning(long dTimeRemaning) {
		this.dTimeRemaning = dTimeRemaning;
	}

	public long geteTimeRemaning() {
		return eTimeRemaning;
	}

	public void seteTimeRemaning(long eTimeRemaning) {
		this.eTimeRemaning = eTimeRemaning;
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

	public long getDisabledTime() {
		return disabledTime;
	}

	public long getEnabledTime() {
		return enabledTime;
	}

	public void startDisableTimer() {
		new BukkitRunnable() {
			long lastRun = getDisabledTime() * 1000;

			public void run() {
				long timeSinceDisable = System.currentTimeMillis() - lastRun;
				if (timeSinceDisable <= getDisabledTime()) {
					cmdOnCooldown = false;

					Bukkit.broadcastMessage(Lang.chat(broadcastMsg.replace("%enabledfor%", String.valueOf(enabledTime))
							.replace("%disabledfor%", String.valueOf(disabledTime))));
					this.cancel();
					startEnableCooldown();
				}
				Debug.Log("disabled " + timeSinceDisable);
			}
		}.runTaskTimer(plugin, 20, 20);
	}

	private void startEnableCooldown() {
		new BukkitRunnable() {
			long lastRun = getEnabledTime() * 1000;

			public void run() {

				long timeSinceEnable = System.currentTimeMillis() - lastRun;
				if (timeSinceEnable <= getEnabledTime()) {
					cmdOnCooldown = true;
					this.cancel();
					startDisableTimer();
				}
				Debug.Log("enabled " + timeSinceEnable);
			}
		}.runTaskTimer(plugin, 20, 20);
	}
}
