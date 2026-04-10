package com.blaxout1213.WeaponPoison.metadata;

import com.blaxout1213.WeaponPoison.tasks.PoisonedEntityTask;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.nio.ByteBuffer;

public class PoisonedEntityDataType implements PersistentDataType<byte[], PoisonedEntityTask>
{
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<PoisonedEntityTask> getComplexType() {
        return PoisonedEntityTask.class;
    }

    @Override
    public @NonNull byte[] toPrimitive(@NonNull PoisonedEntityTask complex, @NotNull PersistentDataAdapterContext context)
    {

        ByteBuffer bb = ByteBuffer.wrap(new byte[100]);
        return complex.writeToBytes(bb);
    }

    @Override
    public @NonNull PoisonedEntityTask fromPrimitive(@NonNull byte[] primitive, @NotNull PersistentDataAdapterContext context) {
        return new PoisonedEntityTask(primitive);
    }
}
