package derekahedron.customrecords.recipe;

import derekahedron.customrecords.CustomRecords;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CRRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CustomRecords.MOD_ID);

    public static final RegistryObject<RecipeSerializer<CopyGoldenRecordRecipe>> COPY_GOLDEN_RECORD =
            RECIPE_SERIALIZERS.register("copy_golden_record", CopyGoldenRecordRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<CustomMusicDiscRecipe>> CUSTOM_MUSIC_DISC =
            RECIPE_SERIALIZERS.register("custom_music_disc", CustomMusicDiscRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<DyeSoundEffectButtonRecipe>> DYE_SOUND_EFFECT_BUTTON =
            RECIPE_SERIALIZERS.register("dye_sound_effect_button", DyeSoundEffectButtonRecipe.Serializer::new);
}
