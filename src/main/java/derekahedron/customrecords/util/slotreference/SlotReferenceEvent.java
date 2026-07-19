package derekahedron.customrecords.util.slotreference;

import derekahedron.customrecords.CustomRecords;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SlotReferenceEvent {

    public static class GetInventory extends Event {

        private final ArrayList<Stream<SlotReference>> slotReferences = new ArrayList<>();
        private final Player player;

        public GetInventory(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return this.player;
        }

        public void acceptReferences(Stream<SlotReference> slotReferences) {
            this.slotReferences.add(slotReferences);
        }
    }

    public static class ExpandSlotReference extends Event {

        private final ArrayList<Stream<SlotReference>> slotReferences = new ArrayList<>();
        private final Player player;
        private final SlotReference slotReference;
        private final ItemStack stack;

        public ExpandSlotReference(Player player, SlotReference slotReference, ItemStack stack) {
            this.player = player;
            this.slotReference = slotReference;
            this.stack = stack;
        }

        public Player getPlayer() {
            return this.player;
        }

        public SlotReference getSlotReference() {
            return this.slotReference;
        }

        public ItemStack getStack() {
            return this.stack;
        }

        public void acceptReferences(Stream<SlotReference> slotReferences) {
            this.slotReferences.add(slotReferences);
        }
    }

    public static Optional<SlotReference> getSlotReference(Player player, ItemStack stack) {
        return getSlotReferences(player)
                .filter(slotReference -> slotReference.getStack(player)
                        .map(slotStack -> slotStack == stack)
                        .orElse(false))
                .findFirst();
    }

    public static Stream<SlotReference> getSlotReferences(Player player) {
        Stream<SlotReference> slotReferences = Stream.concat(
                getEventSlotReferences(player),
                getVanillaSlotReferences(player));

        return slotReferences.flatMap(slotReference -> {
            ItemStack stack = slotReference.getStack(player).orElse(ItemStack.EMPTY);
            if (stack.isEmpty()) return Stream.empty();

            ExpandSlotReference event = new ExpandSlotReference(player, slotReference, stack);

            CustomRecords.EVENT_BUS.post(event);

            return Stream.concat(
                    Stream.of(slotReference),
                    event.slotReferences.stream()
                            .flatMap(Function.identity())
            );
        });
    }

    private static Stream<SlotReference> getEventSlotReferences(Player player) {
        GetInventory event = new GetInventory(player);

        CustomRecords.EVENT_BUS.post(event);

        return event.slotReferences.stream()
                .flatMap(Function.identity());
    }

    private static Stream<SlotReference> getVanillaSlotReferences(Player player) {
        return IntStream.range(0, player.inventoryMenu.slots.size())
                .filter(i -> !player.inventoryMenu.slots.get(i).getItem().isEmpty())
                .mapToObj(i -> new VanillaSlotReference(0, i));
    }
}
