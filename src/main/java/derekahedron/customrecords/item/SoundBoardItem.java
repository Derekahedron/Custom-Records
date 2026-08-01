package derekahedron.customrecords.item;

import derekahedron.customrecords.inventory.SoundBoardMenu;
import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.OpenSoundBoardPacket;
import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.slotreference.SlotReference;
import derekahedron.customrecords.util.slotreference.SlotReferenceEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SoundBoardItem extends Item {

    public static final int MAX_DISPLAYED_BUTTONS = 5;

    public SoundBoardItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {

            private final LazyOptional<IItemHandler> handler =
                    LazyOptional.of(() -> new SoundBoardItemHandler(stack));

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
                return ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, handler);
            }
        };
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        SlotReference slotReference = CRUtil.getSlotReference(player, hand);
        if (stack != slotReference.getStackForPlayer(player).orElse(null)) {
            return InteractionResultHolder.fail(stack);
        }

        SoundBoardItemHandler handler = slotReference.getStackForPlayer(player)
                .flatMap(this::getHandler)
                .orElse(null);
        if (handler == null) return InteractionResultHolder.pass(stack);

        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(
                    serverPlayer,
                    new SimpleMenuProvider(
                            (containerId, inventory, p) ->
                                    new SoundBoardMenu(containerId, inventory, slotReference),
                            stack.getHoverName()),
                    buffer -> SlotReference.toNetwork(buffer, slotReference));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
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

        SlotReference slotReference = SlotReferenceEvent.getSlotReference(player, slot.getItem()).orElse(null);
        if (slotReference == null) return false;

        SoundBoardItemHandler handler = slotReference.getStackForPlayer(player)
                .flatMap(this::getHandler)
                .orElse(null);
        if (handler == null) return false;

        if (player.level().isClientSide()) {
            CRPacketHandler.INSTANCE.sendToServer(new OpenSoundBoardPacket(slotReference));
        }

        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag advanced) {
        super.appendHoverText(stack, level, components, advanced);

        SoundBoardItemHandler handler = getHandler(stack).orElse(null);
        if (handler == null) return;

        // A list of all, non-empty sound effects;
        List<MutableComponent> soundEffectComponents = IntStream.range(0, handler.getSlots())
                .mapToObj(handler::getStackInSlot)
                .flatMap(buttonStack -> {
                    if (!(buttonStack.getItem() instanceof SoundEffectButtonItem item)) return Stream.empty();
                    return item.getSoundEffectTextComponent(buttonStack)
                            .stream();
                })
                .toList();

        int numSoundEffects = soundEffectComponents.size();

        soundEffectComponents.stream()
                .limit(MAX_DISPLAYED_BUTTONS)
                .forEach(component ->
                        components.add(component.withStyle(ChatFormatting.GRAY)));

        if (numSoundEffects > MAX_DISPLAYED_BUTTONS) {
            MutableComponent moreText = Component.translatable(
                    CRItems.SOUND_BOARD.get().getDescriptionId() + ".more",
                    numSoundEffects - MAX_DISPLAYED_BUTTONS);
            components.add(moreText.withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }

    public Optional<SoundBoardItemHandler> getHandler(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .filter(SoundBoardItemHandler.class::isInstance)
                .map(SoundBoardItemHandler.class::cast);
    }
}
