package com.egirlsnation.codingMobs;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory;

import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntitySnowman;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class Bob extends EntitySnowman {

	// DATA_PUMPKIN_ID
	private static final DataWatcherObject<Byte> b = DataWatcher.a(EntitySnowman.class, DataWatcherRegistry.a);
	// PUMPKIN_FLAG
	private static final byte c = 16;
	// EYE_HEIGHT
	private static final float d = 1.7F;

	private boolean waterSensitivity;
	private boolean shouldBurn;
	private boolean pathfindersSet = false;

	public Bob(Main plugin, Location location, boolean water_sensitivity, boolean heat_sensitivity) {
		super(EntityTypes.aF, ((CraftWorld) location.getWorld()).getHandle());
		this.waterSensitivity = water_sensitivity;
		this.shouldBurn = heat_sensitivity;

		// Setup the snowman
		this.setPosition(location.getX(), location.getY(), location.getZ());
		this.setCustomName(new ChatComponentText(ChatColor.RED + "[BUTTHURT] " + ChatColor.GOLD + "Bob"));
		this.setCustomNameVisible(true);

		// Change default health
		AttributeModifiable healthAttribute = this.getAttributeInstance(GenericAttributes.a);
		healthAttribute.setValue(Config.getBobHealth());
		this.setHealth((float) Config.getBobHealth());

	}

	// registerGoals()
	// Setup the targetting goals
	// Increased radius and min,max attack intervals
	@Override
	protected void initPathfinder() {
		this.bP.a(1, (PathfinderGoal) new PathfinderGoalArrowAttack(this, 1.25D, 20, 10.0F));
		this.bP.a(2, (PathfinderGoal) new PathfinderGoalRandomStrollLand(this, 1.0D, 1.0000001E-5F));
		this.bP.a(3, (PathfinderGoal) new PathfinderGoalLookAtPlayer((EntityInsentient) this, EntityHuman.class, 6.0F));
		this.bP.a(4, (PathfinderGoal) new PathfinderGoalRandomLookaround((EntityInsentient) this));
		this.bQ.a(1, (PathfinderGoal) new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
		pathfindersSet = true;
	}

	// createAttributes()
	// w = createMobAttributes()
	// w.a = add()
	// GenericAttributes.a = MAX_HEALTH
	// GenericAttributes.d = MOVEMENT_SPEED
	// GenericAttributes.f = ATTACK_DAMAGE
	public static AttributeProvider.Builder n() {
		return EntityInsentient.w().a(GenericAttributes.a, 100.0D).a(GenericAttributes.d, 0.20000000298023224D)
				.a(GenericAttributes.f, 10.0D);
	}

	// defineSynchedData()
	@Override
	protected void initDatawatcher() {
		super.initDatawatcher();
		this.Y.register(b, Byte.valueOf((byte) 16));
	}

	// addAdditionalSaveData()
	@Override
	public void saveData(NBTTagCompound nbttagcompound) {
		super.saveData(nbttagcompound);
		nbttagcompound.setBoolean("Pumpkin", hasPumpkin());
	}

	// readAdditionalSaveData()
	@Override
	public void loadData(NBTTagCompound nbttagcompound) {
		super.loadData(nbttagcompound);
		if (nbttagcompound.hasKey("Pumpkin"))
			setHasPumpkin(nbttagcompound.getBoolean("Pumpkin"));
	}

	// isSensitiveToWater()
	@Override
	public boolean ex() {
		return waterSensitivity;
	}

	// aiStep()
	@Override
	public void movementTick() {
		super.movementTick();

		// After server restart pathfinders are reset to default this fixes it
		if (!pathfindersSet)
			initPathfinder();

		if (!this.t.y) {
			int i = MathHelper.floor(locX());
			int j = MathHelper.floor(locY());
			int k = MathHelper.floor(locZ());

			if (shouldBurn && this.t.getBiome(new BlockPosition(i, 0, k))
					.getAdjustedTemperature(new BlockPosition(i, j, k)) > 1.0F)
				damageEntity(CraftEventFactory.MELTING, 1.0F);

			if (!this.t.getGameRules().getBoolean(GameRules.c))
				return;

			IBlockData iblockdata = Blocks.cK.getBlockData();
			for (int l = 0; l < 4; l++) {
				i = MathHelper.floor(locX() + ((l % 2 * 2 - 1) * 0.25F));
				j = MathHelper.floor(locY());
				k = MathHelper.floor(locZ() + ((l / 2 % 2 * 2 - 1) * 0.25F));
				BlockPosition blockposition = new BlockPosition(i, j, k);
				if (this.t.getType(blockposition).isAir()
						&& this.t.getBiome(blockposition).getAdjustedTemperature(blockposition) < 0.8F
						&& iblockdata.canPlace((IWorldReader) this.t, blockposition))
					CraftEventFactory.handleBlockFormEvent(this.t, blockposition, iblockdata, (Entity) this);
			}
		}
	}

	// performRangedAttack
	@Override
	public void a(EntityLiving entityliving, float f) {

		FireSnowball entitysnowball = new FireSnowball(this.t, (EntityLiving) this);
		double d0 = entityliving.getHeadY() - 1.100000023841858D;
		double d1 = entityliving.locX() - locX();
		double d2 = d0 - entitysnowball.locY();
		double d3 = entityliving.locZ() - locZ();
		double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.20000000298023224D;
		entitysnowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
		playSound(SoundEffects.sa, 1.0F, 0.4F / (getRandom().nextFloat() * 0.4F + 0.8F));
		this.t.addEntity(entitysnowball);

	}

	// getStandingEyeHeight
	@Override
	protected float b(EntityPose entitypose, EntitySize entitysize) {
		return 1.7F;
	}

	// hasPumpkin()
	public boolean hasPumpkin() {
		return ((((Byte) this.Y.get(b)).byteValue() & 0x10) != 0);
	}

	// setPumpkin()
	public void setHasPumpkin(boolean flag) {
		byte b0 = ((Byte) this.Y.get(b)).byteValue();
		if (flag) {
			this.Y.set(b, Byte.valueOf((byte) (b0 | 0x10)));
		} else {
			this.Y.set(b, Byte.valueOf((byte) (b0 & 0xFFFFFFEF)));
		}
	}

	// getAmbientSound()
	@Nullable
	@Override
	protected SoundEffect getSoundAmbient() {
		return SoundEffects.rX;
	}

	// getHurtSound
	@Nullable
	@Override
	protected SoundEffect getSoundHurt(DamageSource damagesource) {
		return SoundEffects.rZ;
	}

	// getDeathSound()
	@Nullable
	@Override
	protected SoundEffect getSoundDeath() {
		return SoundEffects.rY;
	}

	// getLeshOffset()
	@Override
	public Vec3D cu() {
		return new Vec3D(0.0D, (0.75F * getHeadHeight()), (getWidth() * 0.4F));
	}

}
