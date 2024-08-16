package com.blaxout1213.WeaponPoison.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.bukkit.entity.LivingEntity;

import com.blaxout1213.WeaponPoison.tasks.PoisonedEntityTask;

public class MessageUtil
{
	public static String prepareMessage(String message)
	{
		return message
				.replace("&", "§");
	}
	public static String prepareMessage(String message, LivingEntity le)
	{
		PoisonedEntityTask data = DamageUtil.getWeaponPoisonTask(le);
		return prepareMessage(message, data);
	}
	public static String prepareMessage(String message, PoisonedEntityTask data)
	{
		DecimalFormat dm = new DecimalFormat("###.###");
		dm.setRoundingMode(RoundingMode.DOWN);
		return message
				.replace("%s", String.valueOf(data.getSeverity()))
				.replace("%h", dm.format(data.getHealingMultiplier()*100.00D))
				.replace("%d", String.valueOf(data.recalculateDamage()))
				.replace("%r", String.valueOf(data.getRemainingDamage()))
				.replace("&", "§");
	}
}
