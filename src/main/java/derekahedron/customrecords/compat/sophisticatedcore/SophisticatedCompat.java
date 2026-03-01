package derekahedron.customrecords.compat.sophisticatedcore;

import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.DiscHandlerRegistry;

public class SophisticatedCompat {

    public static void initialize() {
        DiscHandlerRegistry.getHandlers().add(0, new CustomRecordDiscHandler());
    }
}
