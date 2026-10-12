package derekahedron.customrecords.client.sound;

import net.minecraft.client.sounds.AudioStream;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface CancellableSoundInstance {

    void cancel();

    boolean isCancelled();

    default AudioStream wrapAudioStream(AudioStream audioStream) {
        return new AudioStream() {

            @Override
            public void close() throws IOException {
                audioStream.close();
            }

            @Override
            public AudioFormat getFormat() {
                return audioStream.getFormat();
            }

            @Override
            public ByteBuffer read(int bytes) throws IOException {
                if (isCancelled()) {
                    return BufferUtils.createByteBuffer(0);
                } else {
                    return audioStream.read(bytes);
                }
            }
        };
    }
}
