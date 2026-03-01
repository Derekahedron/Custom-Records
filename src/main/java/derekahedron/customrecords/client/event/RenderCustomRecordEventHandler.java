package derekahedron.customrecords.client.event;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.client.render.CustomRecordModelWrapper;
import derekahedron.customrecords.item.RecordTrackHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = CustomRecords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RenderCustomRecordEventHandler {

    @SubscribeEvent
    public static void renderCustomRecord(ModelEvent.ModifyBakingResult event) {
        ForgeRegistries.ITEMS.getEntries().stream()
                .filter(entry -> entry.getValue() instanceof RecordTrackHolder<?>)
                .forEach(entry -> {
                    ModelResourceLocation mrl = new ModelResourceLocation(entry.getKey().location(), "inventory");
                    BakedModel originalModel = event.getModels().get(mrl);

                    if (originalModel != null) {
                        event.getModels().put(mrl, new CustomRecordModelWrapper(originalModel));
                    }
                });
    }

    @SubscribeEvent
    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        var manager = Minecraft.getInstance().getResourceManager();
        ForgeRegistries.ITEMS.getEntries().stream()
                .filter(entry -> entry.getValue() instanceof RecordTrackHolder<?>)
                .forEach(entry -> {
                    RecordTrackHolder<?> item = (RecordTrackHolder<?>) entry.getValue();

                    manager.listResources("models/item/" + item.getModelFolder(),
                                    (loc) -> loc.getPath().endsWith(".json"))
                            .forEach((location, resource) -> {
                                String path = location.getPath()
                                        .replace("models/", "")
                                        .replace(".json", "");
                                event.register(location.withPath(path));
                            });
                });
    }
}
