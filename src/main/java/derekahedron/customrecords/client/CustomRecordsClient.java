package derekahedron.customrecords.client;

import derekahedron.customrecords.client.gui.CRMenuScreens;
import derekahedron.customrecords.client.render.CRItemModelOverrides;
import net.minecraftforge.eventbus.api.IEventBus;

public class CustomRecordsClient {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(CRItemModelOverrides::init);
        modEventBus.addListener(CRMenuScreens::init);
    }
}
