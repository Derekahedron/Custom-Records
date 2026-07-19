package derekahedron.customrecords.util.slotreference;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class EmptySlotReference implements SlotReference {

    @Override
    public Optional<ItemStack> getStack(Player player) {
        return Optional.empty();
    }

    @Override
    public SlotReferenceSerializer<EmptySlotReference> getSerializer() {
        return SlotReferenceSerializers.EMPTY.get();
    }

    public static class Serializer implements SlotReferenceSerializer<EmptySlotReference> {

        @Override
        public EmptySlotReference fromNetwork(FriendlyByteBuf buffer) {
            return new EmptySlotReference();
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, EmptySlotReference slotReference) {
        }
    }
}
