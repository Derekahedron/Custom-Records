package derekahedron.customrecords.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.client.util.ClientPressedSoundEffectButtonsManager;
import derekahedron.customrecords.client.util.ClientProxy;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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

    /**
     * Gets if a button in a players inventory is currently being pressed.
     *
     * @param player the player to check the inventory of
     * @param slotReference the reference to the slot to check
     * @return <code>true</code> if a button in the given slot of the given player is being pressed; <code>false</code> otherwise
     */
    public static boolean isButtonPressed(Player player, SlotReference slotReference) {
        return DistExecutor.unsafeRunForDist(
                () -> () -> ClientPressedSoundEffectButtonsManager.isPressed(player, slotReference),
                () -> () -> PressedSoundEffectButtonsManager.isPressed(player, slotReference)
        );
    }

    public static ItemStack putResourceLocation(ItemStack stack, String key, @Nullable ResourceLocation id) {
        CompoundTag tag;

        if (id != null) {
            tag = stack.getOrCreateTag();
            tag.putString(key, id.toString());
        } else {
            stack.removeTagKey(key);
        }

        return stack;
    }
}
