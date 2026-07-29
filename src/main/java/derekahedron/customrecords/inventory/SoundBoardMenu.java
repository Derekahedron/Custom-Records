package derekahedron.customrecords.inventory;

import com.mojang.datafixers.util.Pair;
import derekahedron.customrecords.item.SoundBoardItemHandler;
import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nullable;

public class SoundBoardMenu extends AbstractContainerMenu {

    public static final ResourceLocation EMPTY_BUTTON_SLOT_TEXTURE =
            CRUtil.location("slot/sound_effect_button");
    public static final int BORDER = 3;
    public static final int PADDING = 4;
    public static final int FONT_PADDING_TOP = 3;
    public static final int FONT_PADDING_BOTTOM = 2;
    public static final int FONT_HEIGHT = 9;
    public static final int SLOT_BORDER = 1;
    public static final int ITEM_SIZE = 16;
    public static final int SLOT_SIZE = ITEM_SIZE + SLOT_BORDER * 2;
    public static final int WIDTH = 176;
    public static final int HEIGHT = 150;
    public static final int NUM_CONTAINER_SLOTS = SoundBoardItemHandler.NUM_BUTTON_SLOTS;

    public final SlotReference slotReference;
    public final SoundBoardContainer container;

    public SoundBoardMenu(int containerId, Inventory inventory, SlotReference slotReference) {
        super(CRMenuTypes.SOUND_BOARD.get(), containerId);
        this.slotReference = slotReference;
        this.container = new SoundBoardContainer(inventory.player, slotReference);

        addSoundEffectButtonSlots();
        addInventorySlots(inventory);
    }

    public void addSoundEffectButtonSlots() {
        int x = BORDER + PADDING;
        int y = BORDER + FONT_PADDING_TOP + FONT_HEIGHT + FONT_PADDING_BOTTOM;

        for (int row = 0; row < SoundBoardItemHandler.NUM_BUTTON_ROWS; row++) {
            for (int col = 0; col < SoundBoardItemHandler.NUM_BUTTON_COLUMNS; col++) {
                addSlot(new SoundEffectButtonSlot(
                        container,
                        col + row * SoundBoardItemHandler.NUM_BUTTON_COLUMNS,
                        x + SLOT_BORDER + col * SLOT_SIZE,
                        y + SLOT_BORDER + row * SLOT_SIZE));
            }
        }
    }

    public void addInventorySlots(Inventory inventory) {
        int x = BORDER + PADDING;
        int y = HEIGHT - BORDER - PADDING - SLOT_SIZE - PADDING;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new InventorySlot(inventory, col + (row + 1) * SoundBoardItemHandler.NUM_BUTTON_COLUMNS,
                        x + SLOT_BORDER + col * SLOT_SIZE,
                        y + SLOT_BORDER - (3 - row) * SLOT_SIZE));
            }
        }

        y = HEIGHT - BORDER - PADDING - SLOT_SIZE;
        for (int k = 0; k < 9; ++k) {
            addSlot(new InventorySlot(inventory, k,
                    x + SLOT_BORDER + k * SLOT_SIZE,
                    y + SLOT_BORDER));
        }
    }

    /**
     * The slot used by the sound board, restricted to what the sound board can accept.
     */
    private static class SoundEffectButtonSlot extends Slot {

        public SoundEffectButtonSlot(Container container, int slotIndex, int x, int y) {
            super(container, slotIndex, x, y);
        }

        @Override
        public Pair<ResourceLocation, ResourceLocation>  getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_BUTTON_SLOT_TEXTURE);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return container.canPlaceItem(getSlotIndex(), stack);
        }
    }

    private class InventorySlot extends Slot {

        public InventorySlot(Container container, int slotIndex, int x, int y) {
            super(container, slotIndex, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
            return slotReference.getStackForPlayer(player)
                    .map(stack -> stack != getItem())
                    .orElse(true);
        }
    }

    @Nullable
    public static SoundBoardMenu readItem(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        SlotReference slotReference = SlotReference.fromNetwork(extraData);

        return slotReference.getStackForPlayer(inventory.player)
                .flatMap(stack -> stack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve())
                .filter(SoundBoardItemHandler.class::isInstance)
                .map(SoundBoardItemHandler.class::cast)
                .map(handler -> new SoundBoardMenu(containerId, inventory, slotReference))
                .orElse(null);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        int menuSize = NUM_CONTAINER_SLOTS + 36;

        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (slotIndex < NUM_CONTAINER_SLOTS) {
            if (!moveItemStackTo(stack, NUM_CONTAINER_SLOTS, menuSize, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(stack, 0, NUM_CONTAINER_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);

        return copy;
    }
}
