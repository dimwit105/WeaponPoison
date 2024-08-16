package com.blaxout1213.WeaponPoison.metadata;

public class ItemPoisonData
{
	private int severity;
	private boolean venom;
	
	public ItemPoisonData(int severity, boolean venom)
	{
		this.severity = severity;
		this.venom = venom;
	}
	
	public int getSeverity()
	{
		return severity;
	}
	public boolean getVenom()
	{
		return venom;
	}
}