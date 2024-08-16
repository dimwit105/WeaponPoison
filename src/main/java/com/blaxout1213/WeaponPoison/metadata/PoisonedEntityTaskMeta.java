package com.blaxout1213.WeaponPoison.metadata;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import com.blaxout1213.WeaponPoison.tasks.PoisonedEntityTask;

public class PoisonedEntityTaskMeta extends FixedMetadataValue
{
	private final PoisonedEntityTask task;

	public PoisonedEntityTaskMeta(Plugin owningPlugin, PoisonedEntityTask task)
	{
		super(owningPlugin, task);
		this.task = task;
	}
	public PoisonedEntityTask getPoisonedEntityTask()
	{
		return task;
	}

}
