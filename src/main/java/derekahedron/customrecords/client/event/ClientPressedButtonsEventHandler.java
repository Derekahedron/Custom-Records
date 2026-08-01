package derekahedron.customrecords.client.event;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.client.util.ClientPressedSoundEffectButtonsManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CustomRecords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientPressedButtonsEventHandler {

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientPressedSoundEffectButtonsManager.clear();
    }
}
