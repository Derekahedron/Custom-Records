package derekahedron.customrecords.item;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.block.CRBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CRItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CustomRecords.MOD_ID);

    public static final RegistryObject<Item> BLANK_SILVER_RECORD =
            ITEMS.register("blank_silver_record", () ->
                    new Item(
                            new Item.Properties()
                                    .stacksTo(16)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SilverRecordItem> SILVER_RECORD =
            ITEMS.register("silver_record", () ->
                    new SilverRecordItem(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<GoldenRecordItem> GOLDEN_RECORD =
            ITEMS.register("golden_record", () ->
                    new GoldenRecordItem(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .fireResistant()
                                    .rarity(Rarity.EPIC)));

    public static final RegistryObject<CustomMusicDiscFragmentItem> CUSTOM_MUSIC_DISC_FRAGMENT =
            ITEMS.register("custom_music_disc_fragment", () ->
                    new CustomMusicDiscFragmentItem(
                            new Item.Properties()));
    public static final RegistryObject<CustomMusicDiscItem> CUSTOM_MUSIC_DISC =
            ITEMS.register("custom_music_disc", () ->
                    new CustomMusicDiscItem(
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.RARE)));

    public static final RegistryObject<SoundEffectButtonItem> WHITE_SOUND_EFFECT_BUTTON =
            ITEMS.register("white_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.WHITE_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> ORANGE_SOUND_EFFECT_BUTTON =
            ITEMS.register("orange_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.ORANGE_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> MAGENTA_SOUND_EFFECT_BUTTON =
            ITEMS.register("magenta_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.MAGENTA_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> LIGHT_BLUE_SOUND_EFFECT_BUTTON =
            ITEMS.register("light_blue_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.LIGHT_BLUE_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> YELLOW_SOUND_EFFECT_BUTTON =
            ITEMS.register("yellow_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.YELLOW_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> LIME_SOUND_EFFECT_BUTTON =
            ITEMS.register("lime_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.LIME_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> PINK_SOUND_EFFECT_BUTTON =
            ITEMS.register("pink_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.PINK_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> GRAY_SOUND_EFFECT_BUTTON =
            ITEMS.register("gray_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.GRAY_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> LIGHT_GRAY_SOUND_EFFECT_BUTTON =
            ITEMS.register("light_gray_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.LIGHT_GRAY_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> PURPLE_SOUND_EFFECT_BUTTON =
            ITEMS.register("purple_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.PURPLE_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> CYAN_SOUND_EFFECT_BUTTON =
            ITEMS.register("cyan_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.CYAN_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> BLUE_SOUND_EFFECT_BUTTON =
            ITEMS.register("blue_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.BLUE_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> BROWN_SOUND_EFFECT_BUTTON =
            ITEMS.register("brown_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.BROWN_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> GREEN_SOUND_EFFECT_BUTTON =
            ITEMS.register("green_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.GREEN_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> RED_SOUND_EFFECT_BUTTON =
            ITEMS.register("red_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.RED_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SoundEffectButtonItem> BLACK_SOUND_EFFECT_BUTTON =
            ITEMS.register("black_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.BLACK_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<SoundEffectButtonItem> GLOBAL_SOUND_EFFECT_BUTTON =
            ITEMS.register("global_sound_effect_button", () ->
                    new SoundEffectButtonItem(
                            CRBlocks.GLOBAL_SOUND_EFFECT_BUTTON.get(),
                            new Item.Properties()
                                    .stacksTo(1)
                                    .fireResistant()
                                    .rarity(Rarity.EPIC)));
}
