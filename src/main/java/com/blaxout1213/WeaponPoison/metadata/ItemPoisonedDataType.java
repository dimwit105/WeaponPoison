package com.blaxout1213.WeaponPoison.metadata;

import java.nio.ByteBuffer;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class ItemPoisonedDataType implements PersistentDataType<byte[], ItemPoisonData>
{


	@Override
	public Class<byte[]> getPrimitiveType()
	{
		return byte[].class;
	}

	@Override
	public Class<ItemPoisonData> getComplexType()
	{
		return ItemPoisonData.class;
	}
	
	@Override
	public ItemPoisonData fromPrimitive(byte[] primative, PersistentDataAdapterContext arg1)
	{
		ByteBuffer bb = ByteBuffer.wrap(primative);
		return new ItemPoisonData(bb.getInt(), bb.get() == 1 ? true : false);
	}
	
	@Override
	public byte[] toPrimitive(ItemPoisonData complex, PersistentDataAdapterContext arg1)
	{
		ByteBuffer bb = ByteBuffer.wrap(new byte[5]);
		bb.putInt(complex.getSeverity());
		bb.put((byte) (complex.getVenom() ? 1:0));
		return bb.array();
	}
}
