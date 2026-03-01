package derekahedron.customrecords.item;

import derekahedron.customrecords.registry.CRRegistryKeys;
import derekahedron.customrecords.sound.GoldenRecordTrack;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class GoldenRecordItem extends CustomRecordItem<GoldenRecordTrack> {
    public static final String GOLDEN_RECORD_TRACK_KEY = "GoldenRecordTrack";

	public GoldenRecordItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable ResourceLocation getMusicTrackId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(GOLDEN_RECORD_TRACK_KEY)) return null;
        return ResourceLocation.tryParse(tag.getString(GOLDEN_RECORD_TRACK_KEY));
    }

    @Override
    public ItemStack putMusicTrackId(ItemStack stack, @Nullable ResourceLocation trackId) {
        return CRUtil.putResourceLocation(stack, GOLDEN_RECORD_TRACK_KEY, trackId);
    }

    @Override
    public @Nullable Holder.Reference<GoldenRecordTrack> getMusicTrack(ResourceLocation trackId, @Nullable RegistryAccess registryAccess) {
        if (registryAccess == null) return null;
        return registryAccess.registryOrThrow(CRRegistryKeys.GOLDEN_RECORD_TRACK).getHolder(ResourceKey.create(CRRegistryKeys.GOLDEN_RECORD_TRACK, trackId)).orElse(null);
    }

    @Override
    public String getDescriptionId(ResourceLocation trackId) {
        return Util.makeDescriptionId("golden_record", trackId);
    }

    @Override
    public String getModelFolder() {
        return "golden_record";
    }
}
