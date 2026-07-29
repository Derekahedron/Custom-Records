package derekahedron.customrecords.client.gui;

import derekahedron.customrecords.inventory.CRMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CRMenuScreens {

    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                MenuScreens.register(CRMenuTypes.SOUND_BOARD.get(), SoundBoardScreen::new));
    }
}
