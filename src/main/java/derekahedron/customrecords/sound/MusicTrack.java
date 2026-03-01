package derekahedron.customrecords.sound;

import net.minecraft.resources.ResourceLocation;

public interface MusicTrack {

    ResourceLocation soundEvent();

    int analogOutput();

    int lengthInTicks();

    boolean inCreativeInventory();
}
