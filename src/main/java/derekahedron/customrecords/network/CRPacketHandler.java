package derekahedron.customrecords.network;

import derekahedron.customrecords.util.CRUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CRPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            CRUtil.location("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int id = 0;

    public static int getId() {
        return id++;
    }

    public static void initialize(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            INSTANCE.registerMessage(getId(),
                    PlayCustomRecordPacket.class,
                    PlayCustomRecordPacket::toBytes,
                    PlayCustomRecordPacket::new,
                    PlayCustomRecordPacket::handle,
                    Optional.of(NetworkDirection.PLAY_TO_CLIENT));
            INSTANCE.registerMessage(getId(),
                    PlaySophisticatedCustomRecordPacket.class,
                    PlaySophisticatedCustomRecordPacket::toBytes,
                    PlaySophisticatedCustomRecordPacket::new,
                    PlaySophisticatedCustomRecordPacket::handle,
                    Optional.of(NetworkDirection.PLAY_TO_CLIENT));
            INSTANCE.registerMessage(getId(),
                    PlaySoundEffectButtonPacket.class,
                    PlaySoundEffectButtonPacket::toBytes,
                    PlaySoundEffectButtonPacket::new,
                    PlaySoundEffectButtonPacket::handle,
                    Optional.of(NetworkDirection.PLAY_TO_CLIENT));
            INSTANCE.registerMessage(getId(),
                    PlaySoundEffectButtonInventoryPacket.class,
                    PlaySoundEffectButtonInventoryPacket::toBytes,
                    PlaySoundEffectButtonInventoryPacket::new,
                    PlaySoundEffectButtonInventoryPacket::handle,
                    Optional.of(NetworkDirection.PLAY_TO_CLIENT));
            INSTANCE.registerMessage(getId(),
                    PressSoundEffectButtonInInventoryPacket.class,
                    PressSoundEffectButtonInInventoryPacket::toBytes,
                    PressSoundEffectButtonInInventoryPacket::new,
                    PressSoundEffectButtonInInventoryPacket::handle,
                    Optional.of(NetworkDirection.PLAY_TO_SERVER));
            INSTANCE.registerMessage(getId(),
                    UpdatePressedSoundEffectButtonPacket.class,
                    UpdatePressedSoundEffectButtonPacket::toBytes,
                    UpdatePressedSoundEffectButtonPacket::new,
                    UpdatePressedSoundEffectButtonPacket::handle,
                    Optional.of(NetworkDirection.PLAY_TO_CLIENT));
            INSTANCE.registerMessage(getId(),
                    TrackPressedSoundEffectButtonPacket.class,
                    TrackPressedSoundEffectButtonPacket::toBytes,
                    TrackPressedSoundEffectButtonPacket::new,
                    TrackPressedSoundEffectButtonPacket::handle,
                    Optional.of(NetworkDirection.PLAY_TO_CLIENT));
            INSTANCE.registerMessage(getId(),
                    OpenSoundBoardPacket.class,
                    OpenSoundBoardPacket::toBytes,
                    OpenSoundBoardPacket::new,
                    OpenSoundBoardPacket::handle,
                    Optional.of(NetworkDirection.PLAY_TO_SERVER));
        });
    }

    public static final PacketDistributor<ServerPlayer> ALL_BUT_PLAYER =
            new PacketDistributor<>(CRPacketHandler::playerListAll, NetworkDirection.PLAY_TO_CLIENT);

    private static Consumer<Packet<?>> playerListAll(PacketDistributor<?> distributor, final Supplier<ServerPlayer> voidSupplier) {
        return p -> {
            ServerPlayer excludedPlayer = voidSupplier.get();
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(player -> {
                if (excludedPlayer != player) {
                    player.connection.send(p);
                }
            });
        };
    }
}
