package com.blaxout1213.WeaponPoison;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityExhaustionEvent.ExhaustionReason;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.blaxout1213.WeaponPoison.metadata.ItemPoisonData;
import com.blaxout1213.WeaponPoison.metadata.ItemPoisonedDataType;
import com.blaxout1213.WeaponPoison.metadata.PoisonCausingEntity;
import com.blaxout1213.WeaponPoison.metadata.PoisonCausingEntityMeta;
import com.blaxout1213.WeaponPoison.metadata.PoisonedEntityTaskMeta;
import com.blaxout1213.WeaponPoison.tasks.PoisonedEntityTask;
import com.blaxout1213.WeaponPoison.tasks.PoisonedEntityTask.ClearReason;
import com.blaxout1213.WeaponPoison.util.DamageUtil;
import com.blaxout1213.WeaponPoison.util.EntityNotFoundException;
import com.blaxout1213.WeaponPoison.util.MessageUtil;
import com.blaxout1213.WeaponPoison.util.SavePlayerDataUtil;

public class EventListener implements Listener
{
	EventListener()
	{
		new VillagerEvents();
		new DispenserShooter();
		WeaponPoison.PLUGIN.getServer().getPluginManager().registerEvents(this, WeaponPoison.PLUGIN);
	}
	@EventHandler
	public void onEntityAttack(EntityDamageByEntityEvent event)
	{
		if(event.getEntity() instanceof LivingEntity)
		{
			LivingEntity defender = (LivingEntity) event.getEntity();
			if(event.getDamager() instanceof LivingEntity)
			{
				LivingEntity attacker = (LivingEntity) event.getDamager();
				ItemStack is = attacker.getEquipment().getItemInMainHand();
				if(is != null && is.getType() != Material.AIR) 
				{
					PersistentDataContainer container = is.getItemMeta().getPersistentDataContainer();
					if(container.has(WeaponPoison.POISONED_ITEM_KEY))
					{
						ItemPoisonData ipd = container.get(WeaponPoison.POISONED_ITEM_KEY, new ItemPoisonedDataType());
						applyPoison(defender, ipd.getSeverity(), ipd.getVenom());
					}
				}
			}
			if(event.getDamager().hasMetadata(WeaponPoison.POISONABLE_METADATA))
			{
				PoisonCausingEntityMeta pcem = (PoisonCausingEntityMeta) event.getDamager().getMetadata(WeaponPoison.POISONABLE_METADATA).get(0);
				applyPoison(defender, pcem.getPoisonCausingEntity().severity, pcem.getPoisonCausingEntity().venom);
			}
		}
	}
	@EventHandler
	public void onEntityHeal(EntityRegainHealthEvent event)
	{
		if(event.getEntity().hasMetadata(WeaponPoison.WEAPONPOISONED_METADATA))
		{
			event.setAmount(DamageUtil.getWeaponPoisonTask(event.getEntity()).getHealingMultiplier()*event.getAmount());
		}
	}
	
	@EventHandler
	public void exhaustion(EntityExhaustionEvent event)
	{
		HumanEntity ent = event.getEntity();
		if(ent instanceof Player)
		{
			if(event.getExhaustionReason() == ExhaustionReason.REGEN && ent.hasMetadata(WeaponPoison.WEAPONPOISONED_METADATA))
			{
				event.setExhaustion((float) (DamageUtil.getWeaponPoisonTask(ent).getHealingMultiplier()*event.getExhaustion()));
			}
		}
	}
	@EventHandler
	public void onDeath(EntityDeathEvent event)
	{
		if(event.getEntity().hasMetadata(WeaponPoison.WEAPONPOISONED_METADATA))
		{
			PoisonedEntityTask data = DamageUtil.getWeaponPoisonTask(event.getEntity());
			data.clearPoison(ClearReason.DEATH);
			if(event instanceof PlayerDeathEvent && data.isDeathTick())
			{
				PlayerDeathEvent pevent = (PlayerDeathEvent) event;
				String s = data.isVenom() ? "Venom" : "Poison";
				pevent.setDeathMessage(MessageUtil.prepareMessage(WeaponPoison.messages.getString("Broadcasts.Death." + s)).replace("%p", pevent.getEntity().getName()));
			}
		}
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event)
	{
		Player p = event.getPlayer();
		try
		{
			WeaponPoison.PLUGIN.getLogger().log(Level.INFO, p.getName() + " left");
			if(p.hasMetadata(WeaponPoison.WEAPONPOISONED_METADATA))
			{
				SavePlayerDataUtil.saveDisconnector(p);
			}	
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		/*
		NBTEntity nbte = new NBTEntity(p);
		if(p.hasMetadata(WeaponPoison.WEAPONPOISONED_METADATA))
		{
			PoisonedEntityTaskMeta poisonTaskMeta = (PoisonedEntityTaskMeta) p.getMetadata(WeaponPoison.WEAPONPOISONED_METADATA).get(0);
			nbte.getPersistentDataContainer().setInteger(WeaponPoison.WEAPONPOISONNBT_SEVERITY, poisonTaskMeta.getPoisonedEntityTask().getSeverity());
			nbte.getPersistentDataContainer().setInteger(WeaponPoison.WEAPONPOISONNBT_DAMAGE_TIMER, poisonTaskMeta.getPoisonedEntityTask().getDamageTimer());
			//NBT.modifyPersistentData(p, nbt -> {
				//nbt.setInteger(WeaponPoison.WEAPONPOISONNBT_SEVERITY, poisonTaskMeta.getPoisonedEntityTask().getSeverity());
				//nbt.setInteger(WeaponPoison.WEAPONPOISONNBT_DAMAGE_TIMER, poisonTaskMeta.getPoisonedEntityTask().getDamageTimer());
			//});
			poisonTaskMeta.getPoisonedEntityTask().clearPoison();
		}
		*/
	}
	
	@EventHandler
	public void onPlayerConnect(PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		try
		{
			int compensation = 4;
			PoisonedEntityTask task = SavePlayerDataUtil.readDisconnector(p, compensation);
			PoisonedEntityTaskMeta poisonTaskMeta = new PoisonedEntityTaskMeta(WeaponPoison.PLUGIN, task);
			
			WeaponPoison.PLUGIN.getFoliaLib().getImpl().runAtEntityTimer(p, task, compensation*20, 20);
			p.setMetadata(WeaponPoison.WEAPONPOISONED_METADATA, poisonTaskMeta);
			
		}
		catch(EntityNotFoundException e)
		{
			WeaponPoison.PLUGIN.getLogger().log(Level.WARNING, "Somehow, Bukkit did not find the entity for " + p.getName() + ", despite having data saved for them.");
		}
		catch(Exception e)
		{
			WeaponPoison.PLUGIN.getLogger().log(Level.INFO, "Exception loading " + p.getName() + "'s data. Probably just didnt have any");
		}
	}
	
	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent event)
	{
		Player p = event.getPlayer();
		if(p.hasMetadata(WeaponPoison.WEAPONPOISONED_METADATA) && event.getItem().getType() == Material.MILK_BUCKET)
		{
			DamageUtil.getWeaponPoisonTask(p).applyAntidote();
		}
	}
	@EventHandler
	public void onPotionEffect(EntityPotionEffectEvent event)
	{
		if(event.getEntity() instanceof LivingEntity && event.getNewEffect() != null && event.getNewEffect().getType() == PotionEffectType.POISON && event.getAction() == Action.ADDED)
		{
			PotionEffect pe = event.getNewEffect();
			LivingEntity le = (LivingEntity) event.getEntity();
			int severity = (int) Math.floor((pe.getDuration() / 20.0D)*(pe.getAmplifier()+1));
			if(le.getCategory() != EntityCategory.UNDEAD)
			{
				applyPoison(le, severity, false);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBowFired(EntityShootBowEvent event)
	{
		ItemStack is = event.getConsumable();
		if(is != null && is.getType() != Material.AIR) 
		{
			PersistentDataContainer container = is.getItemMeta().getPersistentDataContainer();
			if(container.has(WeaponPoison.POISONED_ITEM_KEY))
			{
				ItemPoisonData ipd = container.get(WeaponPoison.POISONED_ITEM_KEY, new ItemPoisonedDataType());
				Entity e = event.getProjectile();
				PoisonCausingEntityMeta pcem = new PoisonCausingEntityMeta(WeaponPoison.PLUGIN, new PoisonCausingEntity(ipd.getSeverity(), ipd.getVenom()));
				e.setMetadata(WeaponPoison.POISONABLE_METADATA, pcem);
			}
		}
	}
	

	@EventHandler
	public void projectileLaunchEvent(ProjectileLaunchEvent event)
	{
		if(event.getEntity().getShooter() instanceof LivingEntity)
		{
			LivingEntity p = (LivingEntity) event.getEntity().getShooter();
			if(p.hasMetadata(WeaponPoison.POISONABLE_METADATA))
			{
					PoisonCausingEntityMeta pcem = (PoisonCausingEntityMeta) p.getMetadata(WeaponPoison.POISONABLE_METADATA).get(0);
					event.getEntity().setMetadata(WeaponPoison.POISONABLE_METADATA, pcem);
			}
			else
			{
				switch(event.getEntity().getType())
				{
					case TRIDENT:
						checkForItemToPoison(event.getEntity(), p, Material.TRIDENT);
						break;
					case SNOWBALL:
						checkForItemToPoison(event.getEntity(), p, Material.SNOWBALL);
						break;
					case EGG:
						checkForItemToPoison(event.getEntity(), p, Material.EGG);
						break;
					default:
						break;
				}
			}
		}
	}
	void checkForItemToPoison(Entity e, LivingEntity p, Material m)
	{
		ArrayList<ItemStack> hands = new ArrayList<ItemStack>();
		hands.add(p.getEquipment().getItemInMainHand());
		hands.add(p.getEquipment().getItemInOffHand());
		for(ItemStack is : hands)
		{
			if(is != null && is.getType().equals(m))
			{
				PersistentDataContainer container = is.getItemMeta().getPersistentDataContainer();
				if(container.has(WeaponPoison.POISONED_ITEM_KEY))
				{
					ItemPoisonData ipd = container.get(WeaponPoison.POISONED_ITEM_KEY, new ItemPoisonedDataType());
					PoisonCausingEntityMeta pcem = new PoisonCausingEntityMeta(WeaponPoison.PLUGIN, new PoisonCausingEntity(ipd.getSeverity(), ipd.getVenom()));
					e.setMetadata(WeaponPoison.POISONABLE_METADATA, pcem);
				}
				break;
			}
		}
	}
	void applyPoison(LivingEntity le, int severity, boolean venom)
	{
		if(le.hasMetadata(WeaponPoison.POISONABLE_METADATA)) { return; }
		if(le.hasMetadata(WeaponPoison.WEAPONPOISONED_METADATA))
		{
			PoisonedEntityTask data = DamageUtil.getWeaponPoisonTask(le);
			if(!data.isVenom() && venom && le instanceof Player)
			{
				Player p = (Player)le;
				p.sendMessage(MessageUtil.prepareMessage(WeaponPoison.messages.getString("Accouncements.Messages.Venom.Upgrade"), data));
			}
			data.setSeverity(severity, venom);
		}
		else
		{
			PoisonedEntityTask poisonTask = new PoisonedEntityTask(le, severity, venom);
			PoisonedEntityTaskMeta poisonTaskMeta = new PoisonedEntityTaskMeta(WeaponPoison.PLUGIN, poisonTask);
			
			WeaponPoison.PLUGIN.getFoliaLib().getImpl().runAtEntityTimer(le, poisonTask, 0, 20);
			le.setMetadata(WeaponPoison.WEAPONPOISONED_METADATA, poisonTaskMeta);
			if(le instanceof Player)
			{
				String s = "Accouncements.Messages.";
				if(venom) { s = s + "Venom.Inflicted";} else {s = s + "Poison.Inflicted";}
				Player p = (Player) le;
				p.sendMessage(MessageUtil.prepareMessage(WeaponPoison.messages.getString(s), poisonTask));
			}
		}
	}
}
