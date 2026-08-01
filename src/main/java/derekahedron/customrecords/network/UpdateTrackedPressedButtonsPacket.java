package derekahedron.customrecords.network;

import com.mojang.datafixers.util.Either;
import derekahedron.customrecords.client.network.UpdatePressedButtonsPacketHandler;
import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.Tuple;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public record UpdateTrackedPressedButtonsPacket(
        UUID playerId,
        Either<Tuple<EquipmentSlot, Boolean>, Collection<EquipmentSlot>> equipment) {

    public static final int UPDATE_LIMIT = 255;

    public UpdateTrackedPressedButtonsPacket(UUID playerId, EquipmentSlot equipmentSlot, boolean isPressed) {
        this(
                playerId,
                Either.left(new Tuple<>(equipmentSlot, isPressed)));
    }

    public UpdateTrackedPressedButtonsPacket(UUID playerId, Collection<EquipmentSlot> equipment) {
        this(
                playerId,
                Either.right(equipment));
    }

    public UpdateTrackedPressedButtonsPacket(FriendlyByteBuf buffer) {
        this(
                buffer.readUUID(),
                buffer.readEither(
                        Tuple.reader(
                                b -> b.readEnum(EquipmentSlot.class),
                                FriendlyByteBuf::readBoolean),
                        CRUtil.collectionReader(
                                FriendlyByteBuf.limitValue(NonNullList::createWithCapacity, UPDATE_LIMIT),
                                b -> b.readEnum(EquipmentSlot.class))));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerId);
        buffer.writeEither(
                equipment,
                Tuple.writer(
                        FriendlyByteBuf::writeEnum,
                        FriendlyByteBuf::writeBoolean),
                CRUtil.collectionWriter(
                        FriendlyByteBuf::writeEnum));
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        UpdatePressedButtonsPacketHandler.handlePacket(this)));
        context.get().setPacketHandled(true);
    }
}
