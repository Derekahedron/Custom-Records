package derekahedron.customrecords.network;

import derekahedron.customrecords.item.OpenableItem;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public record OpenItemPacket(SlotReference slotReference) {

    public OpenItemPacket(FriendlyByteBuf buffer) {
        this(SlotReference.fromNetwork(buffer));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        SlotReference.toNetwork(buffer, slotReference);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) return;

            ItemStack stack = slotReference.getStackForPlayer(player).orElse(null);
            if (stack == null) return;
            if (!(stack.getItem() instanceof OpenableItem item)) return;

            item.getHandler(stack).ifPresent(handler ->
                    NetworkHooks.openScreen(
                            player,
                            new SimpleMenuProvider(
                                    item.getMenuConstructor(slotReference),
                                    stack.getHoverName()),
                            buffer -> SlotReference.toNetwork(buffer, slotReference)));
        });
        context.get().setPacketHandled(true);
    }
}
