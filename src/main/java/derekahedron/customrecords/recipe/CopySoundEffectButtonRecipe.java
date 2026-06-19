package derekahedron.customrecords.recipe;

import com.google.gson.JsonObject;
import derekahedron.customrecords.item.SoundEffectButtonItem;
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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class CopySoundEffectButtonRecipe implements CraftingRecipe {
    public final ResourceLocation id;
    public final String group;
    public final CraftingBookCategory category;
    public final Ingredient button;
    public final Ingredient blankButton;
    public final ItemStack result;

    public CopySoundEffectButtonRecipe(
            ResourceLocation id,
            String group,
            CraftingBookCategory category,
            Ingredient record,
            Ingredient blankRecord,
            ItemStack result) {
        this.id = id;
        this.group = group;
        this.category = category;
        this.button = record;
        this.blankButton = blankRecord;
        this.result = result;
    }

    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        ItemStack button = null;
        ItemStack blankButton = null;
        ResourceLocation soundEffectId = null;

        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
            if (stack.isEmpty()) continue;

            if (this.button.test(stack) && stack.getItem() instanceof SoundEffectButtonItem buttonItem) {
                if (button != null) return false;
                soundEffectId = buttonItem.getSoundEffectId(stack);
                button = stack;
            } else if (this.blankButton.test(stack)) {
                if (blankButton != null) return false;
                blankButton = stack;
            } else {
                return false;
            }
        }

        if (blankButton != null
                && blankButton.getItem() instanceof SoundEffectButtonItem buttonItem) {
            ResourceLocation existingSoundEffectId = buttonItem.getSoundEffectId(blankButton);

            if ((existingSoundEffectId == null && soundEffectId == null)
                    || (soundEffectId != null && soundEffectId.equals(existingSoundEffectId))) {
                return false;
            }
        }

        return blankButton != null && button != null;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < remainingItems.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (button.test(stack) && stack.getItem() instanceof SoundEffectButtonItem) {
                remainingItems.set(i, stack.copy());
            }
        }

        return remainingItems;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {

        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
            if (stack.isEmpty()) continue;

            if (this.button.test(stack) && stack.getItem() instanceof SoundEffectButtonItem buttonItem) {
                ResourceLocation soundEffectId = buttonItem.getSoundEffectId(stack);

                if (this.result.getItem() instanceof SoundEffectButtonItem resultItem) {
                    return resultItem.putSoundEffectId(result.copy(), soundEffectId);
                } else {
                    return result.copy();
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, button, blankButton);
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
    public RecipeSerializer<CopySoundEffectButtonRecipe> getSerializer() {
        return CRRecipeSerializers.COPY_BLANK_SOUND_EFFECT_BUTTON.get();
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    public static class Serializer implements RecipeSerializer<CopySoundEffectButtonRecipe> {

        public CopySoundEffectButtonRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory category = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", null), CraftingBookCategory.MISC);
            Ingredient record = Ingredient.fromJson(json.get("button"), false);
            Ingredient blankRecord = Ingredient.fromJson(json.get("blank_button"), false);
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new CopySoundEffectButtonRecipe(id, group, category, record, blankRecord, result);
        }

        public CopySoundEffectButtonRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            Ingredient button = Ingredient.fromNetwork(buf);
            Ingredient blankButton = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            return new CopySoundEffectButtonRecipe(id, group, category, button, blankButton, result);
        }

        public void toNetwork(FriendlyByteBuf buf, CopySoundEffectButtonRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            recipe.button.toNetwork(buf);
            recipe.blankButton.toNetwork(buf);
            buf.writeItem(recipe.result);
        }
    }

    public static class Builder extends CraftingRecipeBuilder implements RecipeBuilder {
        public final RecipeCategory category;
        public final Item result;
        public final Ingredient record;
        public final Ingredient blankRecord;
        public final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
        @Nullable
        public String group;

        public Builder(RecipeCategory category, ItemLike result, Ingredient record, Ingredient blankRecord) {
            this.category = category;
            this.result = result.asItem();
            this.record = record;
            this.blankRecord = blankRecord;
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
            consumer.accept(new Builder.Result(
                    id,
                    result,
                    group == null ? "" : group,
                    determineBookCategory(category),
                    record,
                    blankRecord,
                    this.advancement, id.withPrefix("recipes/" + this.category.getFolderName() + "/")
            ));
        }

        public static class Result extends CraftingResult {
            private final ResourceLocation id;
            private final Item result;
            private final String group;
            public final Ingredient button;
            public final Ingredient blankButton;
            private final Advancement.Builder advancement;
            private final ResourceLocation advancementId;

            public Result(
                    ResourceLocation id,
                    Item result,
                    String group,
                    CraftingBookCategory category,
                    Ingredient record,
                    Ingredient blankRecord,
                    Advancement.Builder advancement,
                    ResourceLocation advancementId) {
                super(category);
                this.id = id;
                this.result = result;
                this.group = group;
                this.button = record;
                this.blankButton = blankRecord;
                this.advancement = advancement;
                this.advancementId = advancementId;
            }

            public void serializeRecipeData(JsonObject json) {
                super.serializeRecipeData(json);
                if (!group.isEmpty()) {
                    json.addProperty("group", this.group);
                }

                json.add("button", button.toJson());
                json.add("blank_button", blankButton.toJson());

                JsonObject resultJson = new JsonObject();
                resultJson.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result)).toString());
                json.add("result", resultJson);
            }

            public RecipeSerializer<CopySoundEffectButtonRecipe> getType() {
                return CRRecipeSerializers.COPY_BLANK_SOUND_EFFECT_BUTTON.get();
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
