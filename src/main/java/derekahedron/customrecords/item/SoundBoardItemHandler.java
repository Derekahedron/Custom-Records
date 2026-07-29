package derekahedron.customrecords.item;

import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class SoundBoardItemHandler implements IItemHandlerModifiable {

    public static final String BUTTONS_KEY = "Buttons";
    public static final int NUM_BUTTON_ROWS = 2;
    public static final int NUM_BUTTON_COLUMNS = 9;
    public static final int NUM_BUTTON_SLOTS = NUM_BUTTON_ROWS * NUM_BUTTON_COLUMNS;
    public static final int SLOT_LIMIT = 1;

    public final ItemStack stack;
    public final NonNullList<ItemStack> buttons;

    public SoundBoardItemHandler(ItemStack stack) {
        this.stack = stack;
        this.buttons = NonNullList.withSize(NUM_BUTTON_SLOTS, ItemStack.EMPTY);

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(BUTTONS_KEY, Tag.TAG_COMPOUND)) {
            ContainerHelper.loadAllItems(tag.getCompound(BUTTONS_KEY), buttons);
        }
    }

    @Override
    public int getSlots() {
        return NUM_BUTTON_SLOTS;
    }

    @Override
    public int getSlotLimit(int slot) {
        return SLOT_LIMIT;
    }

    public int getStackLimit(int slot, ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot < 0) return false;
        else if (slot >= getSlots()) return false;
        else return stack.getItem() instanceof SoundEffectButtonItem item
                    && item.getSoundEffectId(stack) != null
                    && item.canFitInsideContainerItems();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= getSlots()) return ItemStack.EMPTY;
        return buttons.get(slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (!stack.isEmpty() && !isItemValid(slot, stack)) return;
        buttons.set(slot, stack);
        save();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (slot < 0 || slot >= getSlots()) return stack;
        if (!isItemValid(slot, stack)) return stack;

        ItemStack existing = getStackInSlot(slot).copy();
        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                return stack;
            }

            limit -= existing.getCount();
        }

        if (limit <= 0) return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                existing = stack.copy();
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            setStackInSlot(slot, existing);
        }

        return reachedLimit
                ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit)
                : ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < 0 || slot >= getSlots()) return ItemStack.EMPTY;

        ItemStack remainder = getStackInSlot(slot).copy();
        ItemStack split = remainder.split(amount);
        if (!simulate) {
            setStackInSlot(slot, remainder);
        }

        return split;
    }

    public boolean isEmpty() {
        return buttons.stream().allMatch(ItemStack::isEmpty);
    }

    public void clear() {
        buttons.clear();
        save();
    }

    public void save() {
        if (isEmpty()) {
            if (stack.getTag() == null) return;
            stack.removeTagKey(BUTTONS_KEY);
        } else {
            stack.getOrCreateTag().put(BUTTONS_KEY, Util.make(new CompoundTag(), tag ->
                    ContainerHelper.saveAllItems(tag, buttons)));
        }
    }
}
