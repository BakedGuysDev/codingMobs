package com.egirlsnation.codingMobs;

import net.minecraft.world.entity.projectile.EntityProjectileThrowable;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.ParticleParamItem;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;

public class FireSnowball extends EntitySnowball {

	public FireSnowball(EntityTypes<? extends EntitySnowball> var0, World var1) {
		super(var0, var1);

		this.setOnFire(80);
	}

	public FireSnowball(World var0, double var1, double var3, double var5) {
		super(var0, var1, var3, var5);

		this.setOnFire(80);
	}

	public FireSnowball(World var0, EntityLiving var1) {
		super(var0, var1);

		this.setOnFire(80);
	}

	// onHitEntity
	protected void a(MovingObjectPositionEntity var0) {
		super.a(var0);
		Entity var1 = var0.getEntity();
		int var2 = (var1 instanceof net.minecraft.world.entity.monster.EntityBlaze) ? 3 : 0;
		var1.damageEntity(DamageSource.projectile(this, getShooter()), var2);
		// Apply damage to player kek
		if (var1 instanceof EntityHuman) {
			var1.damageEntity(DamageSource.projectile(this, var1), 5);
			// Add some particles and fire for fun
			Player player = (Player) var1.getBukkitEntity();
			spawnParticles(player, 50);
		}
	}

	private void spawnParticles(Player player, int amount) {

		player.setFireTicks(30);
		for (int i = 0; i < amount; i++) {
			player.spawnParticle(Particle.FLAME, player.getLocation().add(generateRandomCoords(2, -2),
					generateRandomCoords(4, 0), generateRandomCoords(2, -2)), 0);
		}

	}

	private double generateRandomCoords(double maxRange, double offset) {

		double value = offset + Math.random() * (maxRange - offset);

		return value;

	}

	// onHit()
	protected void a(MovingObjectPosition var0) {
		super.a(var0);
		if (!this.t.y) {
			this.t.broadcastEntityEffect(this, (byte) 3);
			die();
		}
	}

	private ParticleParam n() {
		ItemStack var0 = getItem();
		return var0.isEmpty() ? (ParticleParam) Particles.A : (ParticleParam) new ParticleParamItem(Particles.K, var0);
	}

	public void a(byte var0) {
		if (var0 == 3) {
			ParticleParam var1 = n();
			for (int var2 = 0; var2 < 8; var2++)
				this.t.addParticle(var1, locX(), locY(), locZ(), 0.0D, 0.0D, 0.0D);
		}
	}

	protected Item getDefaultItem() {
		return Items.oa;
	}
}