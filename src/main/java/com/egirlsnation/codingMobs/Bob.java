package com.egirlsnation.codingMobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.event.CraftEventFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Bob extends SnowGolem {

	private boolean waterSensitivity;
	private boolean shouldBurn;
	private boolean pathfindersSet = false;

	public Bob(Main plugin, Location location, boolean water_sensitivity, boolean heat_sensitivity) {
		super(EntityType.SNOW_GOLEM, ((CraftWorld) location.getWorld()).getHandle());
		this.waterSensitivity = water_sensitivity;
		this.shouldBurn = heat_sensitivity;

		// Setup the snowman
		this.setPos(location.getX(), location.getY(), location.getZ());
		this.setCustomName(new TextComponent(ChatColor.RED + "[BUTTHURT] " + ChatColor.GOLD + "Bob"));
		this.setCustomNameVisible(true);

		// Change default health
		AttributeInstance healthAttribute = this.getAttribute(Attributes.MAX_HEALTH);
		healthAttribute.setBaseValue(100.0D);
		this.setHealth(100);

	}

	// Setup the targetting goals
	// Increased radius and min,max attack intervals
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, (Goal) new RangedAttackGoal(this, 1.25D, 20, 10.0F));
		this.goalSelector.addGoal(2, (Goal) new WaterAvoidingRandomStrollGoal(this, 1.0D, 1.0000001E-5F));
		this.goalSelector.addGoal(3, (Goal) new LookAtPlayerGoal((Mob) this, Player.class, 6.0F));
		this.goalSelector.addGoal(4, (Goal) new RandomLookAroundGoal((Mob) this));
		this.targetSelector.addGoal(1, (Goal) new NearestAttackableTargetGoal<Player>((Mob) this, Player.class, 10,
				true, false, entityliving -> entityliving instanceof Player));
		pathfindersSet = true;
	}

	@Override
	public boolean isSensitiveToWater() {
		return waterSensitivity;
	}

	// aiStep()
	@Override
	public void aiStep() {
		super.aiStep();

		// After server restart pathfinders are reset to default this fixes it
		if (!pathfindersSet)
			registerGoals();

		if (!this.level.isClientSide) {
			int i = Mth.floor(getX());
			int j = Mth.floor(getY());
			int k = Mth.floor(getZ());

			BlockPos blockposition = new BlockPos(i, j, k);
			Biome biomebase = this.level.getBiome(blockposition);
			if (shouldBurn && biomebase.shouldSnowGolemBurn(blockposition))
				hurt(CraftEventFactory.MELTING, 1.0F);

			if (!this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
				return;

			BlockState iblockdata = Blocks.SNOW.defaultBlockState();
			for (int l = 0; l < 4; l++) {
				i = Mth.floor(getX() + ((l % 2 * 2 - 1) * 0.25F));
				j = Mth.floor(getY());
				k = Mth.floor(getZ() + ((l / 2 % 2 * 2 - 1) * 0.25F));
				BlockPos blockposition1 = new BlockPos(i, j, k);
				if (this.level.getBlockState(blockposition1).isAir()
						&& iblockdata.canSurvive((LevelReader) this.level, blockposition1))
					CraftEventFactory.handleBlockFormEvent(this.level, blockposition1, iblockdata, (Entity) this);
			}

		}
	}

	// performRangedAttack
	@Override
	public void performRangedAttack(LivingEntity entityliving, float f) {

		FireSnowball entitysnowball = new FireSnowball(this.level, (LivingEntity) this);
		double d0 = entityliving.getEyeY() - 1.100000023841858D;
		double d1 = entityliving.getX() - getX();
		double d2 = d0 - entitysnowball.getY();
		double d3 = entityliving.getZ() - getZ();
		double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.20000000298023224D;
		entitysnowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
		playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (getRandom().nextFloat() * 0.4F + 0.8F));
		this.level.addFreshEntity((Entity) entitysnowball);

	}

}
