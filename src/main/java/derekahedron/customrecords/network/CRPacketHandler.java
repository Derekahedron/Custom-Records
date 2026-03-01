package derekahedron.customrecords.network;

import derekahedron.customrecords.util.CRUtil;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

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
                    PlayCustomRecordPacket::handle);
            INSTANCE.registerMessage(getId(),
                    PlaySophisticatedCustomRecordPacket.class,
                    PlaySophisticatedCustomRecordPacket::toBytes,
                    PlaySophisticatedCustomRecordPacket::new,
                    PlaySophisticatedCustomRecordPacket::handle);
        });
    }
}
