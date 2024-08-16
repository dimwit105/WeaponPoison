package com.blaxout1213.WeaponPoison.util;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import com.blaxout1213.WeaponPoison.WeaponPoison;
import com.blaxout1213.WeaponPoison.metadata.ItemPoisonData;
import com.blaxout1213.WeaponPoison.metadata.ItemPoisonedDataType;

import net.md_5.bungee.api.chat.TranslatableComponent;

public class ItemUtil
{
	public static ItemStack venomThis(Material material, int severity, int amount, boolean venom)
	{
		ItemStack is = new ItemStack(material);
		is.setItemMeta(modifiedMeta(is, severity, venom));
		is.setAmount(amount);
		return is;
	}
	public static ItemStack poisonThis(Material material, int severity, int amount)
	{
		return venomThis(material, severity, amount, false);
	}
	
	public static ItemStack venomThat(ItemStack itemStack, int severity, boolean venom)
	{
		itemStack.setItemMeta(modifiedMeta(itemStack, severity, venom));
		return itemStack;
	}
	public static ItemStack poisonThat(ItemStack itemStack, int severity)
	{
		return venomThat(itemStack, severity, false);
	}
	
	public static ItemMeta modifiedMeta(ItemStack is, int severity, boolean venom)
	{
		ItemMeta im = is.getItemMeta();
		TranslatableComponent item = new TranslatableComponent(is.getTranslationKey());
		im.setDisplayName(item.toPlainText() + poisonSuffix(severity, venom));
		ArrayList<String> lore =new ArrayList<String>();
		lore.add("Severity: " + severity);
		if(venom) { lore.add("Envenomed");}
		/*
		if(im instanceof Repairable)
		{
			Repairable imr = (Repairable) im;
			imr.setRepairCost(64);
		}
		*/
		im.setLore(lore);
		im.addItemFlags();
		im.getPersistentDataContainer().set(WeaponPoison.POISONED_ITEM_KEY, new ItemPoisonedDataType(), new ItemPoisonData(severity, venom));
		//im.getPersistentDataContainer().set(WeaponPoison.VENOM_KEY, PersistentDataType.BOOLEAN, venom);
		return im;
	}
	public static String poisonSuffix(int severity, boolean venom)
	{
		StringBuilder sb = new StringBuilder();
		int characters = (severity - 20)/5;
		if(venom) { sb.append(" (v");}
		else {sb.append(" (p");}
		if(characters > 0)
		{
			sb.append(new String(new char[Math.abs(characters)]).replace("\0", "+"));
		}
		else
		{
			sb.append(new String(new char[Math.abs(characters)]).replace("\0", "-"));
		}
		sb.append(")");
		return sb.toString();
	}
}
