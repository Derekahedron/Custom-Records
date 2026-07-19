package derekahedron.customrecords.event;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.util.InventoryCallbacksManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CustomRecords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InventoryCallbacksEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void tickCallbacks(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.side != LogicalSide.SERVER) return;
        InventoryCallbacksManager.tick(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        InventoryCallbacksManager.clearCallbacks(player);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        InventoryCallbacksManager.clearCallbacks(player);
    }
}
