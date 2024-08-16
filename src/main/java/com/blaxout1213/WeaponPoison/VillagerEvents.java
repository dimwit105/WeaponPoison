package com.blaxout1213.WeaponPoison;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import com.blaxout1213.WeaponPoison.util.ItemUtil;
import com.blaxout1213.WeaponPoison.util.TradableItem;

public class VillagerEvents implements Listener
{
	VillagerEvents()
	{
		WeaponPoison.PLUGIN.getServer().getPluginManager().registerEvents(this, WeaponPoison.PLUGIN);
	}
	
	@EventHandler
	public void onVillagerLevel(VillagerAcquireTradeEvent event)
	{
		if(!(event.getEntity() instanceof Villager)) {return;}
		Villager villager = (Villager) event.getEntity();
		if(villager.getProfession() == Profession.FLETCHER && event.getRecipe().getResult().getType().equals(Material.TRIPWIRE_HOOK))
		{
			ItemStack poisonedArrows = ItemUtil.poisonThis(Material.ARROW, 15, 8);
			event.setRecipe(newTrade(poisonedArrows, 1, 8, 2));
		}
		
	}
	
	@EventHandler
	public void onVillagerSpawn(CreatureSpawnEvent event)
	{
		if(event.getEntity() instanceof WanderingTrader)
		{
			WanderingTrader wt = (WanderingTrader) event.getEntity();
			ArrayList<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>(wt.getRecipes());
			switch(WeaponPoison.RAND.nextInt(1))
			{
				case 0:
					TradableItem ti = randomWeapon();
					recipes.remove(WeaponPoison.RAND.nextInt(recipes.size()));
					recipes.add(WeaponPoison.RAND.nextInt(recipes.size()), newTrade(ItemUtil.poisonThis(ti.getMaterial(), 20 + WeaponPoison.RAND.nextInt(3)*5, 1), ti.getCost(), 1, 12));
					wt.setRecipes(recipes);
					break;
				
			}

		}
	}
	
	public static MerchantRecipe newTrade(ItemStack result, int cost, int maxUses, int villXP)
	{
		MerchantRecipe recipe = new MerchantRecipe(result, 0, maxUses, true, villXP, 0.2F);
		ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
		ItemStack emeralds = new ItemStack(Material.EMERALD);
		emeralds.setAmount(cost);
		ingredients.add(emeralds);
		recipe.setIngredients(ingredients);
		return recipe;
	}
	
	public static TradableItem randomWeapon()
	{
		Map<String, Object> options = WeaponPoison.PLUGIN.getConfig().getConfigurationSection("Trades.Weapons").getValues(false);//(HashMap<String, Integer>) WeaponPoison.PLUGIN.getConfig().get("Trades.Weapons");
		ArrayList<String> keys = new ArrayList<>(options.keySet());
		String selected = keys.get(WeaponPoison.RAND.nextInt(keys.size()));
		return new TradableItem(Material.valueOf(selected), (int)options.get(selected));
	}
}
