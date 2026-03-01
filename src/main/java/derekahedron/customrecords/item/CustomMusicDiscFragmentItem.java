package derekahedron.customrecords.item;

import derekahedron.customrecords.sound.MusicDiscTrack;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomMusicDiscFragmentItem extends Item implements MusicDiscTrackHolder {

    public CustomMusicDiscFragmentItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable ResourceLocation getMusicTrackId(ItemStack stack) {
        return CRItems.CUSTOM_MUSIC_DISC.get().getMusicTrackId(stack);
    }

    @Override
    public ItemStack putMusicTrackId(ItemStack stack, @Nullable ResourceLocation trackId) {
        return CRItems.CUSTOM_MUSIC_DISC.get().putMusicTrackId(stack, trackId);
    }

    @Override
    public @Nullable Holder.Reference<MusicDiscTrack> getMusicTrack(ResourceLocation trackId, @Nullable RegistryAccess registryAccess) {
        Holder.Reference<MusicDiscTrack> track = CRItems.CUSTOM_MUSIC_DISC.get().getMusicTrack(trackId, registryAccess);
        return track != null && track.get().hasFragment() ? track : null;
    }

    @Override
    public String getDescriptionId(ResourceLocation trackId) {
        return CRItems.CUSTOM_MUSIC_DISC.get().getDescriptionId(trackId) + ".fragment";
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        Holder.Reference<MusicDiscTrack> track = getMusicTrack(stack, CRUtil.getRegistryAccess());
        if (track == null) return super.getDescriptionId(stack);
        return getDescriptionId(track.key().location());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag advanced) {
        Holder.Reference<MusicDiscTrack> track = getMusicTrack(stack, level != null ? level.registryAccess() : CRUtil.getRegistryAccess());
        if (track == null) return;

        String descriptionId = getDescriptionId(track.key().location()) + ".desc";
        components.add(Component.translatable(descriptionId).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        Holder.Reference<MusicDiscTrack> track = getMusicTrack(stack, CRUtil.getRegistryAccess());
        if (track != null && track.get().fragmentRarity().isPresent()) {
            return track.get().fragmentRarity().get();
        }
        return super.getRarity(stack);
    }

    @Override
    public String getModelFolder() {
        return "music_disc_fragment";
    }
}
