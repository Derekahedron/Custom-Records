package derekahedron.customrecords.block.entity;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.block.CRBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CRBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CustomRecords.MOD_ID);

    public static final RegistryObject<BlockEntityType<SoundEffectButtonBlockEntity>> SOUND_EFFECT_BUTTON =
            BLOCK_ENTITY_TYPES.register("sound_effect_button", () ->
                    BlockEntityType.Builder.of(
                                    SoundEffectButtonBlockEntity::new,
                                    CRBlocks.WHITE_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.ORANGE_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.MAGENTA_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.LIGHT_BLUE_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.YELLOW_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.LIME_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.PINK_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.GRAY_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.LIGHT_GRAY_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.PURPLE_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.CYAN_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.BLUE_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.BROWN_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.GREEN_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.RED_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.BLACK_SOUND_EFFECT_BUTTON.get(),
                                    CRBlocks.GLOBAL_SOUND_EFFECT_BUTTON.get())
                            .build(null));
}
