package derekahedron.customrecords.item;

import derekahedron.customrecords.sound.MusicTrack;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface RecordTrackHolder<T extends MusicTrack> {

    @Nullable
    ResourceLocation getMusicTrackId(ItemStack stack);

    ItemStack putMusicTrackId(ItemStack stack, @Nullable ResourceLocation trackId);

    @Nullable
    Holder.Reference<T> getMusicTrack(ResourceLocation trackId, @Nullable RegistryAccess registryAccess);

    @Nullable
    default Holder.Reference<T> getMusicTrack(ItemStack stack, @Nullable RegistryAccess registryAccess) {
        ResourceLocation trackId = getMusicTrackId(stack);
        if (trackId == null) return null;
        return getMusicTrack(trackId, registryAccess);
    }

    @Nullable
    default Holder.Reference<T> getMusicTrack(ItemStack stack, @Nullable Level level) {
        return level != null ? getMusicTrack(stack, level.registryAccess()) : null;
    }

    @Nullable
    default Holder.Reference<T> getMusicTrack(ResourceLocation trackId, @Nullable Level level) {
        return level != null ? getMusicTrack(trackId, level.registryAccess()) : null;
    }

    static boolean hasSameTrack(ItemStack stack1, ItemStack stack2, @Nullable RegistryAccess registryAccess) {
        if (stack1.getItem() == stack2.getItem() && stack1.getItem() instanceof RecordTrackHolder<?> item) {
            Holder.Reference<?> track1 = item.getMusicTrack(stack1, registryAccess);
            Holder.Reference<?> track2 = item.getMusicTrack(stack2, registryAccess);
            return track1 == track2;
        }

        return false;
    }

    String getDescriptionId(ResourceLocation trackId);

    String getModelFolder();
}
