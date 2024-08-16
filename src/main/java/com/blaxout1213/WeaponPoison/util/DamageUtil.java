package com.blaxout1213.WeaponPoison.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.blaxout1213.WeaponPoison.WeaponPoison;
import com.blaxout1213.WeaponPoison.metadata.PoisonedEntityTaskMeta;
import com.blaxout1213.WeaponPoison.tasks.PoisonedEntityTask;

public class DamageUtil
{
	public static double getPureDamage(LivingEntity le, double damage)
	{
		if(le.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
		{
			double divisor = 0.2D*(le.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1);
			damage = damage / Math.max(0.2, 1-divisor);
		}
		int protections = 0;
		for(ItemStack is : le.getEquipment().getArmorContents())
		{
			if(is != null && is.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) > 0 )
			{
				protections += is.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
			}
		}
		return damage / (1.0D - Math.min(20, protections)*0.04D);
	}
	public static PoisonedEntityTask getWeaponPoisonTask(Entity e) throws IllegalArgumentException
	{
		if(e.hasMetadata(WeaponPoison.WEAPONPOISONED_METADATA))
		{
			PoisonedEntityTaskMeta poisonTaskMeta = (PoisonedEntityTaskMeta) e.getMetadata(WeaponPoison.WEAPONPOISONED_METADATA).get(0);
			return poisonTaskMeta.getPoisonedEntityTask();
		}
		throw new IllegalArgumentException(e.getName() + " did not have any weapon poison task running");
	}
}
