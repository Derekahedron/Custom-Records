package derekahedron.customrecords.item;

import derekahedron.customrecords.sound.JukeboxTrack;
import derekahedron.customrecords.sound.JukeboxTrackEvent;
import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.JukeboxPlayback;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.*;

public class PortableJukeboxItemHandler implements IItemHandlerModifiable {

    public static final String TAG_PORTABLE_JUKEBOX = "PortableJukebox";
    public static final String TAG_SHUFFLE_SEED = "ShuffleSeed";
    public static final String TAG_SELECTED_SLOT = "SelectedSlot";
    public static final String TAG_TICK_COUNT = "TickCount";
    public static final String TAG_AUTOPLAY =  "Autoplay";
    public static final int NUM_DISC_ROWS = 3;
    public static final int NUM_RECORD_COLUMNS = 9;
    public static final int NUM_RECORD_SLOTS = NUM_DISC_ROWS * NUM_RECORD_COLUMNS;

    public final ItemStack stack;
    public final NonNullList<ItemStack> records;
    @Nullable
    public Long shuffleSeed;
    public int selectedSlot = -1;
    public int tickCount;
    public boolean isAutoplay;

    public PortableJukeboxItemHandler(ItemStack stack) {
        this.stack = stack;
        this.records = NonNullList.withSize(NUM_RECORD_SLOTS, ItemStack.EMPTY);

        CompoundTag tag = stack.getTagElement(TAG_PORTABLE_JUKEBOX);
        if (tag != null) {
            ContainerHelper.loadAllItems(tag, records);

            if (tag.contains(TAG_SELECTED_SLOT, Tag.TAG_INT)) {
                selectedSlot = tag.getInt(TAG_SELECTED_SLOT);
            }

            if (tag.contains(TAG_SHUFFLE_SEED, Tag.TAG_LONG)) {
                shuffleSeed = tag.getLong(TAG_SHUFFLE_SEED);
            }

            if (tag.contains(TAG_TICK_COUNT, Tag.TAG_INT)) {
                tickCount = tag.getInt(TAG_TICK_COUNT);
            }

            if (tag.contains(TAG_AUTOPLAY, Tag.TAG_BYTE)) {
                isAutoplay = tag.getBoolean(TAG_AUTOPLAY);
            }
        }
    }

    public static Optional<PortableJukeboxItemHandler> getHandler(Player player, SlotReference slotReference) {
        return slotReference.getStackForPlayer(player)
                .flatMap(stack -> stack.getItem() instanceof PortableJukeboxItem item
                        ? item.getHandler(stack)
                        : Optional.empty());
    }

    public ItemStack getSelectedStack() {
        return getStackInSlot(selectedSlot);
    }

    public boolean isGlobal() {
        return (stack.getItem() instanceof PortableJukeboxItem item && item.isGlobal());
    }

    public Optional<JukeboxPlayback> createNewPlayback(RegistryAccess registries, int tickCount) {
        return getCurrentTrack(registries)
                .map(track -> new JukeboxPlayback(
                        selectedSlot,
                        getSelectedStack().copy(),
                        System.currentTimeMillis() + CRUtil.ticksToMillis(track.lengthInTicks() - tickCount),
                        isGlobal()));
    }

    public Optional<JukeboxTrack> getTrack(int slot, RegistryAccess registryAccess) {
        ItemStack stack = getStackInSlot(slot);
        if (!isItemValid(slot, stack)) return Optional.empty();
        return JukeboxTrackEvent.getJukeboxTrack(
                stack,
                registryAccess);
    }

    public Optional<JukeboxTrack> getCurrentTrack(RegistryAccess registries) {
        return getTrack(selectedSlot, registries);
    }

    public boolean isShuffled() {
        return shuffleSeed != null;
    }

    public int[] getPlayOrder() {
        int numSlots = getSlots();
        int[] slots = new int[numSlots];
        for (int slot = 0; slot < numSlots; slot++) {
            slots[slot] = slot;
        }

        if (shuffleSeed == null) return slots;

        RandomSource random = RandomSource.create(shuffleSeed);
        for (int slot = numSlots; slot > 1; slot--) {
            int other = random.nextInt(slot);
            int id = slots[slot - 1];
            slots[slot - 1] = slots[other];
            slots[other] = id;
        }

        return slots;
    }

    public int getSlotAtOffset(int slot, int offset, @Nullable RegistryAccess registryAccess) {
        int slots = getSlots();
        slot = Math.floorMod(slot, slots);

        int[] playOrder = getPlayOrder();
        List<Integer> validSlots = new ArrayList<>(slots);

        int start = 0;
        for (int i = 0; i < slots; i++) {
            int otherSlot = playOrder[i];
            boolean isValid = isItemValid(otherSlot, getStackInSlot(otherSlot))
                    && (registryAccess == null || getTrack(otherSlot, registryAccess).isPresent());
            if (slot == playOrder[i]) {
                start = validSlots.size();
                if (!isValid && offset > 0) {
                    offset--;
                }
            }
            if (isValid) {
                validSlots.add(otherSlot);
            }
        }

        if (validSlots.isEmpty()) {
            return -1;
        }

        return validSlots.get(Math.floorMod(start + offset, validSlots.size()));
    }

    @Override
    public int getSlots() {
        return NUM_RECORD_SLOTS;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    public int getStackLimit(int slot, ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot < 0) return false;
        else if (slot >= getSlots()) return false;
        else return isValid(stack) && stack.getItem().canFitInsideContainerItems();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= getSlots()) return ItemStack.EMPTY;
        return records.get(slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (!stack.isEmpty() && !isItemValid(slot, stack)) return;
        records.set(slot, stack);
        saveRecords();
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
        return records.stream().allMatch(ItemStack::isEmpty);
    }

    public void clear() {
        records.clear();
        selectedSlot = -1;
        tickCount = 0;
        save();
    }

    public void save() {
        saveRecords();
        saveShuffleSeed();
        saveAutoplay();
    }

    public void saveRecords() {
        if (isEmpty()) {
            removeJukeboxTag("Items");
            selectedSlot = -1;
            tickCount = 0;
        } else {
            ContainerHelper.saveAllItems(getOrCreateJukeboxTag(), records);

            if (!isItemValid(selectedSlot, getSelectedStack())) {
                selectedSlot = getSlotAtOffset(selectedSlot, 1, null);
                tickCount = 0;
            }
        }
        saveSelectedSlot();
        saveTickCount();
    }

    public void saveSelectedSlot() {
        if (selectedSlot != -1) {
            getOrCreateJukeboxTag().putInt(TAG_SELECTED_SLOT, selectedSlot);
        } else {
            removeJukeboxTag(TAG_SELECTED_SLOT);
        }
    }

    public void saveTickCount() {
        if (tickCount != 0) {
            getOrCreateJukeboxTag().putInt(TAG_TICK_COUNT, tickCount);
        } else {
            removeJukeboxTag(TAG_TICK_COUNT);
        }
    }

    public CompoundTag getOrCreateJukeboxTag() {
        return stack.getOrCreateTagElement(TAG_PORTABLE_JUKEBOX);
    }

    public void removeJukeboxTag(String key) {
        CompoundTag tag = stack.getTagElement(TAG_PORTABLE_JUKEBOX);
        if (tag == null) return;

        tag.remove(key);
        if (tag.isEmpty()) {
            stack.removeTagKey(TAG_PORTABLE_JUKEBOX);
        }
    }

    public void saveAutoplay() {
        if (isAutoplay) {
            getOrCreateJukeboxTag().putBoolean(TAG_AUTOPLAY, true);
        } else {
            removeJukeboxTag(TAG_AUTOPLAY);
        }
    }

    public void saveShuffleSeed() {
        if (shuffleSeed != null) {
            getOrCreateJukeboxTag().putLong(TAG_SHUFFLE_SEED, shuffleSeed);
        } else {
            removeJukeboxTag(TAG_SHUFFLE_SEED);
        }
    }

    public static boolean isValid(ItemStack stack) {
        return stack.is(ItemTags.MUSIC_DISCS);
    }
}
