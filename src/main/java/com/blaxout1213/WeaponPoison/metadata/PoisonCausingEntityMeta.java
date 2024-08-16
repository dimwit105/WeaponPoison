package com.blaxout1213.WeaponPoison.metadata;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class PoisonCausingEntityMeta extends FixedMetadataValue
{
	private final PoisonCausingEntity pce;

	public PoisonCausingEntityMeta(Plugin owningPlugin, PoisonCausingEntity value)
	{
		super(owningPlugin, value);
		this.pce = value;
	}

	public PoisonCausingEntity getPoisonCausingEntity()
	{
		return pce;
	}
}