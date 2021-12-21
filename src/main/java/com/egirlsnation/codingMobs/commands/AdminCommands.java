package com.egirlsnation.codingMobs.commands;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.egirlsnation.codingMobs.Config;
import com.egirlsnation.codingMobs.LogFormatter;
import com.egirlsnation.codingMobs.Main;

import net.md_5.bungee.api.ChatColor;

public class AdminCommands implements CommandExecutor {

	Logger log;
	Main plugin;

	public AdminCommands(Main plugin) {
		this.log = plugin.log;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean execute = false;

		// Check permissions
		if (sender instanceof Player) {

			if (sender.hasPermission("codingMobs.admin")) {
				execute = true;
			}

			if (sender.isOp()) {
				execute = true;
			}

		} else if (!(sender instanceof Player)) {
			execute = true;
		}

		// Execute command or reject
		if (execute) {

			if (args == null || args.length == 0) {
				if (sender instanceof Player) {
					sender.sendMessage(ChatColor.RED + "no args were provided.");
					return false;
				} else {
					log.info("no args were provided.");
					return false;
				}

			}

			if (args[0].equalsIgnoreCase("reload")) {
				Config.loadCfg();
				if (sender instanceof Player) {
					sender.sendMessage(ChatColor.GREEN + "CodingMobs plugin config has been reloaded successfully.");
				}
				log.info(LogFormatter.format(LogFormatter.priority.HIGH, "Commands",
						"Plugin Configuration reloaded successfully."));
				return true;
			}

		} else {
			if (sender instanceof Player) {
				log.info(LogFormatter.format(LogFormatter.priority.HIGH, "Commands",
						sender.getName() + " Attempted to run admin command without permission."));
				sender.sendMessage(ChatColor.RED + Config.getMessage("no-perm"));
			}
			return false;
		}

		return false;

	}

}
