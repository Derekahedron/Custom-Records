package derekahedron.customrecords.inventory;

import derekahedron.customrecords.item.SoundBoardItem;
import derekahedron.customrecords.item.SoundBoardItemHandler;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record SoundBoardContainer(Player player, SlotReference slotReference) implements Container {

    public Optional<SoundBoardItemHandler> getHandler() {
        return slotReference.getStackForPlayer(player)
                .flatMap(stack ->
                        stack.getItem() instanceof SoundBoardItem item
                                ? item.getHandler(stack)
                                : Optional.empty());
    }

    @Override
    public int getContainerSize() {
        return getHandler().map(SoundBoardItemHandler::getSlots).orElse(0);
    }

    @Override
    public ItemStack getItem(int slot) {
        return getHandler()
                .map(handler -> handler.getStackInSlot(slot))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return getHandler()
                .map(handler -> handler.isItemValid(slot, stack))
                .orElse(false);
    }

    @Override
    public boolean isEmpty() {
        return getHandler()
                .map(handler -> handler.buttons.stream().allMatch(ItemStack::isEmpty))
                .orElse(true);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (amount <= 0) return ItemStack.EMPTY;
        return getHandler()
                .map(handler -> {
                    ItemStack stack = handler.getStackInSlot(slot).copy();
                    if (stack.isEmpty()) return ItemStack.EMPTY;

                    ItemStack removedStack = stack.split(amount);
                    if (!removedStack.isEmpty()) {
                        handler.setStackInSlot(slot, stack);
                    }

                    return removedStack;
                })
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return getHandler().map(handler -> {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                handler.setStackInSlot(slot, ItemStack.EMPTY);
                return stack;
            }
        }).orElse(ItemStack.EMPTY);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        getHandler().ifPresent(handler -> {
            int limit = handler.getSlotLimit(slot);

            if (!stack.isEmpty() && stack.getCount() > limit) {
                stack.setCount(limit);
            }

            handler.setStackInSlot(slot, stack);
        });
    }

    @Override
    public void setChanged() {
        getHandler().ifPresent(SoundBoardItemHandler::save);
    }

    @Override
    public boolean stillValid(Player player) {
        return getHandler().isPresent();
    }

    @Override
    public void clearContent() {
        getHandler().ifPresent(SoundBoardItemHandler::clear);
    }
}
