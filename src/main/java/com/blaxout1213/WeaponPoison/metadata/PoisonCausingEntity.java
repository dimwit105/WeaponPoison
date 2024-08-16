package com.blaxout1213.WeaponPoison.metadata;

public class PoisonCausingEntity
{
	public final int severity;
	public final boolean venom;
	public PoisonCausingEntity(int severity)
	{
		this(severity, false);
	}
	public PoisonCausingEntity(int severity, boolean venom)
	{
		this.severity = severity;
		this.venom = venom;
	}	
}
