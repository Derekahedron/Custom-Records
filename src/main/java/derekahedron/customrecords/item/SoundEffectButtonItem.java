package derekahedron.customrecords.item;

import derekahedron.customrecords.block.AbstractSoundEffectButtonBlock;
import derekahedron.customrecords.client.network.PlaySoundEffectButtonPacketHandler;
import derekahedron.customrecords.client.util.ClientActiveSoundsManager;
import derekahedron.customrecords.client.util.ClientPressedSoundEffectButtonsManager;
import derekahedron.customrecords.network.*;
import derekahedron.customrecords.stats.CRStats;
import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.InventoryCallbacksManager;
import derekahedron.customrecords.util.PressedSoundEffectButtonsManager;
import derekahedron.customrecords.util.Tuple;
import derekahedron.customrecords.util.slotreference.SlotReference;
import derekahedron.customrecords.util.slotreference.SlotReferenceEvent;
import derekahedron.customrecords.util.slotreference.VanillaSlotReference;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;

public class SoundEffectButtonItem extends BlockItem {

    public static final String SOUND_EFFECT_KEY = "SoundEffect";

    public SoundEffectButtonItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag advanced) {
        super.appendHoverText(stack, level, components, advanced);
        getSoundEffectTextComponent(stack)
                .ifPresent(component -> components.add(component.withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        int maxStackSize = super.getMaxStackSize(stack);

        if (maxStackSize != 1 && getSoundEffectId(stack) != null) {
            return 1;
        } else {
            return maxStackSize;
        }
    }

    @Nullable
    public ResourceLocation getSoundEffectId(ItemStack stack) {
        if (!(stack.getItem() instanceof SoundEffectButtonItem)) return null;
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(SOUND_EFFECT_KEY)) return null;
        return ResourceLocation.tryParse(tag.getString(SOUND_EFFECT_KEY));
    }

    @Nullable
    public SoundEvent getSoundEffect(ItemStack stack) {
        ResourceLocation soundEffectId = getSoundEffectId(stack);
        if (soundEffectId == null) return null;
        return SoundEvent.createVariableRangeEvent(soundEffectId);
    }

    @SuppressWarnings("UnusedReturnValue")
    public ItemStack putSoundEffectId(ItemStack stack, @Nullable ResourceLocation soundEffectId) {
        return CRUtil.putResourceLocation(stack, SOUND_EFFECT_KEY, soundEffectId);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        SlotReference slotReference = CRUtil.getSlotReference(player, hand);
        if (stack != slotReference.getStackForPlayer(player).orElse(null)) {
            return InteractionResultHolder.fail(stack);
        }

        if (CRUtil.isButtonPressed(player, slotReference)) {
            return InteractionResultHolder.fail(stack);
        }

        if (player.level().isClientSide()) {
            CRPacketHandler.INSTANCE.sendToServer(new PressSoundEffectButtonInInventoryPacket(slotReference));
            onInventoryPress(stack, player, slotReference, false);
        }

        return InteractionResultHolder.sidedSuccess(stack, player.level().isClientSide);
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack stack,
            ItemStack otherStack,
            Slot slot,
            ClickAction clickAction,
            Player player,
            SlotAccess slotAccess) {
        if (clickAction != ClickAction.SECONDARY
                || !otherStack.isEmpty()
                || stack.getCount() > 1) return false;

        SlotReference slotReference = SlotReferenceEvent.getSlotReference(player, slot.getItem()).orElseGet(() -> {
            if (!(player.containerMenu instanceof CreativeModeInventoryScreen.ItemPickerMenu)) {
                return new VanillaSlotReference(player.containerMenu.containerId, slot.index);
            } else {
                return null;
            }
        });

        if (slotReference == null) return false;
        if (CRUtil.isButtonPressed(player, slotReference)) return true;

        if (player.level().isClientSide()) {
            CRPacketHandler.INSTANCE.sendToServer(new PressSoundEffectButtonInInventoryPacket(slotReference));
            onInventoryPress(stack, player, slotReference, false);
        }
        return true;
    }

    public void onInventoryPress(ItemStack stack, Player player, SlotReference slotReference, boolean updatePlayer) {
        SoundEvent soundEffect = getSoundEffect(stack);
        boolean isGlobal = isGlobal(player, slotReference);

        player.awardStat(CRStats.PRESS_SOUND_EFFECT_BUTTON.get());
        player.gameEvent(GameEvent.ITEM_INTERACT_START);

        if (soundEffect == null
                && getBlock() instanceof AbstractSoundEffectButtonBlock block) {
            player.level().playSound(
                    updatePlayer && !player.level().isClientSide ? null : player,
                    player,
                    block.getClickOnSoundEvent(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F);
        }

        if (soundEffect != null) {
            playSound(player, slotReference, soundEffect, isGlobal, updatePlayer);
        }

        pressButton(player, slotReference, updatePlayer);
    }

    public void onInventoryUnpress(ItemStack stack, Player player, SlotReference slotReference, boolean updatePlayer) {
        player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);

        if (getSoundEffectId(stack) == null
                && getBlock() instanceof AbstractSoundEffectButtonBlock block) {
            player.level().playSound(
                    updatePlayer && !player.level().isClientSide ? null : player,
                    player,
                    block.getClickOffSoundEvent(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F);
        }

        unpressButton(player, slotReference, updatePlayer);
    }

    public void pressButton(Player player, SlotReference slotReference, boolean updatePlayer) {
        if (player instanceof ServerPlayer serverPlayer) {
            PressedSoundEffectButtonsManager.setPressedTicksRemaining(
                    serverPlayer,
                    slotReference,
                    AbstractSoundEffectButtonBlock.TICKS_TO_STAY_PRESSED);

            if (updatePlayer) {
                CRPacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new UpdatePressedButtonPacket(
                                slotReference,
                                true));
            }

            CRUtil.getEquipmentSlot(player, slotReference).ifPresent(equipmentSlot ->
                    CRPacketHandler.INSTANCE.send(
                            PacketDistributor.TRACKING_ENTITY.with(() -> serverPlayer),
                            new UpdateTrackedPressedButtonsPacket(
                                    player.getUUID(),
                                    equipmentSlot,
                                    true)));

            new PressedCallback(serverPlayer, slotReference).add();
        } else {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPressedSoundEffectButtonsManager.setPressed(slotReference, true));
        }
    }

    public void unpressButton(Player player, SlotReference slotReference, boolean updatePlayer) {
        if (player instanceof ServerPlayer serverPlayer) {
            PressedSoundEffectButtonsManager.setPressedTicksRemaining(
                    serverPlayer,
                    slotReference,
                    0);

            if (updatePlayer) {
                CRPacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new UpdatePressedButtonPacket(
                                slotReference,
                                false));
            }

            CRUtil.getEquipmentSlot(player, slotReference).ifPresent(equipmentSlot ->
                    CRPacketHandler.INSTANCE.send(
                            PacketDistributor.TRACKING_ENTITY.with(() -> serverPlayer),
                            new UpdateTrackedPressedButtonsPacket(
                                    player.getUUID(),
                                    equipmentSlot,
                                    false)));
        } else {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPressedSoundEffectButtonsManager.setPressed(slotReference, false));
        }
    }

    public boolean isGlobal(Player player, SlotReference slotReference) {
        return slotReference.getHoldingSlotReference()
                .flatMap(holdingReference -> holdingReference.getStackForPlayer(player))
                .map(stack -> stack.is(CRItemTags.GLOBAL_SOUND_BOARDS))
                .orElse(false);
    }

    public void playSound(Player player, SlotReference slotReference, SoundEvent soundEffect, boolean isGlobal, boolean updatePlayer) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (isGlobal) {
                CRPacketHandler.INSTANCE.send(
                        updatePlayer
                                ? PacketDistributor.ALL.noArg()
                                : CRPacketHandler.ALL_BUT_PLAYER.with(() -> serverPlayer),
                        new PlaySoundEffectButtonInventoryPacket(
                                player.getUUID(),
                                slotReference,
                                soundEffect,
                                true));
            } else {
                CRPacketHandler.INSTANCE.send(
                        CRUtil.withinDistance(serverPlayer, AbstractSoundEffectButtonBlock.SOUND_EVENT_RADIUS, updatePlayer ? null : serverPlayer),
                        new PlaySoundEffectButtonInventoryPacket(
                                player.getUUID(),
                                slotReference,
                                soundEffect,
                                false));
            }

            new SoundEffectCallback(serverPlayer, slotReference, isGlobal).add();
        } else {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientActiveSoundsManager.playSoundInstance(
                            player.getUUID(),
                            slotReference,
                            PlaySoundEffectButtonPacketHandler.globalSoundInstance(soundEffect, player.level().random)));
        }
    }

    public void stopSound(Player player, SlotReference slotReference, boolean isGlobal, boolean updatePlayer) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (isGlobal) {
                CRPacketHandler.INSTANCE.send(
                        updatePlayer
                                ? PacketDistributor.ALL.noArg()
                                : CRPacketHandler.ALL_BUT_PLAYER.with(() -> serverPlayer),
                        new PlaySoundEffectButtonInventoryPacket(
                                player.getUUID(),
                                slotReference,
                                true));
            } else {
                CRPacketHandler.INSTANCE.send(
                        updatePlayer
                                ? PacketDistributor.DIMENSION.with(player.level()::dimension)
                                : CRPacketHandler.DIMENSION_EXCEPT_PLAYER.with(() -> serverPlayer),
                        new PlaySoundEffectButtonInventoryPacket(
                                player.getUUID(),
                                slotReference,
                                false));
            }
        }
    }

    public Optional<MutableComponent> getSoundEffectTextComponent(ItemStack stack) {
        return Optional.ofNullable(getSoundEffectId(stack))
                .map(soundEffectId -> {
                    String descriptionId = Util.makeDescriptionId("sound_effect_button", soundEffectId);
                    return Component.translatable(descriptionId);
                });
    }

    public class SoundEffectCallback extends InventoryCallbacksManager.Callback {

        public static final HashSet<Tuple<UUID, SlotReference>> EXISTING_CALLBACKS = new HashSet<>();

        public final boolean isGlobal;

        public SoundEffectCallback(ServerPlayer player, SlotReference slotReference, boolean isGlobal) {
            super(player, slotReference);
            this.isGlobal = isGlobal;
        }

        public void add() {
            var callbackKey = new Tuple<>(playerId, slotReference);
            if (!SoundEffectCallback.EXISTING_CALLBACKS.contains(callbackKey)
                    && InventoryCallbacksManager.addCallback(this)) {
                SoundEffectCallback.EXISTING_CALLBACKS.add(callbackKey);
            }
        }

        @Override
        public void onChange(@Nullable ServerPlayer player) {
            EXISTING_CALLBACKS.remove(new Tuple<>(playerId, slotReference));

            if (player != null) {
                SoundEffectButtonItem.this.stopSound(player, slotReference, isGlobal, true);
            } else {
                CRPacketHandler.INSTANCE.send(
                        PacketDistributor.ALL.noArg(),
                        new PlaySoundEffectButtonInventoryPacket(
                                playerId,
                                this.slotReference,
                                Optional.empty(),
                                true));
            }
        }
    }

    public class PressedCallback extends InventoryCallbacksManager.Callback {

        public static final HashSet<Tuple<UUID, SlotReference>> EXISTING_CALLBACKS = new HashSet<>();

        public PressedCallback(ServerPlayer player, SlotReference slotReference) {
            super(player, slotReference);
        }

        public void add() {
            var callbackKey = new Tuple<>(playerId, slotReference);
            if (!PressedCallback.EXISTING_CALLBACKS.contains(callbackKey)
                    && InventoryCallbacksManager.addCallback(this)) {
                PressedCallback.EXISTING_CALLBACKS.add(callbackKey);
            }
        }

        @Override
        public void onChange(@Nullable ServerPlayer player) {
            EXISTING_CALLBACKS.remove(new Tuple<>(playerId, slotReference));

            if (player != null) {
                SoundEffectButtonItem.this.unpressButton(player, slotReference, true);
            } else {
                CRPacketHandler.INSTANCE.send(
                        PacketDistributor.ALL.noArg(),
                        new UpdateTrackedPressedButtonsPacket(
                                playerId,
                                List.of()));
            }
        }
    }
}
