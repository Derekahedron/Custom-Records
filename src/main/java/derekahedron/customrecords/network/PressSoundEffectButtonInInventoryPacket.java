package derekahedron.customrecords.network;

import derekahedron.customrecords.item.SoundEffectButtonItem;
import derekahedron.customrecords.util.PressedSoundEffectButtonsManager;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PressSoundEffectButtonInInventoryPacket(SlotReference slotReference) {

    public PressSoundEffectButtonInInventoryPacket(FriendlyByteBuf buffer) {
        this(SlotReference.fromNetwork(buffer));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        SlotReference.toNetwork(buffer, slotReference);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) return;
            if (PressedSoundEffectButtonsManager.isPressed(player, slotReference)) return;

            ItemStack stack = slotReference.getStackForPlayer(player).orElse(null);
            if (stack == null) return;
            if (!(stack.getItem() instanceof SoundEffectButtonItem item)) return;

            item.onInventoryPress(stack, player, slotReference, false);
        });
        context.get().setPacketHandled(true);
    }
}
