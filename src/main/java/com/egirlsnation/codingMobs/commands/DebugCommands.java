package com.egirlsnation.codingMobs.commands;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.egirlsnation.codingMobs.Main;

public class DebugCommands implements CommandExecutor {

	private Logger log;
	private Main plugin;

	public DebugCommands(Main plugin) {
		this.log = plugin.log;
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {

		if (arg0 instanceof Player) {
			Player player = (Player) arg0;

		}

		return false;

	}

}
