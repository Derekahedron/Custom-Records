package derekahedron.customrecords;

import derekahedron.customrecords.block.CRBlocks;
import derekahedron.customrecords.block.entity.CRBlockEntityTypes;
import derekahedron.customrecords.compat.sophisticatedcore.SophisticatedCompat;
import derekahedron.customrecords.item.CRCreativeTabs;
import derekahedron.customrecords.item.CRItems;
import derekahedron.customrecords.loot.CRLootModifiers;
import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.registry.CRRegistryKeys;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import derekahedron.customrecords.recipe.CRRecipeSerializers;

@Mod(CustomRecords.MOD_ID)
public class CustomRecords {
    public static final String MOD_ID = "customrecords";
    public static final String MOD_NAME = "Custom Records";
    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("forremoval")
    public CustomRecords() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CRBlocks.BLOCKS.register(modEventBus);
        CRItems.ITEMS.register(modEventBus);
        CRBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        CRRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        CRLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);

        modEventBus.addListener(CRPacketHandler::initialize);
        modEventBus.addListener(CRRegistryKeys::initialize);
        modEventBus.addListener(CRCreativeTabs::initialize);

        if (ModList.get().isLoaded("sophisticatedcore")) {
            try {
                SophisticatedCompat.initialize();
            } catch (NoClassDefFoundError e) {
                LOGGER.error("Sophisticated Core integration failed to load. Probably due to not being on the right version");
            }
        }

        MinecraftForge.EVENT_BUS.register(this);
    }
}
