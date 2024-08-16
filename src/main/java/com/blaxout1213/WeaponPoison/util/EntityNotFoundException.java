package com.blaxout1213.WeaponPoison.util;
/**
 * Represents Bukkit failing to find an entity by its UUID, which we use to deserialize poison/venom data
 */
public class EntityNotFoundException extends Exception
{
	private static final long serialVersionUID = -4646554427149085531L;
	public EntityNotFoundException()
	{
		super();
	}
	public EntityNotFoundException(String message)
	{
		super(message);
	}
}
