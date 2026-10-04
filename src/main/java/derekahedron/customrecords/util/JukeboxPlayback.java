package derekahedron.customrecords.util;

import derekahedron.customrecords.item.PortableJukeboxItemHandler;
import derekahedron.customrecords.sound.JukeboxTrack;
import derekahedron.customrecords.sound.JukeboxTrackEvent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class JukeboxPlayback {
    public final int slot;
    public final ItemStack record;
    public long endTimestamp;
    public final boolean isGlobal;

    public JukeboxPlayback(int slotIndex, ItemStack record, long endTimestamp, boolean isGlobal) {
        this.slot = slotIndex;
        this.record = record;
        this.endTimestamp = endTimestamp;
        this.isGlobal = isGlobal;
    }

    public boolean isSameTrack(JukeboxPlayback playback) {
        return this.slot == playback.slot
                && ItemStack.isSameItemSameTags(this.record, playback.record);
    }

    public boolean isSameTrack(PortableJukeboxItemHandler handler) {
        return slot == handler.selectedSlot
                && ItemStack.isSameItemSameTags(record, handler.getStackInSlot(slot));
    }

    public Optional<JukeboxTrack> getTrack(RegistryAccess registries) {
        return JukeboxTrackEvent.getJukeboxTrack(record, registries);
    }

    public int getTickCount(int lengthInTicks) {
        int remaining = CRUtil.millisToTicks(endTimestamp - CRUtil.getCurrentTimeMillisForPlaybacks());
        return Mth.clamp(lengthInTicks - remaining, 0, lengthInTicks);
    }

    public boolean hasEnded() {
        return endTimestamp < CRUtil.getCurrentTimeMillisForPlaybacks();
    }

    public static void toNetwork(FriendlyByteBuf buffer, JukeboxPlayback playback) {
        buffer.writeInt(playback.slot);
        buffer.writeItem(playback.record);
        buffer.writeLong(playback.endTimestamp - CRUtil.getCurrentTimeMillisForPlaybacks());
        buffer.writeBoolean(playback.isGlobal);
    }

    public static JukeboxPlayback fromNetwork(FriendlyByteBuf buffer) {
        return new JukeboxPlayback(
                buffer.readInt(),
                buffer.readItem(),
                CRUtil.getCurrentTimeMillisForPlaybacks() + buffer.readLong(),
                buffer.readBoolean());
    }
}
