package derekahedron.customrecords.stats;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CRStats {

    public static final DeferredRegister<ResourceLocation> CUSTOM_STATS =
            DeferredRegister.create(Registries.CUSTOM_STAT, CustomRecords.MOD_ID);

    public static final RegistryObject<ResourceLocation> PRESS_SOUND_EFFECT_BUTTON =
            CUSTOM_STATS.register("press_sound_effect_button", () ->
                    CRUtil.location("press_sound_effect_button"));

    public static void initialize(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Stats.CUSTOM.get(PRESS_SOUND_EFFECT_BUTTON.get(), StatFormatter.DEFAULT);
        });
    }
}
