package derekahedron.customrecords.item;

import derekahedron.customrecords.registry.CRRegistryKeys;
import derekahedron.customrecords.sound.GoldenRecordTrack;
import derekahedron.customrecords.sound.MusicDiscTrack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import java.util.List;

@SuppressWarnings("unused")
public class CRCreativeTabs {
    public static void initialize(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            CustomMusicDiscItem customMusicDisc = CRItems.CUSTOM_MUSIC_DISC.get();
            GoldenRecordItem goldenRecord = CRItems.GOLDEN_RECORD.get();
            SilverRecordItem silverRecord = CRItems.SILVER_RECORD.get();
            ItemStack lastStack = new ItemStack(Items.MUSIC_DISC_RELIC);
            HolderLookup.Provider lookup = event.getParameters().holders();
            List<Holder.Reference<MusicDiscTrack>> musicDiscTracks = lookup.lookupOrThrow(CRRegistryKeys.MUSIC_DISK_TRACK)
                    .listElements()
                    .filter(reference -> reference.get().inCreativeInventory())
                    .toList();
            List<Holder.Reference<GoldenRecordTrack>> goldenRecordTracks = lookup.lookupOrThrow(CRRegistryKeys.GOLDEN_RECORD_TRACK)
                    .listElements()
                    .filter(reference -> reference.get().inCreativeInventory())
                    .toList();

            for (Holder.Reference<MusicDiscTrack> track : musicDiscTracks) {
                ResourceLocation trackId = track.key().location();
                ItemStack stack = customMusicDisc.putMusicTrackId(new ItemStack(customMusicDisc), trackId);
                event.getEntries().putAfter(lastStack, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                lastStack = stack;
            }

            for (Holder.Reference<GoldenRecordTrack> track : goldenRecordTracks) {
                ResourceLocation trackId = track.key().location();
                ItemStack stack = goldenRecord.putMusicTrackId(new ItemStack(goldenRecord), trackId);
                event.getEntries().putAfter(lastStack, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                lastStack = stack;
            }

            if (!goldenRecordTracks.isEmpty()) {
                ItemStack stack = new ItemStack(CRItems.BLANK_SILVER_RECORD.get());
                event.getEntries().putAfter(lastStack, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                lastStack = stack;
            }

            for (Holder.Reference<GoldenRecordTrack> track : goldenRecordTracks) {
                ResourceLocation trackId = track.key().location();
                ItemStack stack = silverRecord.putMusicTrackId(new ItemStack(silverRecord), trackId);
                event.getEntries().putAfter(lastStack, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                lastStack = stack;
            }
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            CustomMusicDiscFragmentItem customMusicDiscFragment = CRItems.CUSTOM_MUSIC_DISC_FRAGMENT.get();
            ItemStack lastStack = new ItemStack(Items.DISC_FRAGMENT_5);
            HolderLookup.Provider lookup = event.getParameters().holders();
            List<Holder.Reference<MusicDiscTrack>> musicDiscTracks = lookup.lookupOrThrow(CRRegistryKeys.MUSIC_DISK_TRACK)
                    .listElements()
                    .filter(reference -> reference.get().inCreativeInventory())
                    .toList();

            for (Holder.Reference<MusicDiscTrack> track : musicDiscTracks) {
                ResourceLocation trackId = track.key().location();
                ItemStack stack = customMusicDiscFragment.putMusicTrackId(new ItemStack(customMusicDiscFragment), trackId);
                event.getEntries().putAfter(lastStack, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                lastStack = stack;
            }
        }
    }
}
