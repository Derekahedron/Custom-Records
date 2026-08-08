package derekahedron.customrecords.client.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on the client before a sound effect button's sound is started.
 * Cancelling prevents the sound from playing; any sound already playing for
 * the same button is left untouched.
 */
@Cancelable
public class StartSoundEffectEvent extends Event {

    private final Player player;
    private SoundEvent soundEffect;
    private SoundSource soundSource;
    private long randomSeed;
    private final boolean isGlobal;

    public StartSoundEffectEvent(
            Player player,
            SoundEvent soundEffect,
            SoundSource soundSource,
            long randomSeed,
            boolean isGlobal) {
        this.player = player;
        this.soundEffect = soundEffect;
        this.soundSource = soundSource;
        this.randomSeed = randomSeed;
        this.isGlobal = isGlobal;
    }

    public Player getPlayer() {
        return this.player;
    }

    public SoundEvent getSoundEffect() {
        return this.soundEffect;
    }

    public SoundSource getSoundSource() {
        return this.soundSource;
    }

    public long getRandomSeed() {
        return this.randomSeed;
    }

    public boolean isGlobal() {
        return this.isGlobal;
    }

    public void setSoundEffect(SoundEvent soundEffect) {
        this.soundEffect = soundEffect;
    }

    public void setSoundSource(SoundSource soundSource) {
        this.soundSource = soundSource;
    }

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }
}
