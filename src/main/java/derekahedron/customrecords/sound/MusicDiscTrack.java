package derekahedron.customrecords.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Rarity;

import java.util.Optional;

public record MusicDiscTrack(
        SoundEvent soundEvent,
        int analogOutput,
        int lengthInTicks,
        boolean inCreativeInventory,
        Optional<Rarity> rarity,
        boolean hasFragment,
        Optional<Rarity> fragmentRarity,
        Optional<TagKey<DamageType>> immuneTo,
        boolean isFireResistant,
        boolean despawns
) implements MusicTrack {
    public static final Codec<MusicDiscTrack> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC
                            .xmap(SoundEvent::createVariableRangeEvent, SoundEvent::getLocation)
                            .fieldOf("sound_event")
                            .forGetter(MusicDiscTrack::soundEvent),
                    Codec.INT
                            .fieldOf("analog_output")
                            .forGetter(MusicDiscTrack::analogOutput),
                    Codec.INT
                            .fieldOf("length_in_ticks")
                            .forGetter(MusicDiscTrack::lengthInTicks),
                    Codec.BOOL
                            .optionalFieldOf("in_creative_inventory", true)
                            .forGetter(MusicDiscTrack::inCreativeInventory),
                    CRUtil.RARITY_CODEC
                            .optionalFieldOf("rarity")
                            .forGetter(MusicDiscTrack::rarity),
                    Codec.BOOL
                            .optionalFieldOf("has_fragment", false)
                            .forGetter(MusicDiscTrack::hasFragment),
                    CRUtil.RARITY_CODEC
                            .optionalFieldOf("fragment_rarity")
                            .forGetter(MusicDiscTrack::rarity),
                    TagKey.codec(Registries.DAMAGE_TYPE)
                            .optionalFieldOf("immune_to")
                            .forGetter(MusicDiscTrack::immuneTo),
                    Codec.BOOL
                            .optionalFieldOf("is_fire_resistant", false)
                            .forGetter(MusicDiscTrack::isFireResistant),
                    Codec.BOOL
                            .optionalFieldOf("despawns", true)
                            .forGetter(MusicDiscTrack::despawns)
            ).apply(instance, MusicDiscTrack::new));
}
