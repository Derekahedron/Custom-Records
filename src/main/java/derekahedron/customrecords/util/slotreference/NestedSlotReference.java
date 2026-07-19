package derekahedron.customrecords.util.slotreference;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.Optional;
import java.util.stream.IntStream;

public record NestedSlotReference(SlotReference slotReference, int slotIndex) implements SlotReference {

    @Override
    public Optional<ItemStack> getStack(Player player) {
        return slotReference.getStack(player)
                .flatMap(stack -> stack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve())
                .map(handler -> handler.getStackInSlot(slotIndex));
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
