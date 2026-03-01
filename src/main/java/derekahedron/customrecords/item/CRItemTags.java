package derekahedron.customrecords.item;

import derekahedron.customrecords.util.CRUtil;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CRItemTags {

    public static final TagKey<Item> IMMUNE_TO_DAMAGE =
            ItemTags.create(CRUtil.location("immune_to_damage"));

    public static final TagKey<Item> NEVER_DESPAWNS =
            ItemTags.create(CRUtil.location("never_despawns"));

    public static final TagKey<Item> SOUND_EFFECT_BUTTONS =
            ItemTags.create(CRUtil.location("sound_effect_buttons"));

    public static final TagKey<Item> DYEABLE_SOUND_EFFECT_BUTTONS =
            ItemTags.create(CRUtil.location("dyeable_sound_effect_buttons"));
}
