package derekahedron.customrecords.client.sound;

import derekahedron.customrecords.client.util.CRClientUtil;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.concurrent.CompletableFuture;

public class SeekingSimpleSoundInstance extends SimpleSoundInstance {

    public final float offsetSeconds;

    public SeekingSimpleSoundInstance(
            ResourceLocation location,
            SoundSource source,
            float volume,
            float pitch,
            RandomSource random,
            boolean looping,
            int delay,
            Attenuation attenuation,
            double x,
            double y,
            double z,
            boolean relative,
            float offsetSeconds) {
        super(location, source, volume, pitch, random, looping, delay, attenuation, x, y, z, relative);
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
