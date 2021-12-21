package com.egirlsnation.codingMobs.events;

import java.util.logging.Logger;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import com.egirlsnation.codingMobs.Bob;
import com.egirlsnation.codingMobs.Main;
import com.egirlsnation.codingMobs.Thief;

public class ChunkLoadListener implements Listener {

	private final Logger log;
	private final Main plugin;

	public ChunkLoadListener(Main plugin) {
		this.log = plugin.log;
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChunkLoad(ChunkLoadEvent event) {

		// Replace the custom entites because the AI will not load
		Chunk loadedChunk = event.getChunk();

		for (Entity entity : loadedChunk.getEntities()) {

			// Check for custom villagers
			if ((entity instanceof Villager) && entity.getCustomName() != null) {

				Location location = entity.getLocation();
				World world = plugin.getServer().getWorld(entity.getWorld().getName());
				entity.remove();
				Thief dirtyThief = new Thief(plugin, location);
				((CraftWorld) world).getHandle().addEntity(dirtyThief);

				// Check for custom snow golem
			} else if ((entity instanceof Snowman) && entity.getCustomName() != null) {

				Location location = entity.getLocation();
				World world = plugin.getServer().getWorld(entity.getWorld().getName());
				entity.remove();
				Bob angryBob = new Bob(plugin, location, false, false);
				((CraftWorld) world).getHandle().addEntity(angryBob);

			}

		}

	}

}
