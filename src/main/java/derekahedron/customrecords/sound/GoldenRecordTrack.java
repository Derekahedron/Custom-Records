package derekahedron.customrecords.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record GoldenRecordTrack(
        ResourceLocation soundEvent,
        int analogOutput,
        int lengthInTicks,
        boolean inCreativeInventory
) implements MusicTrack {

    public static final Codec<GoldenRecordTrack> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC
                            .fieldOf("sound_event")
                            .forGetter(GoldenRecordTrack::soundEvent),
                    Codec.INT
                            .fieldOf("analog_output")
                            .forGetter(GoldenRecordTrack::analogOutput),
                    Codec.INT
                            .fieldOf("length_in_ticks")
                            .forGetter(GoldenRecordTrack::lengthInTicks),
                    Codec.BOOL
                            .optionalFieldOf("in_creative_inventory", true)
                            .forGetter(GoldenRecordTrack::inCreativeInventory)
            ).apply(instance, GoldenRecordTrack::new));
}
