package derekahedron.customrecords.recipe;

import derekahedron.customrecords.item.RecordTrackHolder;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class CustomMusicDiscRecipe implements CraftingRecipe {
    public static final int REQUIRED_FRAGMENTS = 9;
    public final ResourceLocation id;
    public final String group;
    public final CraftingBookCategory category;
    public final Ingredient fragment;
    public final ItemStack result;

    public CustomMusicDiscRecipe(
            ResourceLocation id,
            String group,
            CraftingBookCategory category,
            Ingredient fragment,
            ItemStack result) {
        this.id = id;
        this.group = group;
        this.category = category;
        this.fragment = fragment;
        this.result = result;
    }

    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        int fragments = 0;
        ResourceLocation trackId = null;

        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
            if (stack.isEmpty()) continue;

            if (this.fragment.test(stack) && stack.getItem() instanceof RecordTrackHolder<?> fragmentItem) {
                ResourceLocation fragmentTrackId = fragmentItem.getMusicTrackId(stack);
                if (fragmentTrackId == null) {
                    return false;
                } else if (trackId == null) {
                    trackId = fragmentTrackId;
                } else if (!trackId.equals(fragmentTrackId)) {
                    return false;
                }
                fragments++;
            } else {
                return false;
            }
        }

        return trackId != null && fragments == REQUIRED_FRAGMENTS;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
            if (stack.isEmpty()) continue;

            if (this.fragment.test(stack) && stack.getItem() instanceof RecordTrackHolder<?> fragmentItem) {
                Holder.Reference<?> track = fragmentItem.getMusicTrack(stack, registryAccess);
                if (track == null) return ItemStack.EMPTY;

                if (this.result.getItem() instanceof RecordTrackHolder<?> resultItem) {
                    return resultItem.putMusicTrackId(result.copy(), track.key().location());
                } else {
                    return result.copy();
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(REQUIRED_FRAGMENTS, fragment);
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= REQUIRED_FRAGMENTS;
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
    public RecipeSerializer<CustomMusicDiscRecipe> getSerializer() {
        return CRRecipeSerializers.CUSTOM_MUSIC_DISC.get();
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    public static class Serializer implements RecipeSerializer<CustomMusicDiscRecipe> {

        public CustomMusicDiscRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory category = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", null), CraftingBookCategory.MISC);
            Ingredient fragment = Ingredient.fromJson(json.get("fragment"), false);
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new CustomMusicDiscRecipe(id, group, category, fragment, result);
        }

        public CustomMusicDiscRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            Ingredient fragment = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            return new CustomMusicDiscRecipe(id, group, category, fragment, result);
        }

        public void toNetwork(FriendlyByteBuf buf, CustomMusicDiscRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            recipe.fragment.toNetwork(buf);
            buf.writeItem(recipe.result);
        }
    }

    public static class Builder extends CraftingRecipeBuilder implements RecipeBuilder {
        public final RecipeCategory category;
        public final Item result;
        public final Ingredient fragment;
        public final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
        @Nullable
        public String group;

        public Builder(RecipeCategory category, ItemLike result, Ingredient fragment) {
            this.category = category;
            this.result = result.asItem();
            this.fragment = fragment;
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
            consumer.accept(new CustomMusicDiscRecipe.Builder.Result(
                    id,
                    result,
                    group == null ? "" : group,
                    determineBookCategory(category),
                    fragment,
                    this.advancement, id.withPrefix("recipes/" + this.category.getFolderName() + "/")
            ));
        }

        public static class Result extends CraftingRecipeBuilder.CraftingResult {
            private final ResourceLocation id;
            private final Item result;
            private final String group;
            public final Ingredient fragment;
            private final Advancement.Builder advancement;
            private final ResourceLocation advancementId;

            public Result(
                    ResourceLocation id,
                    Item result,
                    String group,
                    CraftingBookCategory category,
                    Ingredient fragment,
                    Advancement.Builder advancement,
                    ResourceLocation advancementId) {
                super(category);
                this.id = id;
                this.result = result;
                this.group = group;
                this.fragment = fragment;
                this.advancement = advancement;
                this.advancementId = advancementId;
            }

            public void serializeRecipeData(JsonObject json) {
                super.serializeRecipeData(json);
                if (!group.isEmpty()) {
                    json.addProperty("group", this.group);
                }

                json.add("fragment", fragment.toJson());

                JsonObject resultJson = new JsonObject();
                resultJson.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result)).toString());
                json.add("result", resultJson);
            }

            public RecipeSerializer<CustomMusicDiscRecipe> getType() {
                return CRRecipeSerializers.CUSTOM_MUSIC_DISC.get();
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
