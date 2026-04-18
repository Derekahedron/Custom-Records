package derekahedron.customrecords.sound;

import net.minecraft.sounds.SoundEvent;

public interface MusicTrack {

    SoundEvent soundEvent();

    int analogOutput();

    int lengthInTicks();

    boolean inCreativeInventory();
}
