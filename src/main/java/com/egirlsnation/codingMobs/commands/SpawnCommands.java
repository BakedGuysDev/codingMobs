package com.egirlsnation.codingMobs.commands;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;

import com.egirlsnation.codingMobs.Bob;
import com.egirlsnation.codingMobs.Main;
import com.egirlsnation.codingMobs.Thief;

public class SpawnCommands implements CommandExecutor {

	private Logger log;
	private Main plugin;

	public SpawnCommands(Main plugin) {
		this.log = plugin.log;
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {

		if (arg3 == null || arg3.length == 0) {
			log.info("no args were provided.");
			return false;
		}
			
		
		if (arg0 instanceof Player) {
			Player player = (Player) arg0;

			if (arg3[0].equalsIgnoreCase("snowman")) {

				Bob angryBob = new Bob(plugin, player.getLocation(), false, false);
				World world = player.getWorld();
				((CraftWorld) world).getHandle().addEntity(angryBob);

			} else if (arg3[0].equalsIgnoreCase("thief")) {

				Thief dirtyThief = new Thief(plugin, player.getLocation());
				World world = player.getWorld();
				((CraftWorld) world).getHandle().addEntity(dirtyThief);

			}

		}

		return true;

	}

}
