package com.egirlsnation.codingMobs;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.egirlsnation.codingMobs.commands.SpawnCommands;
import com.egirlsnation.codingMobs.commands.SpawnTabCompleter;
import com.egirlsnation.codingMobs.events.ChunkLoadListener;
import com.egirlsnation.codingMobs.events.MobEventListener;
import com.egirlsnation.codingMobs.events.PlayerJoinListener;

public class Main extends JavaPlugin {

	public Logger log;
	public boolean supportsInventoryStackSize = true;

	@Override
	public void onEnable() {

		log = this.getLogger();

		// Initialize classes and commands
		this.getCommand("spawn").setExecutor(new SpawnCommands(this));
		this.getCommand("spawn").setTabCompleter(new SpawnTabCompleter());
		Config.init(this);

		// Register the plugin listener
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new MobEventListener(this), this);
		pm.registerEvents(new ChunkLoadListener(this), this);
		pm.registerEvents(new PlayerJoinListener(this), this);

		log.info("codingMobs plugin has been enabled!");

		// Update the mobs at spawn after a server restart
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {

				for (Entity entity : getServer().getWorld("world").getLivingEntities()) {

					// Check for custom villagers
					if ((entity instanceof Villager) && entity.getCustomName() != null) {

						Location location = entity.getLocation();
						World world = getServer().getWorld("world");
						entity.remove();
						Thief dirtyThief = new Thief(Main.this, location);
						((CraftWorld) world).getHandle().addEntity(dirtyThief);

						// Check for custom snow golem
					} else if ((entity instanceof Snowman) && entity.getCustomName() != null) {

						Location location = entity.getLocation();
						World world = getServer().getWorld("world");
						entity.remove();
						Bob angryBob = new Bob(Main.this, location, false, false);
						((CraftWorld) world).getHandle().addEntity(angryBob);

					}

				}

			}
		};
		runnable.runTaskLater(this, 1L);

	}

	@Override
	public void onDisable() {
		log.info("codingMobs plugin has been disabled!");
	}

}
