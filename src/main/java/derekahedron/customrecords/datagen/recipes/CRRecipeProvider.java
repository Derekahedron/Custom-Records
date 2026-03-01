package derekahedron.customrecords.datagen.recipes;

import derekahedron.customrecords.item.CRItemTags;
import derekahedron.customrecords.item.CRItems;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import derekahedron.customrecords.recipe.CopyGoldenRecordRecipe;
import derekahedron.customrecords.recipe.CustomMusicDiscRecipe;
import derekahedron.customrecords.recipe.DyeSoundEffectButtonRecipe;

import java.util.Objects;
import java.util.function.Consumer;

public class CRRecipeProvider extends RecipeProvider {

    public CRRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        new CopyGoldenRecordRecipe.Builder(RecipeCategory.MISC,
                CRItems.SILVER_RECORD.get(),
                Ingredient.of(CRItems.GOLDEN_RECORD.get()),
                Ingredient.of(CRItems.BLANK_SILVER_RECORD.get()))
                .save(consumer, CRUtil.location("copy_golden_record"));

        new CustomMusicDiscRecipe.Builder(RecipeCategory.MISC,
                CRItems.CUSTOM_MUSIC_DISC.get(),
                Ingredient.of(CRItems.CUSTOM_MUSIC_DISC_FRAGMENT.get()))
                .save(consumer, CRUtil.location("custom_music_disc"));

        dyeSoundEffectButton(consumer, CRItems.WHITE_SOUND_EFFECT_BUTTON.get(), Items.WHITE_DYE);
        dyeSoundEffectButton(consumer, CRItems.ORANGE_SOUND_EFFECT_BUTTON.get(), Items.ORANGE_DYE);
        dyeSoundEffectButton(consumer, CRItems.MAGENTA_SOUND_EFFECT_BUTTON.get(), Items.MAGENTA_DYE);
        dyeSoundEffectButton(consumer, CRItems.LIGHT_BLUE_SOUND_EFFECT_BUTTON.get(), Items.LIGHT_BLUE_DYE);
        dyeSoundEffectButton(consumer, CRItems.YELLOW_SOUND_EFFECT_BUTTON.get(), Items.YELLOW_DYE);
        dyeSoundEffectButton(consumer, CRItems.LIME_SOUND_EFFECT_BUTTON.get(), Items.LIME_DYE);
        dyeSoundEffectButton(consumer, CRItems.PINK_SOUND_EFFECT_BUTTON.get(), Items.PINK_DYE);
        dyeSoundEffectButton(consumer, CRItems.GRAY_SOUND_EFFECT_BUTTON.get(), Items.GRAY_DYE);
        dyeSoundEffectButton(consumer, CRItems.LIGHT_GRAY_SOUND_EFFECT_BUTTON.get(), Items.LIGHT_GRAY_DYE);
        dyeSoundEffectButton(consumer, CRItems.PURPLE_SOUND_EFFECT_BUTTON.get(), Items.PURPLE_DYE);
        dyeSoundEffectButton(consumer, CRItems.CYAN_SOUND_EFFECT_BUTTON.get(), Items.CYAN_DYE);
        dyeSoundEffectButton(consumer, CRItems.BLUE_SOUND_EFFECT_BUTTON.get(), Items.BLUE_DYE);
        dyeSoundEffectButton(consumer, CRItems.BROWN_SOUND_EFFECT_BUTTON.get(), Items.BROWN_DYE);
        dyeSoundEffectButton(consumer, CRItems.GREEN_SOUND_EFFECT_BUTTON.get(), Items.GREEN_DYE);
        dyeSoundEffectButton(consumer, CRItems.RED_SOUND_EFFECT_BUTTON.get(), Items.RED_DYE);
        dyeSoundEffectButton(consumer, CRItems.BLACK_SOUND_EFFECT_BUTTON.get(), Items.BLACK_DYE);
    }

    public static void dyeSoundEffectButton(Consumer<FinishedRecipe> consumer, ItemLike soundEffectButton, ItemLike dye) {
        new DyeSoundEffectButtonRecipe.Builder(RecipeCategory.REDSTONE,
                soundEffectButton,
                Ingredient.of(CRItemTags.DYEABLE_SOUND_EFFECT_BUTTONS),
                Ingredient.of(dye))
                .group("sound_effect_button_dye")
                .unlockedBy(
                        getHasName(dye),
                        has(dye))
                .save(consumer, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(soundEffectButton.asItem())));
    }
}
