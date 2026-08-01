package derekahedron.customrecords.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.client.util.ClientPressedSoundEffectButtonsManager;
import derekahedron.customrecords.client.util.ClientProxy;
import derekahedron.customrecords.util.slotreference.SlotReference;
import derekahedron.customrecords.util.slotreference.VanillaSlotReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.IntFunction;

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

    public static SlotReference getSlotReference(Player player, InteractionHand hand) {
        return getSlotReference(player, hand == InteractionHand.MAIN_HAND
                ? EquipmentSlot.MAINHAND
                : EquipmentSlot.OFFHAND);
    }

    public static SlotReference getSlotReference(Player player, EquipmentSlot equipmentSlot) {
        int slotIndex = switch (equipmentSlot) {
            case HEAD -> 5;
            case CHEST -> 6;
            case LEGS -> 7;
            case FEET -> 8;
            case OFFHAND -> 45;
            case MAINHAND -> 36 + player.getInventory().selected;
        };
        return new VanillaSlotReference(0, slotIndex);
    }

    public static Optional<EquipmentSlot> getEquipmentSlot(Player player, SlotReference slotReference) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (getSlotReference(player, equipmentSlot).equals(slotReference)) {
                return Optional.of(equipmentSlot);
            }
        }

        return Optional.empty();
    }

    public static Optional<EquipmentSlot> getEquipmentSlot(Player player, ItemStack stack) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (stack == player.getItemBySlot(equipmentSlot)) {
                return Optional.of(equipmentSlot);
            }
        }

        return Optional.empty();
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

    public static PacketDistributor.PacketTarget withinDistance(Entity entity, double distance, @Nullable ServerPlayer excluded) {
        return PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                excluded,
                entity.getX(), entity.getY(), entity.getZ(),
                distance,
                entity.level().dimension()));
    }

    public static PacketDistributor.PacketTarget withinDistance(BlockPos pos, Level level, double distance, @Nullable ServerPlayer excluded) {
        return PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                excluded,
                pos.getX(), pos.getY(), pos.getZ(),
                distance,
                level.dimension()));
    }

    public static <T, C extends Collection<T>> FriendlyByteBuf.Reader<Collection<T>> collectionReader(
            IntFunction<C> collectionSupplier,
            FriendlyByteBuf.Reader<T> reader) {
        return buffer -> buffer.readCollection(collectionSupplier, reader);
    }

    public static <T> FriendlyByteBuf.Writer<Collection<T>> collectionWriter(
            FriendlyByteBuf.Writer<T> writer) {
        return (buffer, collection) -> buffer.writeCollection(collection, writer);
    }
}
