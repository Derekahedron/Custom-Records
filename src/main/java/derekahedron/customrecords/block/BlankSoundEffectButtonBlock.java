package derekahedron.customrecords.block;

import derekahedron.customrecords.sound.CRSoundEvents;
import net.minecraft.sounds.SoundEvent;

public class BlankSoundEffectButtonBlock extends SoundEffectButtonBlock {

    public BlankSoundEffectButtonBlock(Properties properties) {
        super(properties);
    }

    @Override
    public SoundEvent getClickOnSoundEvent() {
        return CRSoundEvents.BLOCK_METAL_SOUND_EFFECT_BUTTON_CLICK_ON.get();
    }

    @Override
    public SoundEvent getClickOffSoundEvent() {
        return CRSoundEvents.BLOCK_METAL_SOUND_EFFECT_BUTTON_CLICK_OFF.get();
    }
}
