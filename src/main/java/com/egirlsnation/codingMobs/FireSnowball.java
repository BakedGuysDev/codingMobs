package com.egirlsnation.codingMobs;

import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class FireSnowball extends Snowball {

	public FireSnowball(Level var0, LivingEntity var1) {
		super(var0, var1);

		this.setSecondsOnFire(10);
	}

	@Override
	protected void onHitEntity(EntityHitResult var0) {
		super.onHitEntity(var0);
		Entity var1 = var0.getEntity();
		// Apply damage to player kek
		if (var1 instanceof Player) {
			var1.hurt(DamageSource.thrown(this, var1), 5);
			// Add some particles and fire for fun
			spawnParticles((CraftPlayer) var1.getBukkitEntity(), 50);
		}
	}

	private void spawnParticles(org.bukkit.entity.Player player, int amount) {

		player.setFireTicks(35);
		for (int i = 0; i < amount; i++) {
			player.spawnParticle(Particle.FLAME, player.getLocation().add(generateRandomCoords(2, -2),
					generateRandomCoords(4, 0), generateRandomCoords(2, -2)), 0);
		}

	}

	private double generateRandomCoords(double maxRange, double offset) {

		double value = offset + Math.random() * (maxRange - offset);

		return value;

	}

}