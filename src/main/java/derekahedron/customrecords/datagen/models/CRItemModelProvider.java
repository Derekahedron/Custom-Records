package derekahedron.customrecords.datagen.models;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.item.CRItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CRItemModelProvider extends ItemModelProvider {

    public CRItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CustomRecords.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(CRItems.BLANK_SILVER_RECORD.get());
        basicItem(CRItems.SILVER_RECORD.get());
        basicItem(CRItems.GOLDEN_RECORD.get());
        basicItem(CRItems.CUSTOM_MUSIC_DISC_FRAGMENT.get());
        basicItem(CRItems.CUSTOM_MUSIC_DISC.get());
    }
}
