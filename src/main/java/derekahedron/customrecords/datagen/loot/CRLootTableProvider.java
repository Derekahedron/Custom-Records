package derekahedron.customrecords.datagen.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class CRLootTableProvider extends LootTableProvider {

    public CRLootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(CRBlockLoot::new, LootContextParamSets.BLOCK)));
    }
}
