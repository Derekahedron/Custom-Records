package derekahedron.customrecords.datagen.tags;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.item.CRItemTags;
import derekahedron.customrecords.item.CRItems;
import derekahedron.customrecords.util.CompatUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class CRItemTagsProvider extends ItemTagsProvider {

    public CRItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> contentsGetter,
            ExistingFileHelper exFileHelper) {
        super(output, lookupProvider, contentsGetter, CustomRecords.MOD_ID, exFileHelper);
    }

    @Override
    public String getName() {
        return String.format("%s Item Tags", CustomRecords.MOD_NAME);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {

        tag(ItemTags.MUSIC_DISCS)
                .add(CRItems.SILVER_RECORD.get())
                .add(CRItems.GOLDEN_RECORD.get())
                .add(CRItems.CUSTOM_MUSIC_DISC.get());

        tag(ItemTags.create(CompatUtil.inventoryExpansion("sack_type/button")))
                .addTag(CRItemTags.SOUND_EFFECT_BUTTONS);

        tag(ItemTags.create(CompatUtil.inventoryExpansion("sack_type/music_disc")))
                .add(CRItems.BLANK_SILVER_RECORD.get())
                .add(CRItems.CUSTOM_MUSIC_DISC_FRAGMENT.get());

        tag(ItemTags.create(CompatUtil.inventoryExpansion("sack_weight/half")))
                .addTag(CRItemTags.SOUND_EFFECT_BUTTONS);

        tag(CRItemTags.IMMUNE_TO_DAMAGE)
                .add(CRItems.GOLDEN_RECORD.get())
                .add(CRItems.GLOBAL_SOUND_EFFECT_BUTTON.get());

        tag(CRItemTags.NEVER_DESPAWNS)
                .add(CRItems.GOLDEN_RECORD.get())
                .add(CRItems.GLOBAL_SOUND_EFFECT_BUTTON.get());

        tag(CRItemTags.SOUND_EFFECT_BUTTONS)
                .addTag(CRItemTags.DYEABLE_SOUND_EFFECT_BUTTONS)
                .add(CRItems.GLOBAL_SOUND_EFFECT_BUTTON.get());

        tag(CRItemTags.DYEABLE_SOUND_EFFECT_BUTTONS)
                .add(CRItems.WHITE_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.ORANGE_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.MAGENTA_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.LIGHT_BLUE_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.YELLOW_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.LIME_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.PINK_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.GRAY_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.LIGHT_GRAY_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.PURPLE_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.CYAN_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.BLUE_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.BROWN_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.GREEN_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.RED_SOUND_EFFECT_BUTTON.get())
                .add(CRItems.BLACK_SOUND_EFFECT_BUTTON.get());
    }
}
