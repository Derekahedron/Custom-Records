package derekahedron.customrecords;

import derekahedron.customrecords.block.CRBlocks;
import derekahedron.customrecords.block.entity.CRBlockEntityTypes;
import derekahedron.customrecords.client.CustomRecordsClient;
import derekahedron.customrecords.compat.curios.CuriosCompat;
import derekahedron.customrecords.compat.sophisticatedcore.SophisticatedCompat;
import derekahedron.customrecords.inventory.CRMenuTypes;
import derekahedron.customrecords.item.CRCreativeTabs;
import derekahedron.customrecords.item.CRItems;
import derekahedron.customrecords.item.SoundEffectPredicate;
import derekahedron.customrecords.loot.CRLootModifiers;
import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.registry.CRRegistryKeys;
import com.mojang.logging.LogUtils;
import derekahedron.customrecords.sound.CRSoundEvents;
import derekahedron.customrecords.stats.CRStats;
import derekahedron.customrecords.util.slotreference.NestedSlotReference;
import derekahedron.customrecords.util.slotreference.SlotReferenceSerializers;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import derekahedron.customrecords.recipe.CRRecipeSerializers;

@Mod(CustomRecords.MOD_ID)
public class CustomRecords {
    public static final String MOD_ID = "customrecords";
    public static final String MOD_NAME = "Custom Records";
    public static final IEventBus EVENT_BUS = BusBuilder.builder().build();
    private static final Logger LOGGER = LogUtils.getLogger();


    @SuppressWarnings("forremoval")
    public CustomRecords() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CRBlocks.BLOCKS.register(modEventBus);
        CRItems.ITEMS.register(modEventBus);
        CRBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        CRRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        CRLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        CRSoundEvents.SOUND_EVENTS.register(modEventBus);
        CRStats.CUSTOM_STATS.register(modEventBus);
        CRMenuTypes.MENU_TYPES.register(modEventBus);
        SlotReferenceSerializers.SLOT_REFERENCE_SERIALIZERS.register(modEventBus);

        modEventBus.addListener(CRPacketHandler::initialize);
        modEventBus.addListener(CRRegistryKeys::init);
        modEventBus.addListener(CRCreativeTabs::initialize);
        modEventBus.addListener(CRStats::initialize);

        EVENT_BUS.addListener(NestedSlotReference::expandSlotReferences);

        if (ModList.get().isLoaded("sophisticatedcore")) {
            try {
                SophisticatedCompat.initialize();
            } catch (NoClassDefFoundError e) {
                LOGGER.error("Sophisticated Core integration failed to load. Probably due to not being on the right version");
            }
        }

        if (ModList.get().isLoaded("curios")) {
            try {
                CuriosCompat.init(modEventBus);
            } catch (NoClassDefFoundError e) {
                LOGGER.error("Curios integration failed to load. Probably due to not being on the right version");
            }
        }

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CustomRecordsClient.init(modEventBus));
        ItemPredicate.register(SoundEffectPredicate.ID, SoundEffectPredicate::deserializeFromJson);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
