package derekahedron.customrecords.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class CRClientUtil {

    public static Sound streaming(Sound sound) {
        if (sound == SoundManager.EMPTY_SOUND
                || sound == SoundManager.INTENTIONALLY_EMPTY_SOUND
                || sound.shouldStream()) {
            return sound;
        }

        return new Sound(
                sound.getLocation().toString(),
                sound.getVolume(),
                sound.getPitch(),
                sound.getWeight(),
                sound.getType(),
                true,
                sound.shouldPreload(),
                sound.getAttenuationDistance());
    }

    public static CompletableFuture<AudioStream> seek(CompletableFuture<AudioStream> streamFuture, float offsetSeconds) {
        if (offsetSeconds <= 0) return streamFuture;

        return streamFuture.thenApply(stream -> {
            try {
                AudioFormat audioFormat = stream.getFormat();
                long startingBytes = (long) ((offsetSeconds * audioFormat.getSampleRate()) * audioFormat.getFrameSize());
                while (startingBytes > 0) {
                    int bytesToSkip = (int) Math.min(startingBytes, Integer.MAX_VALUE);
                    startingBytes -= bytesToSkip;
                    if (!stream.read(bytesToSkip).hasRemaining()) {
                        break;
                    }
                }
                return stream;
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static void playClick() {
        Minecraft.getInstance().getSoundManager()
                .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
