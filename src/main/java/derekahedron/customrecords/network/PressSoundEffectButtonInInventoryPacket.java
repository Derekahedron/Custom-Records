package derekahedron.customrecords.network;

import derekahedron.customrecords.item.SoundEffectButtonItem;
import derekahedron.customrecords.util.InventoryCallbacksManager;
import derekahedron.customrecords.util.PressedSoundEffectButtonsManager;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public record PressSoundEffectButtonInInventoryPacket(SlotReference slotReference) {

    public static final HashMap<UUID, HashSet<SlotReference>> EXISTING_CALLBACKS = new HashMap<>();

    public PressSoundEffectButtonInInventoryPacket(FriendlyByteBuf buffer) {
        this(SlotReference.fromNetwork(buffer));
    }

    public void toBytes(FriendlyByteBuf buffer) {
        SlotReference.toNetwork(buffer, slotReference);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) return;
            Level level = player.level();
            UUID playerId = player.getUUID();

            if (PressedSoundEffectButtonsManager.isPressed(player, slotReference)) return;

            ItemStack stack = slotReference.getStack(player).orElse(null);
            if (stack == null || !(stack.getItem() instanceof SoundEffectButtonItem item)) return;

            boolean hasSoundEffect = item.getSoundEffect(stack) != null;

            item.onInventoryPress(stack, player, slotReference);

            if (!EXISTING_CALLBACKS.containsKey(playerId) || !EXISTING_CALLBACKS.get(playerId).contains(slotReference)) {
                EXISTING_CALLBACKS.computeIfAbsent(playerId, k -> new HashSet<>()).add(slotReference);

                InventoryCallbacksManager.addCallback(player, slotReference, new InventoryCallbacksManager.Callback(slotReference, stack) {
                    @Override
                    public void onChange(@Nullable ServerPlayer serverPlayer) {
                        Optional.ofNullable(EXISTING_CALLBACKS.get(playerId)).ifPresent(set -> {
                            set.remove(this.slotReference);
                            if (set.isEmpty()) {
                                EXISTING_CALLBACKS.remove(playerId);
                            }
                        });

                        if (serverPlayer != null) {
                            if (hasSoundEffect) {
                                item.stopSound(serverPlayer, this.slotReference);
                            }
                            if (PressedSoundEffectButtonsManager.isPressed(serverPlayer, this.slotReference)) {
                                item.onInventoryUnpress(this.stack, serverPlayer, this.slotReference);
                            }
                        } else {
                            if (hasSoundEffect) {
                                CRPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PlaySoundEffectButtonInventoryPacket(
                                        playerId,
                                        this.slotReference,
                                        Optional.empty(),
                                        true));
                            }
                            CRPacketHandler.INSTANCE.send(
                                    PacketDistributor.DIMENSION.with(level::dimension),
                                    new UpdatePressedSoundEffectButtonPacket(playerId, this.slotReference, false));
                        }
                    }
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}
