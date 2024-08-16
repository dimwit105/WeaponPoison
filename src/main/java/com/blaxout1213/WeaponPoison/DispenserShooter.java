package com.blaxout1213.WeaponPoison;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.projectiles.BlockProjectileSource;

import com.blaxout1213.WeaponPoison.metadata.ItemPoisonData;
import com.blaxout1213.WeaponPoison.metadata.ItemPoisonedDataType;
import com.blaxout1213.WeaponPoison.metadata.PoisonCausingEntity;
import com.blaxout1213.WeaponPoison.metadata.PoisonCausingEntityMeta;

public class DispenserShooter implements Listener
{
	private HashMap<Block, ItemStack> dispensed = new HashMap<Block, ItemStack>();
	DispenserShooter()
	{
		WeaponPoison.PLUGIN.getServer().getPluginManager().registerEvents(this, WeaponPoison.PLUGIN);
	}
	
	@EventHandler
	public void dispenserEvent(BlockDispenseEvent event)
	{
		ItemStack is = event.getItem();
		if(is != null && is.getType() != Material.AIR) 
		{
			dispensed.put(event.getBlock(), is);
		}
	}
	
	@EventHandler
	public void dispenserLaunchEvent(ProjectileLaunchEvent event)
	{
		if(event.getEntity().getShooter() instanceof BlockProjectileSource)
		{
			BlockProjectileSource bps = (BlockProjectileSource) event.getEntity().getShooter();
			if(dispensed.get(bps.getBlock()) != null)
			{
				ItemStack is = dispensed.remove(bps.getBlock());
				if(is != null && is.getType() != Material.AIR) 
				{
					PersistentDataContainer container = is.getItemMeta().getPersistentDataContainer();
					if(container.has(WeaponPoison.POISONED_ITEM_KEY))
					{
						ItemPoisonData ipd = container.get(WeaponPoison.POISONED_ITEM_KEY, new ItemPoisonedDataType());
						PoisonCausingEntityMeta pcem = new PoisonCausingEntityMeta(WeaponPoison.PLUGIN, new PoisonCausingEntity(ipd.getSeverity(), ipd.getVenom()));
						event.getEntity().setMetadata(WeaponPoison.POISONABLE_METADATA, pcem);
					}
				}
			}
		}
	}
}
