package derekahedron.customrecords.item;

import derekahedron.customrecords.registry.CRRegistryKeys;
import derekahedron.customrecords.sound.MusicDiscTrack;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;

public class CustomMusicDiscItem extends CustomRecordItem<MusicDiscTrack> implements MusicDiscTrackHolder {
    public static final String MUSIC_DISC_TRACK_KEY = "MusicDiscTrack";

    public CustomMusicDiscItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable ResourceLocation getMusicTrackId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(MUSIC_DISC_TRACK_KEY)) return null;
        return ResourceLocation.tryParse(tag.getString(MUSIC_DISC_TRACK_KEY));
    }

    @Override
    public ItemStack putMusicTrackId(ItemStack stack, @Nullable ResourceLocation trackId) {
        return CRUtil.putResourceLocation(stack, MUSIC_DISC_TRACK_KEY, trackId);
    }

    @Override
    public @Nullable Holder.Reference<MusicDiscTrack> getMusicTrack(ResourceLocation trackId, @Nullable RegistryAccess registryAccess) {
        if (registryAccess == null) return null;
        return registryAccess.registryOrThrow(CRRegistryKeys.MUSIC_DISK_TRACK).getHolder(ResourceKey.create(CRRegistryKeys.MUSIC_DISK_TRACK, trackId)).orElse(null);
    }

    @Override
    public String getDescriptionId(ResourceLocation trackId) {
        return Util.makeDescriptionId("music_disc", trackId);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        Holder.Reference<MusicDiscTrack> track = getMusicTrack(stack, CRUtil.getRegistryAccess());
        if (track == null) return super.getDescriptionId(stack);
        return getDescriptionId(track.key().location());
    }

    public Rarity getRarity(ItemStack stack) {
        Holder.Reference<MusicDiscTrack> track = getMusicTrack(stack, CRUtil.getRegistryAccess());
        if (track != null && track.get().rarity().isPresent()) {
            return track.get().rarity().get();
        }
        return super.getRarity(stack);
    }

    @Override
    public String getModelFolder() {
        return "music_disc";
    }
}
