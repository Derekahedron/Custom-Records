package derekahedron.customrecords.item.sack;

import derekahedron.customrecords.item.CRItems;
import derekahedron.customrecords.item.SoundEffectPredicate;
import derekahedron.customrecords.util.CRUtil;
import derekahedron.invexp.item.sack.SackWeightRule;
import derekahedron.invexp.registry.InvExpRegistryKeys;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

public class CRSackWeightRules {

    public static final ResourceKey<SackWeightRule> BLANK_SOUND_EFFECT_BUTTON = ResourceKey.create(
            InvExpRegistryKeys.SACK_WEIGHT_RULE, CRUtil.location("blank_sound_effect_button"));

    public static void bootstrap(BootstapContext<SackWeightRule> context) {
        context.register(BLANK_SOUND_EFFECT_BUTTON, new SackWeightRule(
                Optional.of(10),
                Optional.of(Ingredient.of(CRItems.BLANK_SOUND_EFFECT_BUTTON.get())),
                Optional.of(new SoundEffectPredicate(null)),
                Optional.empty()));
    }
}
