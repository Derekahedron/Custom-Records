package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.JukeboxPlaybackPacketHandler;
import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.JukeboxPlayback;
import derekahedron.customrecords.util.Triple;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public record UpdateTrackedJukeboxPlaybacksPacket(
        Collection<Triple<UUID, SlotReference, JukeboxPlayback>> playbacks) {

    public static final int UPDATE_LIMIT = 255;

    public UpdateTrackedJukeboxPlaybacksPacket(FriendlyByteBuf buffer) {
        this(
                CRUtil.collectionReader(
                                FriendlyByteBuf.limitValue(NonNullList::createWithCapacity, UPDATE_LIMIT),
                                Triple.reader(
                                        FriendlyByteBuf::readUUID,
                                        SlotReference::fromNetwork,
                                        JukeboxPlayback::fromNetwork))
                        .apply(buffer));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        CRUtil.collectionWriter(
                        Triple.writer(
                                FriendlyByteBuf::writeUUID,
                                SlotReference::toNetwork,
                                JukeboxPlayback::toNetwork))
                .accept(buffer, playbacks);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        JukeboxPlaybackPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
