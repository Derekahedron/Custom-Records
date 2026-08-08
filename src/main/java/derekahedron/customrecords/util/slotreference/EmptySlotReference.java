package derekahedron.customrecords.util.slotreference;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class EmptySlotReference implements SlotReference {

    public static final Codec<EmptySlotReference> CODEC = Codec.unit(() -> SlotReference.EMPTY);

    @Override
    public Optional<ItemStack> getStackForPlayer(Player player) {
        return Optional.empty();
    }

    @Override
    public SlotReferenceSerializer<EmptySlotReference> getSerializer() {
        return SlotReferenceSerializers.EMPTY.get();
    }

    public static class Serializer implements SlotReferenceSerializer<EmptySlotReference> {

        @Override
        public EmptySlotReference fromNetwork(FriendlyByteBuf buffer) {
            return SlotReference.EMPTY;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, EmptySlotReference slotReference) {
        }

        @Override
        public Codec<EmptySlotReference> getCodec() {
            return CODEC;
        }
    }
}
