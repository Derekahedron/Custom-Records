package derekahedron.customrecords.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class SoundEffectPredicate extends ItemPredicate {

    public static final ResourceLocation ID = CRUtil.location("sound_effect");
    public static final String SOUND_EFFECT_KEY = "sound_effect";

    @Nullable
    public final ResourceLocation soundEffectId;

    public SoundEffectPredicate(@Nullable ResourceLocation soundEffectId) {
        this.soundEffectId = soundEffectId;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.getItem() instanceof SoundEffectButtonItem item) {
            ResourceLocation soundEffectId = item.getSoundEffectId(stack);

            return ((this.soundEffectId == null && soundEffectId == null)
                    || (this.soundEffectId != null && this.soundEffectId.equals(soundEffectId)));
        }
        return false;
    }

    @Override
    public JsonElement serializeToJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", ID.toString());
        json.add(SOUND_EFFECT_KEY, this.soundEffectId != null
                ? new JsonPrimitive(this.soundEffectId.toString())
                : JsonNull.INSTANCE);
        return json;
    }

    public static SoundEffectPredicate deserializeFromJson(JsonObject json) {
        if (json.get(SOUND_EFFECT_KEY) instanceof JsonPrimitive soundEffectId) {
            return new SoundEffectPredicate(ResourceLocation.tryParse(soundEffectId.getAsString()));
        } else {
            return new SoundEffectPredicate(null);
        }
    }
}
