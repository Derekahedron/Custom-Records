package derekahedron.customrecords.datagen;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.datagen.loot.CRLootTableProvider;
import derekahedron.customrecords.datagen.models.CRBlockStateProvider;
import derekahedron.customrecords.datagen.models.CRItemModelProvider;
import derekahedron.customrecords.datagen.recipes.CRRecipeProvider;
import derekahedron.customrecords.datagen.tags.CRDamageTypeTagsProvider;
import derekahedron.customrecords.datagen.tags.CRItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = CustomRecords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CRDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new CRRecipeProvider(
                output));

        generator.addProvider(event.includeClient(), new CRBlockStateProvider(
                output, existingFileHelper));

        generator.addProvider(event.includeClient(), new CRItemModelProvider(
                output, existingFileHelper));

        generator.addProvider(event.includeServer(), new CRItemTagsProvider(
                output, lookupProvider, CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()), existingFileHelper));

        generator.addProvider(event.includeServer(), new CRLootTableProvider(
                output));

        RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();
        DatapackBuiltinEntriesProvider builtinProvider =
                generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
                        output, lookupProvider, registrySetBuilder,
                        Set.of(CustomRecords.MOD_ID)));

        generator.addProvider(event.includeServer(), new CRDamageTypeTagsProvider(
                output, builtinProvider.getRegistryProvider(), existingFileHelper));
    }
}
