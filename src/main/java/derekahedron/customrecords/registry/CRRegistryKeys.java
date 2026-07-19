package derekahedron.customrecords.registry;

import derekahedron.customrecords.sound.GoldenRecordTrack;
import derekahedron.customrecords.sound.MusicDiscTrack;
import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.slotreference.SlotReference;
import derekahedron.customrecords.util.slotreference.SlotReferenceSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DataPackRegistryEvent;

public class CRRegistryKeys {
    public static final ResourceKey<Registry<GoldenRecordTrack>> GOLDEN_RECORD_TRACK =
            ResourceKey.createRegistryKey(CRUtil.location("golden_record_track"));
    public static final ResourceKey<Registry<MusicDiscTrack>> MUSIC_DISK_TRACK =
            ResourceKey.createRegistryKey(CRUtil.location("music_disc_track"));
    public static final ResourceKey<Registry<SlotReferenceSerializer<? extends SlotReference>>> SLOT_REFERENCE_SERIALIZER =
            ResourceKey.createRegistryKey(CRUtil.location("slot_reference_serializer"));

    public static void init(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(GOLDEN_RECORD_TRACK, GoldenRecordTrack.CODEC, GoldenRecordTrack.CODEC);
        event.dataPackRegistry(MUSIC_DISK_TRACK, MusicDiscTrack.CODEC, MusicDiscTrack.CODEC);
    }
}
