package derekahedron.customrecords.block;

import derekahedron.customrecords.CustomRecords;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CRBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CustomRecords.MOD_ID);

    public static final RegistryObject<SoundEffectButtonBlock> WHITE_SOUND_EFFECT_BUTTON =
            BLOCKS.register("white_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.NONE)
                                    .noCollission()
                                    .strength(0.2F)
                                    .pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<SoundEffectButtonBlock> ORANGE_SOUND_EFFECT_BUTTON =
            BLOCKS.register("orange_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> MAGENTA_SOUND_EFFECT_BUTTON =
            BLOCKS.register("magenta_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> LIGHT_BLUE_SOUND_EFFECT_BUTTON =
            BLOCKS.register("light_blue_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> YELLOW_SOUND_EFFECT_BUTTON =
            BLOCKS.register("yellow_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> LIME_SOUND_EFFECT_BUTTON =
            BLOCKS.register("lime_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> PINK_SOUND_EFFECT_BUTTON =
            BLOCKS.register("pink_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> GRAY_SOUND_EFFECT_BUTTON =
            BLOCKS.register("gray_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> LIGHT_GRAY_SOUND_EFFECT_BUTTON =
            BLOCKS.register("light_gray_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> PURPLE_SOUND_EFFECT_BUTTON =
            BLOCKS.register("purple_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> CYAN_SOUND_EFFECT_BUTTON =
            BLOCKS.register("cyan_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> BLUE_SOUND_EFFECT_BUTTON =
            BLOCKS.register("blue_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> BROWN_SOUND_EFFECT_BUTTON =
            BLOCKS.register("brown_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> GREEN_SOUND_EFFECT_BUTTON =
            BLOCKS.register("green_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> RED_SOUND_EFFECT_BUTTON =
            BLOCKS.register("red_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
    public static final RegistryObject<SoundEffectButtonBlock> BLACK_SOUND_EFFECT_BUTTON =
            BLOCKS.register("black_sound_effect_button", () ->
                    new SoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));

    public static final RegistryObject<GlobalSoundEffectButtonBlock> GLOBAL_SOUND_EFFECT_BUTTON =
            BLOCKS.register("global_sound_effect_button", () ->
                    new GlobalSoundEffectButtonBlock(
                            BlockBehaviour.Properties.copy(WHITE_SOUND_EFFECT_BUTTON.get())));
}
