package derekahedron.customrecords.compat.curios;

import derekahedron.customrecords.CustomRecords;
import net.minecraftforge.eventbus.api.IEventBus;

public class CuriosCompat {

    public static void init(IEventBus modEventBus) {
        CuriosSlotReferenceSerializers.SLOT_REFERENCE_SERIALIZERS.register(modEventBus);
        CustomRecords.EVENT_BUS.addListener(CuriosSlotReference::addCuriosSlots);
    }
}
