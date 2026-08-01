package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.UpdatePressedButtonsPacketHandler;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record UpdatePressedButtonPacket(SlotReference slotReference, boolean isPressed) {

    public UpdatePressedButtonPacket(FriendlyByteBuf buffer) {
        this(
                SlotReference.fromNetwork(buffer),
                buffer.readBoolean());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        SlotReference.toNetwork(buffer, slotReference);
        buffer.writeBoolean(isPressed);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        UpdatePressedButtonsPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
