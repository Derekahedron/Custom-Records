package derekahedron.customrecords.client.sound;

import derekahedron.customrecords.client.util.CRClientUtil;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.concurrent.CompletableFuture;

public class SeekingEntityBoundSoundInstance extends EntityBoundSoundInstance {

    public final float offsetSeconds;

    public SeekingEntityBoundSoundInstance(
            SoundEvent soundEvent,
            SoundSource source,
            float volume,
            float pitch,
            Entity entity,
            long seed,
            float offsetSeconds) {
        super(soundEvent, source, volume, pitch, entity, seed);
        this.offsetSeconds = offsetSeconds;
    }

    @Override
    public WeighedSoundEvents resolve(SoundManager manager) {
        WeighedSoundEvents events = super.resolve(manager);
        sound = CRClientUtil.streaming(sound);
        return events;
    }

    @Override
    public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
        return CRClientUtil.seek(super.getStream(soundBuffers, sound, looping), offsetSeconds);
    }
}
