package derekahedron.customrecords.loot;

import derekahedron.customrecords.CustomRecords;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CRLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CustomRecords.MOD_ID);

    public static final RegistryObject<Codec<UniqueLootModifier>> UNIQUE_LOOT =
            LOOT_MODIFIER_SERIALIZERS.register("unique_loot", () -> UniqueLootModifier.CODEC);
}
