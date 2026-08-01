package derekahedron.customrecords.event;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.UpdateTrackedPressedButtonsPacket;
import derekahedron.customrecords.util.PressedSoundEffectButtonsManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

@Mod.EventBusSubscriber(modid = CustomRecords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PressedButtonsEventHandler {

    @SubscribeEvent
    public static void clearPressed(ServerAboutToStartEvent event) {
        PressedSoundEffectButtonsManager.clear();
    }

    @SubscribeEvent
    public static void tickPressed(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.side != LogicalSide.SERVER) return;
        PressedSoundEffectButtonsManager.tick(event.getServer());
    }

    @SubscribeEvent
    public static void trackPressed(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof Player tracked)
                || !(event.getEntity() instanceof ServerPlayer player)) return;
        Collection<EquipmentSlot> pressedSlots = PressedSoundEffectButtonsManager.getPressedEquipmentSlots(tracked);

        if (pressedSlots.isEmpty()) return;
        CRPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new UpdateTrackedPressedButtonsPacket(
                        tracked.getUUID(),
                        pressedSlots));
    }

    @SubscribeEvent
    public static void unTrackPressed(PlayerEvent.StopTracking event) {
        if (!(event.getTarget() instanceof Player tracked)
                || !(event.getEntity() instanceof ServerPlayer player)) return;

        CRPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new UpdateTrackedPressedButtonsPacket(
                        tracked.getUUID(),
                        Collections.emptySet()));
    }
}
