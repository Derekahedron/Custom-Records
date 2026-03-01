package derekahedron.customrecords.damage;

import derekahedron.customrecords.util.CRUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class CRDamageTypeTags {

    public static final TagKey<DamageType> DAMAGES_ITEMS =
            TagKey.create(Registries.DAMAGE_TYPE, CRUtil.location("damages_items"));
}
