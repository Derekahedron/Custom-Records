package derekahedron.customrecords.compat.curios;

import derekahedron.customrecords.util.slotreference.SlotReference;
import derekahedron.customrecords.util.slotreference.SlotReferenceEvent;
import derekahedron.customrecords.util.slotreference.SlotReferenceSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public record CuriosSlotReference(String identifier, int index) implements SlotReference {

    @Override
    public Optional<ItemStack> getStackForPlayer(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findCurio(identifier, index))
                .map(SlotResult::stack);
    }

    @Override
    public SlotReferenceSerializer<CuriosSlotReference> getSerializer() {
        return CuriosSlotReferenceSerializers.CURIOS.get();
    }

    public static class Serializer implements SlotReferenceSerializer<CuriosSlotReference> {

        @Override
        public CuriosSlotReference fromNetwork(FriendlyByteBuf buffer) {
            return new CuriosSlotReference(
                    buffer.readUtf(),
                    buffer.readVarInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CuriosSlotReference slotReference) {
            buffer.writeUtf(slotReference.identifier);
            buffer.writeVarInt(slotReference.index);
        }
    }

    public static void addCuriosSlots(SlotReferenceEvent.GetInventory event) {
        CuriosApi.getCuriosInventory(event.getPlayer())
                .lazyMap(ICuriosItemHandler::getCurios)
                .lazyMap(Map::entrySet)
                .lazyMap(Collection::stream)
                .lazyMap(entries ->
                        entries.flatMap(slotEntry -> IntStream.range(0, slotEntry.getValue().getStacks().getSlots())
                                .filter(i -> !slotEntry.getValue().getStacks().getStackInSlot(i).isEmpty())
                                .mapToObj(i -> (SlotReference) new CuriosSlotReference(slotEntry.getKey(), i))))
                .ifPresent(event::acceptReferences);
    }
}
