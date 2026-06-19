package derekahedron.customrecords.sound;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CRSoundEvents {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CustomRecords.MOD_ID);

    public static final RegistryObject<SoundEvent> BLOCK_METAL_SOUND_EFFECT_BUTTON_CLICK_OFF =
            register("block.metal_sound_effect_button.click_off");
    public static final RegistryObject<SoundEvent> BLOCK_METAL_SOUND_EFFECT_BUTTON_CLICK_ON =
            register("block.metal_sound_effect_button.click_on");
    public static final RegistryObject<SoundEvent> BLOCK_STONE_SOUND_EFFECT_BUTTON_CLICK_OFF =
            register("block.stone_sound_effect_button.click_off");
    public static final RegistryObject<SoundEvent> BLOCK_STONE_SOUND_EFFECT_BUTTON_CLICK_ON =
            register("block.stone_sound_effect_button.click_on");

    public static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(CRUtil.location(name)));
    }
}
