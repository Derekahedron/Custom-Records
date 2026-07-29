package derekahedron.customrecords.util.slotreference;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record VanillaSlotReference(int containerId, int slotIndex) implements SlotReference {

    @Override
    public Optional<ItemStack> getStackForPlayer(Player player) {
        if (slotIndex < 0) {
            return Optional.empty();
        } else if (containerId == 0) {
            return slotIndex < player.inventoryMenu.slots.size()
                    ? Optional.of(player.inventoryMenu.getSlot(slotIndex).getItem())
                    : Optional.empty();
        } else if (containerId == player.containerMenu.containerId) {
            return slotIndex < player.containerMenu.slots.size()
                    ? Optional.of(player.containerMenu.getSlot(slotIndex).getItem())
                    : Optional.empty();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public SlotReferenceSerializer<VanillaSlotReference> getSerializer() {
        return SlotReferenceSerializers.VANILLA.get();
    }

    public static class Serializer implements SlotReferenceSerializer<VanillaSlotReference> {

        @Override
        public VanillaSlotReference fromNetwork(FriendlyByteBuf buffer) {
            return new VanillaSlotReference(
                    buffer.readInt(),
                    buffer.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, VanillaSlotReference slotReference) {
            buffer.writeInt(slotReference.containerId);
            buffer.writeInt(slotReference.slotIndex);
        }
    }
}
