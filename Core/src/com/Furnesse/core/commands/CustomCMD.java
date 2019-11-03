package com.Furnesse.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.customcommands.CustomCommand;

public class CustomCMD implements CommandExecutor {

	Core plugin = Core.instance;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			// Player commands
			CustomCommand ccmd = plugin.getCommands().getCommand(cmd.getName());
			if (cmd.getName().equalsIgnoreCase(ccmd.getCmd())) {
				if (player.hasPermission(ccmd.getPerm()) || ccmd.getPerm().equals("") || ccmd.getPerm() == null) {
					for (String msg : ccmd.getMsg()) {
						player.sendMessage(msg);
					}
				}
			}
			return true;
		}
		return false;
	}
}
