package derekahedron.customrecords.network;

import derekahedron.customrecords.client.network.JukeboxPlaybackPacketHandler;
import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.Tuple;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.function.Supplier;

public record SyncEnderJukeboxPacket(Collection<Tuple<ItemStack, Integer>> records) {

    public SyncEnderJukeboxPacket(FriendlyByteBuf buffer) {
        this(CRUtil.collectionReader(
                NonNullList::createWithCapacity,
                Tuple.reader(
                        FriendlyByteBuf::readItem,
                        FriendlyByteBuf::readVarInt))
                .apply(buffer));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        CRUtil.collectionWriter(
                Tuple.writer(
                        FriendlyByteBuf::writeItem,
                        FriendlyByteBuf::writeVarInt))
                .accept(buffer, records);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        JukeboxPlaybackPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
