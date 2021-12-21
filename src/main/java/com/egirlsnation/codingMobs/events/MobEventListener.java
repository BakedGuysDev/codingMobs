package com.egirlsnation.codingMobs.events;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftCreature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowball;
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
import com.egirlsnation.codingMobs.Config;
import com.egirlsnation.codingMobs.LogFormatter;
import com.egirlsnation.codingMobs.Main;
import com.egirlsnation.codingMobs.Thief;

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

			if (!Config.isThiefDropEnabled())
				return;

			// Drop gold nugget from villager
			Random r = new Random();

			// Chance for thief to drop a gold nugget
			if ((r.nextInt(1000 + 0) - 0) > (Config.getSpawnChance() * 10)) {
				return;
			}

			if (Config.isDebugging())
				log.info(LogFormatter.format(LogFormatter.priority.HIGH, "Damage Event",
						"Damage Event has been caught."));

			event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
					new ItemStack(Material.GOLD_NUGGET));

			if (Config.isDebugging())
				log.info(LogFormatter.format(LogFormatter.priority.LOW, "Thief Drop", "Thief dropped a gold_nugget."));

		}

		// Check if custom villager attacked player
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Villager
				&& event.getDamager().getCustomName() != null) {

			try {
				((Thief) (((CraftCreature) event.getDamager()).getHandle())).setAttacked(true);
				((Thief) (((CraftCreature) event.getDamager()).getHandle())).stealItems((Player) event.getEntity());

				if (Config.isDebugging()) {
					log.info(LogFormatter.format(LogFormatter.priority.HIGH, "Damage Event",
							"Damage event has been caught."));
					log.info(LogFormatter.format(LogFormatter.priority.LOW, "Thief Attack",
							"Thief stole items of player: " + event.getEntity().getName() + "."));
				}

				if (Config.isThiefMessageEnabled()) {

					((Player) (event.getEntity())).sendMessage(
							Config.getWelcomeMessageHeaderColor() + "[" + Config.getMessage("welcome-message-header")
									+ "] " + Config.getThiefMessageColor() + Config.getMessage("thief-message"));

				}

			} catch (Exception ex) {
				plugin.log.warning("Custom mob doesn't belong to this runtime.");
			}

			((Player) event.getEntity()).damage(Config.getThiefDamage());
			if (Config.isDebugging())
				log.info(LogFormatter.format(LogFormatter.priority.LOW, "Thief Attack", "Thief attacked player: "
						+ event.getEntity().getName() + " and dealt a damage of: " + Config.getThiefDamage() + "."));

			event.setCancelled(true);

		}

		// Check if snowman attacked player
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Snowball
				&& event.getDamager().getFireTicks() > 0) {

			if (Config.isDebugging()) {
				log.info(LogFormatter.format(LogFormatter.priority.HIGH, "Damage Event",
						"Damage event has been caught."));
				log.info(LogFormatter.format(LogFormatter.priority.LOW, "Bob Attack",
						"Bob attacked player: " + event.getEntity().getName() + "."));
			}

			((Player) event.getEntity()).damage(Config.getBobDamage(),
					(Entity) ((Snowball) event.getDamager()).getShooter());
			if (Config.isDebugging())
				log.info(LogFormatter.format(LogFormatter.priority.LOW, "Bob Attack", "Bob attacked player: "
						+ event.getEntity().getName() + " and dealt a damage of: " + Config.getBobDamage() + "."));

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

			if (Config.isDebugging()) {

				log.info(
						LogFormatter.format(LogFormatter.priority.HIGH, "Death Event", "Death event has been caught."));

				List<ItemStack> list = ((Thief) ((CraftCreature) event.getEntity()).getHandle()).getStolenItems();
				if (list != null && list.size() > 0) {
					log.info(LogFormatter.format(LogFormatter.priority.LOW, "Entity Death",
							"Player: " + event.getEntity().getName() + " Killed Thief and got the items back."));
				} else {
					log.info(LogFormatter.format(LogFormatter.priority.LOW, "Entity Death",
							"Player: " + event.getEntity().getName() + " Killed Thief."));
				}

			}

		} catch (Exception ex) {
			log.info(LogFormatter.format(LogFormatter.priority.HIGH, "Death Event", "Death event has been caught."));
			log.info(LogFormatter.format(LogFormatter.priority.LOW, "Entity Death",
					"Player: " + event.getEntity().getName() + " Killed Thief but villager had no items to drop."));
		}

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Random r = new Random();

		if (!(event.getEntity() instanceof Zombie) && !(event.getEntity() instanceof Skeleton))
			return;

		// Only spawn when enabled
		if (!Config.isSpawnEnabled())
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

		// Chance to replace the entity
		if ((r.nextInt(1000 + 0) - 0) > (Config.getSpawnChance() * 10)) {
			return;
		}

		if (Config.isDebugging())
			log.info(LogFormatter.format(LogFormatter.priority.HIGH, "Spawn Event", "Spawn event has been caught."));

		// Choose between thief and snowman 50% 50% chance
		if ((r.nextInt(1000 + 0) - 0) > 500) {
			// spawn theif
			Thief dirtyThief = new Thief(plugin, event.getEntity().getLocation());
			World world = plugin.getServer().getWorld(event.getEntity().getWorld().getName());
			((CraftWorld) world).getHandle().addEntity(dirtyThief);
			if (Config.isDebugging())
				log.info(LogFormatter.format(LogFormatter.priority.LOW, "Entity Spawn",
						"Thief has spawned at: X: " + event.getEntity().getLocation().getX() + " Y: "
								+ event.getEntity().getLocation().getY() + " Z: "
								+ event.getEntity().getLocation().getZ() + "."));
		} else { // spawn snowman
			Bob angryBob = new Bob(plugin, event.getEntity().getLocation(), false, false);
			World world = plugin.getServer().getWorld(event.getEntity().getWorld().getName());
			((CraftWorld) world).getHandle().addEntity(angryBob);
			if (Config.isDebugging())
				log.info(LogFormatter.format(LogFormatter.priority.LOW, "Entity Spawn",
						"Bob has spawned at: X: " + event.getEntity().getLocation().getX() + " Y: "
								+ event.getEntity().getLocation().getY() + " Z: "
								+ event.getEntity().getLocation().getZ() + "."));
		}

		// Remove the originally spawned entity
		event.setCancelled(true);

	}

}
