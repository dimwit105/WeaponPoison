package com.blaxout1213.WeaponPoison.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.blaxout1213.WeaponPoison.metadata.PoisonedEntityDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.blaxout1213.WeaponPoison.WeaponPoison;
import com.blaxout1213.WeaponPoison.tasks.PoisonedEntityTask;

public class SavePlayerDataUtil
{
	public static final NamespacedKey POISONED_ENTITY = new NamespacedKey(WeaponPoison.PLUGIN, "poisoned_entity");
	public static void saveDisconnector(LivingEntity p, PoisonedEntityTask task)
	{
		p.getPersistentDataContainer().set(POISONED_ENTITY, new PoisonedEntityDataType(), task);
	}

}
