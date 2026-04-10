package com.blaxout1213.WeaponPoison.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class DamageUtil
{
	public static double getPureDamage(LivingEntity le, double damage)
	{
		if(le.hasPotionEffect(PotionEffectType.RESISTANCE))
		{
			double divisor = 0.2D*(le.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier() + 1);
			damage = damage / Math.max(0.2, 1-divisor);
		}
		int protections = 0;
		for(ItemStack is : le.getEquipment().getArmorContents())
		{
			if(is != null && is.getEnchantmentLevel(Enchantment.PROTECTION) > 0 )
			{
				protections += is.getEnchantmentLevel(Enchantment.PROTECTION);
			}
		}
		return damage / (1.0D - Math.min(20, protections)*0.04D);
	}
}
