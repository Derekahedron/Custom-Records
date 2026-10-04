package derekahedron.customrecords.sound;

import derekahedron.customrecords.item.CustomRecordItem;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;

public record JukeboxTrack(
        SoundEvent soundEvent,
        int analogOutput,
        int lengthInTicks,
        MutableComponent displayName) {

    public static void getVanillaTrack(JukeboxTrackEvent.Get event) {
        if (event.isCanceled()) return;
        ItemStack stack = event.getStack();

        if (stack.getItem() instanceof RecordItem item) {
            event.setTrack(new JukeboxTrack(
                    item.getSound(),
                    item.getAnalogOutput(),
                    item.getLengthInTicks(),
                    item.getDisplayName()));
        }
    }

    public static void getCustomTrack(JukeboxTrackEvent.Get event) {
        if (event.isCanceled()) return;
        ItemStack stack = event.getStack();

        if (stack.getItem() instanceof CustomRecordItem<?> customRecordItem) {
            Holder.Reference<? extends MusicTrack> track = customRecordItem.getMusicTrack(stack, event.getRegistries());

            if (track == null) {
                event.setTrack(null);
            } else {
                event.setTrack(new JukeboxTrack(
                        track.get().soundEvent(),
                        track.get().analogOutput(),
                        track.get().lengthInTicks(),
                        Component.translatable(customRecordItem.getDescriptionId(track.key().location()) + ".desc")));
            }
        }
    }
}
