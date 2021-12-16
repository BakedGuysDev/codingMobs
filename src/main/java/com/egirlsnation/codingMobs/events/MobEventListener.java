package com.egirlsnation.codingMobs.events;

import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftCreature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.egirlsnation.codingMobs.Bob;
import com.egirlsnation.codingMobs.Main;
import com.egirlsnation.codingMobs.Thief;

import net.md_5.bungee.api.ChatColor;

public class MobEventListener implements Listener {

	private final Logger log;
	private final Main plugin;

	public MobEventListener(Main plugin) {
		this.log = plugin.log;
		this.plugin = plugin;
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {

		// Check if custom villager is damaged
		if (event.getEntity() instanceof Villager && event.getEntity().getCustomName() != null
				&& event.getDamager() instanceof Player) {

			// Drop gold nugget from villager
			Random r = new Random();
			event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
					new ItemStack(Material.GOLD_NUGGET));

		}

		// Check if custom villager attacked player
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Villager
				&& event.getDamager().getCustomName() != null) {

			try {
			((Thief) (((CraftCreature) event.getDamager()).getHandle())).setAttacked(true);
			((Thief) (((CraftCreature) event.getDamager()).getHandle())).stealItems((Player) event.getEntity());
			
			((Player) (event.getEntity()))
					.sendMessage(ChatColor.RED + "The dirty thief stole your items. Kill him to get your items back!");
			} catch (Exception ex) {
				// class loader exception, did you fucking reload the plugin idiot?
				// reloading the chunks will fix it you baka so chill
			}
			
			((Player) event.getEntity()).damage(5.0D);
			event.setCancelled(true);
			
		}
		
		// Check if snowman attacked player
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Snowman
				&& event.getDamager().getCustomName() != null) {
			
			((Player) event.getEntity()).damage(5.0D);
			event.setCancelled(true);
			
		}
		
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {

		if (!(event.getEntity() instanceof Villager)) {
			return;
		}

		if (event.getEntity().getCustomName() == null) {
			return;
		}

		try {
			for (ItemStack item : ((Thief) ((CraftCreature) event.getEntity()).getHandle()).getStolenItems()) {
				if (item != null) {
					event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), item);
				}
			}
		} catch (Exception ex) {
			// The villiger had an empty stolenItems list :KEK:
		}

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Random r = new Random();

		if (!(event.getEntity() instanceof Zombie) && !(event.getEntity() instanceof Skeleton))
			return;

		// Only spawn for entities on the surface
		Location l = event.getEntity().getLocation();
		Location headBlock = new Location(l.getWorld(), l.getX() + 1, l.getY(), l.getZ());

		if (headBlock != null && headBlock.getBlock().getType() == Material.AIR) {

			int skyLight = headBlock.getBlock().getLightFromSky();
			if (skyLight == 0) {
				return;
			}

		} else {
			return;
		}

		// 30% chance to replace the entity
		if ((r.nextInt(1000 + 0) - 0) > 300) {
			return;
		}

		// Choose between thief and snowman 50% 50% chance
		if ((r.nextInt(1000 + 0) - 0) > 500) {
			// spawn theif
			Thief dirtyThief = new Thief(plugin, event.getEntity().getLocation());
			World world = plugin.getServer().getWorld(event.getEntity().getWorld().getName());
			((CraftWorld) world).getHandle().addFreshEntity(dirtyThief);
		} else { // spawn snowman
			Bob angryBob = new Bob(plugin, event.getEntity().getLocation(), false, false);
			World world = plugin.getServer().getWorld(event.getEntity().getWorld().getName());
			((CraftWorld) world).getHandle().addFreshEntity(angryBob);
		}

		// Remove the originally spawned entity
		event.setCancelled(true);

	}

}
