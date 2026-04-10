package com.blaxout1213.WeaponPoison.util;

import com.blaxout1213.WeaponPoison.WeaponPoison;
import com.blaxout1213.WeaponPoison.metadata.ItemPoisonData;
import com.blaxout1213.WeaponPoison.tasks.PoisonedEntityTask;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class PoisonedEntitiesManager
{
    private static final ConcurrentHashMap<LivingEntity, PoisonedEntityTask> poisonedEntities = new ConcurrentHashMap<>();
    public static void add(LivingEntity le, ItemPoisonData ipd)
    {
        if(poisonedEntities.containsKey(le))
        {
            PoisonedEntityTask pet = poisonedEntities.get(le);
            pet.setSeverity(ipd.getSeverity(), ipd.getVenom());
        }
        else
        {
            PoisonedEntityTask pet = new PoisonedEntityTask(le, ipd.getSeverity(), ipd.getVenom());
            poisonedEntities.put(le, pet);
        }
    }
    public static boolean has(LivingEntity le)
    {
        return poisonedEntities.containsKey(le);
    }
    public static PoisonedEntityTask get(LivingEntity le)
    {
        return poisonedEntities.get(le);
    }
    public static PoisonedEntityTask remove(LivingEntity le)
    {
        var task = poisonedEntities.remove(le);
        if(task != null)
            task.cancel();
        return task;
    }
    public static void violentRemove(LivingEntity le)
    {
        var task = remove(le);
        if(task != null)
            le.damage(task.getRemainingDamage());
    }
    public static void start(LivingEntity le, PoisonedEntityTask task)
    {
        poisonedEntities.put(le, task);
        WeaponPoison.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(le, task, 20,20);
    }
}
