package com.egirlsnation.codingMobs.commands;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;

import com.egirlsnation.codingMobs.Bob;
import com.egirlsnation.codingMobs.Config;
import com.egirlsnation.codingMobs.LogFormatter;
import com.egirlsnation.codingMobs.Main;
import com.egirlsnation.codingMobs.Thief;

import net.md_5.bungee.api.ChatColor;

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

			// Player isn't op and doesn't have permission
			if (!(player.hasPermission("codingMobs.spawn"))) {

				if (!(player.isOp())) {
					log.info(LogFormatter.format(LogFormatter.priority.HIGH, "Commands",
							player.getName() + " Attempted to spawn a custom mob without permission."));
					player.sendMessage(ChatColor.RED + Config.getMessage("no-perm"));
					return true;
				}

			}

			if (arg3[0].equalsIgnoreCase("bob")) {

				Bob angryBob = new Bob(plugin, player.getLocation(), false, false);
				World world = player.getWorld();
				((CraftWorld) world).getHandle().addEntity(angryBob);
				log.info(LogFormatter.format(LogFormatter.priority.MEDIUM, "Commands",
						player.getName() + " Spawned a new bob at: X: " + player.getLocation().getX() + " Y: "
								+ player.getLocation().getY() + " Z: " + player.getLocation().getZ()));
				return true;

			} else if (arg3[0].equalsIgnoreCase("thief")) {

				Thief dirtyThief = new Thief(plugin, player.getLocation());
				World world = player.getWorld();
				((CraftWorld) world).getHandle().addEntity(dirtyThief);
				log.info(LogFormatter.format(LogFormatter.priority.MEDIUM, "Commands",
						player.getName() + " Spawned a new Thief at: X: " + player.getLocation().getX() + " Y: "
								+ player.getLocation().getY() + " Z: " + player.getLocation().getZ()));
				return true;

			}

		}

		return false;

	}

}
