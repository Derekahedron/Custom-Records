package derekahedron.customrecords.registry;

import derekahedron.customrecords.sound.GoldenRecordTrack;
import derekahedron.customrecords.sound.MusicDiscTrack;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DataPackRegistryEvent;

public class CRRegistryKeys {
    public static final ResourceKey<Registry<GoldenRecordTrack>> GOLDEN_RECORD_TRACK =
            ResourceKey.createRegistryKey(CRUtil.location("golden_record_track"));
    public static final ResourceKey<Registry<MusicDiscTrack>> MUSIC_DISK_TRACK =
            ResourceKey.createRegistryKey(CRUtil.location("music_disc_track"));

    public static void initialize(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(GOLDEN_RECORD_TRACK, GoldenRecordTrack.CODEC, GoldenRecordTrack.CODEC);
        event.dataPackRegistry(MUSIC_DISK_TRACK, MusicDiscTrack.CODEC, MusicDiscTrack.CODEC);
    }
}
