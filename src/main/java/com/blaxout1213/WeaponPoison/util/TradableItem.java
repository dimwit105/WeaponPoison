package com.blaxout1213.WeaponPoison.util;

import org.bukkit.Material;

public class TradableItem
{
	Material mat;
	int cost;
	
	public TradableItem(Material mat, int cost)
	{
		this.mat = mat;
		this.cost = cost;
	}
	
	public Material getMaterial()
	{
		return mat;
	}
	public int getCost()
	{
		return cost;
	}
}
