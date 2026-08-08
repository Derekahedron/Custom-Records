package derekahedron.customrecords.util.slotreference;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.Optional;
import java.util.stream.IntStream;

public record NestedSlotReference(SlotReference slotReference, int slotIndex) implements SlotReference {

    public static final Codec<NestedSlotReference> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SlotReference.CODEC
                    .fieldOf("slot_reference")
                    .forGetter(NestedSlotReference::slotReference),
            Codec.INT
                    .fieldOf("slot_index")
                    .forGetter(NestedSlotReference::slotIndex)
    ).apply(instance, NestedSlotReference::new));

    @Override
    public Optional<ItemStack> getStackForPlayer(Player player) {
        return slotReference.getStackForPlayer(player)
                .filter(stack -> !stack.isEmpty())
                .flatMap(stack -> stack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve())
                .map(handler -> handler.getStackInSlot(slotIndex));
    }

    @Override
    public Optional<SlotReference> getHoldingSlotReference() {
        return Optional.of(slotReference);
    }

    @Override
    public SlotReferenceSerializer<NestedSlotReference> getSerializer() {
        return SlotReferenceSerializers.NESTED.get();
    }

    public static class Serializer implements SlotReferenceSerializer<NestedSlotReference> {

        @Override
        public NestedSlotReference fromNetwork(FriendlyByteBuf buffer) {
            return new NestedSlotReference(
                    SlotReference.fromNetwork(buffer),
                    buffer.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, NestedSlotReference slotReference) {
            SlotReference.toNetwork(buffer, slotReference.slotReference);
            buffer.writeInt(slotReference.slotIndex);
        }

        @Override
        public Codec<NestedSlotReference> getCodec() {
            return CODEC;
        }
    }

    public static void expandSlotReferences(SlotReferenceEvent.ExpandSlotReference event) {
        SlotReference slotReference = event.getSlotReference();

        event.getStack().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve()
                .map(handler -> IntStream.range(0, handler.getSlots())
                        .filter(i -> !handler.getStackInSlot(i).isEmpty())
                        .mapToObj(i -> (SlotReference) new NestedSlotReference(slotReference, i)))
                .ifPresent(event::acceptReferences);
    }
}
