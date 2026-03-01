package derekahedron.customrecords.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.client.util.ClientProxy;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

public class CRUtil {
    public static final Codec<Rarity> RARITY_CODEC = Codec.either(Codec.STRING, Codec.INT).comapFlatMap((either) -> either.map(
            (str) -> {
                try {
                    return DataResult.success(Rarity.valueOf(str));
                } catch (IllegalArgumentException e) {
                    return DataResult.error(() -> "Unknown enum value name: " + str);
                }},
            (num) -> num >= 0 && num < Rarity.values().length
                    ? DataResult.success(Rarity.values()[num])
                    : DataResult.error(() -> "Unknown enum id: " + num)
            ),
            (value) -> Either.left(value.name()));

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(CustomRecords.MOD_ID, path);
    }

    @Nullable
    public static RegistryAccess getRegistryAccess() {
        return DistExecutor.unsafeRunForDist(
                () -> ClientProxy::getClientRegistryAccess,
                () -> () -> ServerLifecycleHooks.getCurrentServer().registryAccess()
        );
    }

    public static ItemStack putResourceLocation(ItemStack stack, String key, @Nullable ResourceLocation id) {
        CompoundTag tag;

        if (id != null) {
            tag = stack.getOrCreateTag();
            tag.putString(key, id.toString());
        } else {
            tag = stack.getTag();
            if (tag != null && tag.contains(key)) {
                tag.remove(key);
                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }
        }

        return stack;
    }
}
