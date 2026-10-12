package derekahedron.customrecords.item;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PortableEnderJukeboxItemHandler extends PortableJukeboxItemHandler {

    public static final String TAG_SELECTED_RECORD = "SelectedRecord";

    public final Player player;

    public PortableEnderJukeboxItemHandler(ItemStack stack, Player player) {
        super(stack);
        this.player = player;
    }

    @Override
    public void saveRecords() {
        player.getCapability(PortableEnderJukeboxInventory.CAPABILITY).ifPresent(
                inventory -> inventory.setRecords(records));
    }

    @Override
    public void loadRecords() {
        records.clear();
        player.getCapability(PortableEnderJukeboxInventory.CAPABILITY).ifPresent(
                inventory -> inventory.loadRecords(records));
    }

    @Override
    public void saveTickCount() {
        super.saveTickCount();
        ItemStack selectedRecord = ItemStack.EMPTY;
        if (tickCount > 0) {
            selectedRecord = getSelectedStack();
        }
        if (!selectedRecord.isEmpty()) {
            getOrCreateJukeboxTag().put(TAG_SELECTED_RECORD, Util.make(new CompoundTag(), selectedRecord::save));
        } else {
            removeJukeboxTag(TAG_SELECTED_RECORD);
        }
    }

    @Override
    public void loadTickCount() {
        super.loadTickCount();
        if (tickCount <= 0) return;

        ItemStack selectedRecord = getSelectedStack();
        ItemStack oldSelectedRecord;
        CompoundTag tag = stack.getTagElement(TAG_PORTABLE_JUKEBOX);
        if (tag != null && tag.contains(TAG_SELECTED_RECORD, Tag.TAG_COMPOUND)) {
            oldSelectedRecord = ItemStack.of(tag.getCompound(TAG_SELECTED_RECORD));
        } else {
            oldSelectedRecord = ItemStack.EMPTY;
        }

        if (!ItemStack.matches(oldSelectedRecord, selectedRecord)) {
            tickCount = 0;
        }
    }
}
