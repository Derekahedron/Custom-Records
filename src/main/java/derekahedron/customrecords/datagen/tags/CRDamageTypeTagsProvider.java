package derekahedron.customrecords.datagen.tags;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.damage.CRDamageTypeTags;
import derekahedron.customrecords.util.CompatUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class CRDamageTypeTagsProvider extends TagsProvider<DamageType> {

    public CRDamageTypeTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, CustomRecords.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return String.format("%s Damage Types", CustomRecords.MOD_NAME);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(CRDamageTypeTags.DAMAGES_ITEMS)
                .add(DamageTypes.CACTUS)
                .addTag(DamageTypeTags.IS_FIRE)
                .addTag(DamageTypeTags.IS_EXPLOSION)
                .addTag(DamageTypeTags.IS_LIGHTNING)
                .addOptional(CompatUtil.alexsCaves("acid"));
    }
}
