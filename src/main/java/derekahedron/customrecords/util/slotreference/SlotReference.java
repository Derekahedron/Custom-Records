package derekahedron.customrecords.util.slotreference;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface SlotReference {

    /**
     * Gets the stack in the referenced slot for a given player
     *
     * @param player the player to get the stack for
     * @return the {@link ItemStack} in this slot; <code>empty</code> if the slot is not represented in the player
     */
    Optional<ItemStack> getStack(Player player);

    <T extends SlotReference> SlotReferenceSerializer<T> getSerializer();

    static SlotReference fromNetwork(FriendlyByteBuf buffer) {
        SlotReferenceSerializer<? extends SlotReference> serializer = buffer.readRegistryIdUnsafe(SlotReferenceSerializers.REGISTRY.get());
        return serializer == null ? new EmptySlotReference() : serializer.fromNetwork(buffer);
    }

    static <T extends SlotReference> void toNetwork(FriendlyByteBuf buffer, T slotReference) {
        SlotReferenceSerializer<T> serializer = slotReference.getSerializer();
        buffer.writeRegistryIdUnsafe(SlotReferenceSerializers.REGISTRY.get(), serializer);
        serializer.toNetwork(buffer, slotReference);
    }
}
