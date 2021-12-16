package com.egirlsnation.codingMobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.lang.reflect.Field;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftVillager;
import org.bukkit.inventory.ItemStack;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Thief extends Villager {

	private static Field attributeField;

	private List<ItemStack> stolen_items = new ArrayList<>();
	private boolean attacked = false;
	private boolean attacking = false;
	private boolean avoiding = false;

	public Thief(Main plugin, Location location) {
		super(EntityType.VILLAGER, (Level) ((CraftWorld) location.getWorld()).getHandle());

		// Setup the villager
		this.setPos(location.getX(), location.getY(), location.getZ());
		this.setCustomName(new TextComponent(ChatColor.RED + "[JEW] " + ChatColor.GOLD + "Thief"));
		this.setCustomNameVisible(true);

		// Attempt to give the entity attack damage
		try {
			registerGenericAttribute(this.getBukkitEntity(), Attributes.ATTACK_DAMAGE);
			registerGenericAttribute(this.getBukkitEntity(), Attributes.FOLLOW_RANGE);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Change default health
		AttributeInstance healthAttribute = this.getAttribute(Attributes.MAX_HEALTH);
		healthAttribute.setBaseValue(100.0D);
		this.setHealth(100);

	}

	static {
		try {
			attributeField = AttributeMap.class.getDeclaredField("b");
			attributeField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	private void registerGenericAttribute(org.bukkit.entity.Entity entity, Attribute attribute)
			throws IllegalAccessException {
		AttributeMap attributeMapBase = ((CraftLivingEntity) entity).getHandle().getAttributes();
		Map<Attribute, AttributeInstance> map = (Map<Attribute, AttributeInstance>) attributeField
				.get(attributeMapBase);
		Attribute attributeBase = attribute;
		AttributeInstance attributeModifiable = new AttributeInstance(attributeBase, AttributeInstance::getAttribute);
		attributeModifiable.setBaseValue(10.0D);
		map.put(attributeBase, attributeModifiable);
	}

	@Override
	public void tick() {
		super.tick();

		CraftVillager entity = (CraftVillager) this.getBukkitEntity();
		if (!attacked && entity.getEquipment().getItemInMainHand() != new ItemStack(Material.GOLDEN_SWORD)) {
			giveSword();
		} else if (avoiding && entity.getEquipment().getItemInMainHand() != null) {
			entity.getEquipment().clear();
		}

		if (attacked && !avoiding) {
			clearGoalSelectors();
			// Goal selectors
			this.goalSelector.addGoal(0, (Goal) new AvoidEntityGoal<Player>(this, Player.class, 10, 0.8D, 0.8D));
			this.goalSelector.addGoal(1, (Goal) new PanicGoal(this, 1.5D));
			this.goalSelector.addGoal(2, (Goal) new RandomStrollGoal(this, 0.6D));
			this.goalSelector.addGoal(3, (Goal) new RandomLookAroundGoal(this));
			avoiding = true;
		} else if (!attacked && !attacking) {
			clearGoalSelectors();
			// Target selector
			this.targetSelector.addGoal(0, (Goal) new MeleeAttackGoal(this, 0.8D, false));
			// Goal selector
			this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<Player>(this, Player.class, true));
			attacking = true;
		}

	}

	// fucking villagers cant hold swords :(
	public void giveSword() {
		CraftVillager entity = (CraftVillager) this.getBukkitEntity();
		entity.getEquipment().clear();
		entity.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));
	}

	public void stealItems(org.bukkit.entity.Player player) {

		// Steal all items from player except for sword
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);

			if (item == null || item.getType() == Material.AIR)
				continue;

			if (item.getType() != Material.NETHERITE_SWORD && item.getType() != Material.DIAMOND_SWORD
					&& item.getType() != Material.IRON_SWORD && item.getType() != Material.STONE_SWORD
					&& item.getType() != Material.WOODEN_SWORD) {
				stolen_items.add(item);
				player.getInventory().setItem(i, new ItemStack(Material.AIR));
			}
		}

	}

	private void clearGoalSelectors() {

		this.goalSelector.removeAllGoals();
		this.targetSelector.removeAllGoals();

	}

	public void setAttacked(boolean val) {
		this.attacked = val;
	}

	public List<ItemStack> getStolenItems() {

		return this.stolen_items;

	}

}
