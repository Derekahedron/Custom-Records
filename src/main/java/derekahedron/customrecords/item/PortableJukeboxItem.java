package derekahedron.customrecords.item;

import derekahedron.customrecords.client.util.ClientJukeboxPlaybackManager;
import derekahedron.customrecords.inventory.PortableJukeboxMenu;
import derekahedron.customrecords.util.*;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class PortableJukeboxItem extends OpenableItem {

    public static final int MAX_DISPLAYED_RECORDS = 5;

    public PortableJukeboxItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {

            private final LazyOptional<IItemHandler> handler =
                    LazyOptional.of(() -> new PortableJukeboxItemHandler(stack));

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
                return ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, handler);
            }
        };
    }

    @Override
    public MenuConstructor getMenuConstructor(SlotReference slotReference) {
        return (containerId, inventory, player) ->
                new PortableJukeboxMenu(containerId, inventory, slotReference);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag advanced) {
        super.appendHoverText(stack, level, components, advanced);
        if (level == null) return;

        PortableJukeboxItemHandler handler = getHandler(stack).orElse(null);
        if (handler == null) return;

        boolean playing = DistExecutor.unsafeRunForDist(
                () -> () -> ClientJukeboxPlaybackManager.isPlaying(stack),
                () -> () -> false);

        int start = 0;
        int[] playOrder = handler.getPlayOrder();
        for (int i = 0; i < playOrder.length; i++) {
            if (playOrder[i] == handler.selectedSlot) {
                start = i;
                break;
            }
        }

        List<MutableComponent> recordNames = IntStream.concat(
                        Arrays.stream(playOrder).skip(start),
                        Arrays.stream(playOrder).limit(start))
                .mapToObj(slot -> handler.getTrack(slot, level.registryAccess())
                        .map(track -> playing && slot == handler.selectedSlot
                                ? Component.translatable("record.nowPlaying", track.displayName())
                                .withStyle(CRUtil::animatedColor)
                                : track.displayName().copy().withStyle(ChatFormatting.GRAY)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        recordNames.stream()
                .limit(MAX_DISPLAYED_RECORDS)
                .forEach(components::add);

        if (recordNames.size() > MAX_DISPLAYED_RECORDS) {
            MutableComponent moreText = Component.translatable(
                    getDescriptionId() + ".more",
                    recordNames.size() - MAX_DISPLAYED_RECORDS);
            components.add(moreText.withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }

    @Override
    public Optional<PortableJukeboxItemHandler> getHandler(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .filter(PortableJukeboxItemHandler.class::isInstance)
                .map(PortableJukeboxItemHandler.class::cast);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onDestroyed(ItemEntity itemEntity) {
        getHandler(itemEntity.getItem())
                .ifPresent(handler ->
                        ItemUtils.onContainerDestroyed(
                                itemEntity,
                                handler.records.stream().map(ItemStack::copy)));
    }

    public boolean isGlobal() {
        return false;
    }
}
