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

        SlotReference slotReference = SlotReferenceEvent.getSlotReference(player, stack).orElse(null);
        if (slotReference == null) return InteractionResultHolder.pass(stack);

        if (CRUtil.isButtonPressed(player, slotReference)) {
            return InteractionResultHolder.fail(stack);
        }

        if (player.level().isClientSide()) {
            CRPacketHandler.INSTANCE.sendToServer(new PressSoundEffectButtonInInventoryPacket(slotReference));
            onInventoryPress(stack, player, slotReference);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
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
            onInventoryPress(stack, player, slotReference);
        }
        return true;
    }

    public void onInventoryPress(ItemStack stack, Player player, SlotReference slotReference) {
        SoundEvent soundEffect = getSoundEffect(stack);
        boolean isGlobal = isGlobal(player, slotReference);

        player.awardStat(CRStats.PRESS_SOUND_EFFECT_BUTTON.get());
        player.gameEvent(GameEvent.BLOCK_ACTIVATE);
        if (soundEffect == null && getBlock() instanceof AbstractSoundEffectButtonBlock block) {
            player.level().playSound(
                    player,
                    player,
                    block.getClickOnSoundEvent(),
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F);
        }

        if (soundEffect != null) {
            playSound(player, slotReference, soundEffect, isGlobal);
        }

        pressButton(player, slotReference);
    }

    public void onInventoryUnpress(ItemStack stack, Player player, SlotReference slotReference) {
        if (!PressedSoundEffectButtonsManager.isPressed(player, slotReference)) {
            player.gameEvent(GameEvent.BLOCK_DEACTIVATE);

            if (!player.level().isClientSide
                    && getSoundEffectId(stack) == null
                    && getBlock() instanceof AbstractSoundEffectButtonBlock block) {
                player.level().playSound(
                        null,
                        player,
                        block.getClickOffSoundEvent(),
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F);
            }
        } else {
            if (!player.level().isClientSide) {
                PressedSoundEffectButtonsManager.setPressedTicksRemaining(player, slotReference, 0);
            }
        }

        if (!player.level().isClientSide) {
            CRPacketHandler.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                    new UpdatePressedSoundEffectButtonPacket(
                            player.getUUID(),
                            slotReference,
                            false));
        }
    }

    public void pressButton(Player player, SlotReference slotReference) {
        if (player instanceof ServerPlayer serverPlayer) {
            PressedSoundEffectButtonsManager.setPressedTicksRemaining(
                    player,
                    slotReference,
                    AbstractSoundEffectButtonBlock.TICKS_TO_STAY_PRESSED);

            CRPacketHandler.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY.with(() -> player),
                    new UpdatePressedSoundEffectButtonPacket(
                            player.getUUID(),
                            slotReference,
                            true));

            new PressedCallback(serverPlayer, slotReference).add();
        } else {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPressedSoundEffectButtonsManager.setPressed(player, slotReference, true));
        }
    }

    public boolean isGlobal(Player player, SlotReference slotReference) {
        return slotReference.getHoldingSlotReference()
                .flatMap(holdingReference -> holdingReference.getStackForPlayer(player))
                .map(stack -> stack.is(CRItemTags.GLOBAL_SOUND_BOARDS))
                .orElse(false);
    }

    public void playSound(Player player, SlotReference slotReference, SoundEvent soundEffect, boolean isGlobal) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (isGlobal) {
                CRPacketHandler.INSTANCE.send(CRPacketHandler.ALL_BUT_PLAYER.with(() -> serverPlayer),
                        new PlaySoundEffectButtonInventoryPacket(
                                player.getUUID(),
                                slotReference,
                                soundEffect,
                                true));
            } else {
                CRPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        serverPlayer,
                        player.getX(), player.getY(), player.getZ(),
                        64.0D,
                        player.level().dimension()
                )), new PlaySoundEffectButtonInventoryPacket(
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

    public void stopSound(Player player, SlotReference slotReference, boolean isGlobal) {
        if (!player.level().isClientSide()) {
            if (isGlobal) {
                CRPacketHandler.INSTANCE.send(
                        PacketDistributor.ALL.noArg(),
                        new PlaySoundEffectButtonInventoryPacket(
                                player.getUUID(),
                                slotReference,
                                true));
            } else {
                CRPacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(player.level()::dimension),
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
                SoundEffectButtonItem.this.stopSound(player, slotReference, isGlobal);
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
                if (CRUtil.isButtonPressed(player, slotReference)) {
                    SoundEffectButtonItem.this.onInventoryUnpress(stack, player, slotReference);
                }
            } else {
                CRPacketHandler.INSTANCE.send(
                        PacketDistributor.ALL.noArg(),
                        new UpdatePressedSoundEffectButtonPacket(
                                playerId,
                                slotReference,
                                false));
            }
        }
    }
}
