package derekahedron.customrecords.event;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.item.PortableEnderJukeboxInventory;
import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.SyncEnderJukeboxPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

@Mod.EventBusSubscriber(modid = CustomRecords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnderJukeboxStorageEventHandler {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PortableEnderJukeboxInventory.class);
    }

    @SubscribeEvent
    public static void attachStorage(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;

        PortableEnderJukeboxInventory.Provider provider = new PortableEnderJukeboxInventory.Provider();
        event.addCapability(PortableEnderJukeboxInventory.ID, provider);
    }

    @SubscribeEvent
    public static void copyStorage(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player copy = event.getEntity();
        original.reviveCaps();
        PortableEnderJukeboxInventory originalInventory = original.getCapability(PortableEnderJukeboxInventory.CAPABILITY)
                .resolve().orElse(null);
        PortableEnderJukeboxInventory copyInventory = copy.getCapability(PortableEnderJukeboxInventory.CAPABILITY)
                .resolve().orElse(null);
        if (originalInventory != null && copyInventory != null) {
            copyInventory.records.clear();
            copyInventory.records.addAll(originalInventory.records);
        }
        original.invalidateCaps();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void syncOnLogin(PlayerEvent.PlayerLoggedInEvent event) {
        sync(event.getEntity());
    }

    @SubscribeEvent
    public static void syncOnRespawn(PlayerEvent.PlayerRespawnEvent event) {
        sync(event.getEntity());
    }

    @SubscribeEvent
    public static void syncOnDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        sync(event.getEntity());
    }

    public static void sync(Player entity) {
        if (!(entity instanceof ServerPlayer player)) return;

        player.getCapability(PortableEnderJukeboxInventory.CAPABILITY).ifPresent(inventory ->
                CRPacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SyncEnderJukeboxPacket(List.copyOf(inventory.records))));
    }
}
