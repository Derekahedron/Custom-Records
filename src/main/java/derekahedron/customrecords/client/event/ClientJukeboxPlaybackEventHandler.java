package derekahedron.customrecords.client.event;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.client.util.ClientJukeboxPlaybackManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CustomRecords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientJukeboxPlaybackEventHandler {

    @SubscribeEvent
    public static void onChangeDimensions(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientJukeboxPlaybackManager.clear();
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientJukeboxPlaybackManager.clear();
    }

    @SubscribeEvent
    public static void onClientRespawn(ClientPlayerNetworkEvent.Clone event) {
        ClientJukeboxPlaybackManager.refreshSounds();
    }

    @SubscribeEvent
    public static void onPauseTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        if (ClientJukeboxPlaybackManager.LAST_PAUSED_AT == null
                && Minecraft.getInstance().isPaused()) {
            ClientJukeboxPlaybackManager.LAST_PAUSED_AT = System.currentTimeMillis();
        }
    }
}
