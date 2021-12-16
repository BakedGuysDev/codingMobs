package com.egirlsnation.codingMobs.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class SpawnTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		if (args.length == 1) {
			
			List<String> options = new ArrayList<String>();
			options.add("bob");
			options.add("thief");
			return options;
			
		}
		
		return null;
	}

}
