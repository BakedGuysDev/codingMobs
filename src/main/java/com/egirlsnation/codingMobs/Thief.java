package com.egirlsnation.codingMobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftVillager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.attribute.Attribute;

import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;

public class Thief extends EntityVillager {

	private static Field attributeField;

	private List<ItemStack> stolen_items = new ArrayList<>();
	private boolean attacked = false;
	private boolean attacking = false;
	private boolean avoiding = false;

	public Thief(Main plugin, Location location) {
		super(EntityTypes.aV, ((CraftWorld) location.getWorld()).getHandle());
		this.bK = 20;

		// Setup the villager
		this.setPosition(location.getX(), location.getY(), location.getZ());
		this.setCustomName(new ChatComponentText(ChatColor.RED + "[JEW] " + ChatColor.GOLD + "Thief"));
		this.setCustomNameVisible(true);

		// Attempt to give the entity attack damage
		try {
			registerGenericAttribute(this.getBukkitEntity(), Attribute.GENERIC_ATTACK_DAMAGE);
			registerGenericAttribute(this.getBukkitEntity(), Attribute.GENERIC_FOLLOW_RANGE);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Change default health
		AttributeModifiable healthAttribute = this.getAttributeInstance(GenericAttributes.a);
		healthAttribute.setValue(Config.getThiefHealth());
		this.setHealth((float) Config.getThiefHealth());

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
			this.bP.a(0, new PathfinderGoalAvoidTarget<>(this, EntityHuman.class, 10, 0.8D, 0.8D));
			this.bP.a(1, new PathfinderGoalPanic(this, 1.5D));
			this.bP.a(2, new PathfinderGoalRandomStrollLand(this, 0.6D));
			this.bP.a(3, new PathfinderGoalRandomLookaround(this));
			avoiding = true;
		} else if (!attacked && !attacking) {
			clearGoalSelectors();
			// Target selector
			this.bQ.a(0, new PathfinderGoalMeleeAttack(this, 0.8D, false));
			// Goal selector
			this.bP.a(0, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
			attacking = true;
		}

	}

	static {
		try {
			attributeField = AttributeMapBase.class.getDeclaredField("b");
			attributeField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	private void registerGenericAttribute(org.bukkit.entity.Entity entity, Attribute attribute)
			throws IllegalAccessException {
		AttributeMapBase attributeMapBase = ((CraftLivingEntity) entity).getHandle().getAttributeMap();
		Map<AttributeBase, AttributeModifiable> map = (Map<AttributeBase, AttributeModifiable>) attributeField
				.get(attributeMapBase);
		AttributeBase attributeBase = CraftAttributeMap.toMinecraft(attribute);
		AttributeModifiable attributeModifiable = new AttributeModifiable(attributeBase,
				AttributeModifiable::getAttribute);
		attributeModifiable.setValue(10.0D);
		map.put(attributeBase, attributeModifiable);
	}

	// fucking villagers cant hold swords :(
	public void giveSword() {
		CraftVillager entity = (CraftVillager) this.getBukkitEntity();
		entity.getEquipment().clear();
		entity.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));
		this.updateEquipment();
	}

	public void stealItems(Player player) {

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

		this.bP.a();
		this.bQ.a();

	}

	public List<ItemStack> getStolenItems() {
		return stolen_items;
	}

	public void setAttacked(boolean val) {
		this.attacked = val;
	}

}
