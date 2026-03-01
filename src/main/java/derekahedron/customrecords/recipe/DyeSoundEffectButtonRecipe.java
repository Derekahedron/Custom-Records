package derekahedron.customrecords.recipe;

import derekahedron.customrecords.item.SoundEffectButtonItem;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.CraftingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class DyeSoundEffectButtonRecipe implements CraftingRecipe {
    public final ResourceLocation id;
    public final String group;
    public final CraftingBookCategory category;
    public final Ingredient input;
    public final Ingredient material;
    public final ItemStack result;

    public DyeSoundEffectButtonRecipe(
            ResourceLocation id,
            String group,
            CraftingBookCategory category,
            Ingredient input,
            Ingredient material,
            ItemStack result) {
        this.id = id;
        this.group = group;
        this.category = category;
        this.input = DifferenceIngredient.of(input, Ingredient.of(result));
        this.material = material;
        this.result = result;
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        ItemStack baseButton = null;
        ItemStack dye = null;

        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
            if (stack.isEmpty()) continue;

            if (input.test(stack)) {
                if (baseButton != null) return false;
                baseButton = stack;
            } else if (material.test(stack)) {
                if (dye != null) return false;
                dye = stack;
            } else {
                return false;
            }
        }
        return baseButton != null && dye != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        ItemStack baseButton = null;

        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
            if (stack.isEmpty()) continue;

            if (input.test(stack)) {
                baseButton = stack;
                break;
            }
        }

        if (baseButton == null || !(baseButton.getItem() instanceof SoundEffectButtonItem item)) return ItemStack.EMPTY;
        ResourceLocation trackId = item.getSoundEffectId(baseButton);

        ItemStack resultButton = result.copy();
        item.putSoundEffectId(resultButton, trackId);
        return resultButton;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input, material);
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<DyeSoundEffectButtonRecipe> getSerializer() {
        return CRRecipeSerializers.DYE_SOUND_EFFECT_BUTTON.get();
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    public static class Serializer implements RecipeSerializer<DyeSoundEffectButtonRecipe> {

        public DyeSoundEffectButtonRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory category = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", null), CraftingBookCategory.MISC);
            Ingredient input = Ingredient.fromJson(json.get("input"), false);
            Ingredient material = Ingredient.fromJson(json.get("material"), false);
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new DyeSoundEffectButtonRecipe(id, group, category, input, material, result);
        }

        public DyeSoundEffectButtonRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            Ingredient input = Ingredient.fromNetwork(buf);
            Ingredient material = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            return new DyeSoundEffectButtonRecipe(id, group, category, input, material, result);
        }

        public void toNetwork(FriendlyByteBuf buf, DyeSoundEffectButtonRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            recipe.input.toNetwork(buf);
            recipe.material.toNetwork(buf);
            buf.writeItem(recipe.result);
        }
    }

    public static class Builder extends CraftingRecipeBuilder implements RecipeBuilder {
        public final RecipeCategory category;
        public final Item result;
        public final Ingredient input;
        public final Ingredient material;
        public final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
        @Nullable
        public String group;

        public Builder(RecipeCategory category, ItemLike result, Ingredient input, Ingredient material) {
            this.category = category;
            this.result = result.asItem();
            this.input = input;
            this.material = material;
        }

        public Builder unlockedBy(String p_126133_, CriterionTriggerInstance p_126134_) {
            advancement.addCriterion(p_126133_, p_126134_);
            return this;
        }

        public Builder group(@Nullable String group) {
            this.group = group;
            return this;
        }

        public Item getResult() {
            return result;
        }

        public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
            advancement.parent(ROOT_RECIPE_ADVANCEMENT)
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(RequirementsStrategy.OR);
            consumer.accept(new Result(
                    id,
                    result,
                    group == null ? "" : group,
                    determineBookCategory(category),
                    input,
                    material,
                    this.advancement, id.withPrefix("recipes/" + this.category.getFolderName() + "/")
            ));
        }

        public static class Result extends CraftingRecipeBuilder.CraftingResult {
            private final ResourceLocation id;
            private final Item result;
            private final String group;
            public final Ingredient input;
            public final Ingredient material;
            private final Advancement.Builder advancement;
            private final ResourceLocation advancementId;

            public Result(ResourceLocation id, Item result, String group, CraftingBookCategory category, Ingredient input, Ingredient material, Advancement.Builder advancement, ResourceLocation advancementId) {
                super(category);
                this.id = id;
                this.result = result;
                this.group = group;
                this.input = input;
                this.material = material;
                this.advancement = advancement;
                this.advancementId = advancementId;
            }

            public void serializeRecipeData(JsonObject json) {
                super.serializeRecipeData(json);
                if (!group.isEmpty()) {
                    json.addProperty("group", this.group);
                }

                json.add("input", input.toJson());
                json.add("material", material.toJson());

                JsonObject resultJson = new JsonObject();
                resultJson.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result)).toString());
                json.add("result", resultJson);
            }

            public RecipeSerializer<DyeSoundEffectButtonRecipe> getType() {
                return CRRecipeSerializers.DYE_SOUND_EFFECT_BUTTON.get();
            }

            public ResourceLocation getId() {
                return this.id;
            }

            @Nullable
            public JsonObject serializeAdvancement() {
                return this.advancement.serializeToJson();
            }

            @Nullable
            public ResourceLocation getAdvancementId() {
                return this.advancementId;
            }
        }
    }
}
