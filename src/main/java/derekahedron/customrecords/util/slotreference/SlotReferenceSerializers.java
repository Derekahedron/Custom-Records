package derekahedron.customrecords.util.slotreference;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.registry.CRRegistryKeys;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SlotReferenceSerializers {

    public static final DeferredRegister<SlotReferenceSerializer<?>> SLOT_REFERENCE_SERIALIZERS =
            DeferredRegister.create(CRRegistryKeys.SLOT_REFERENCE_SERIALIZER, CustomRecords.MOD_ID);

    public static final RegistryObject<SlotReferenceSerializer<EmptySlotReference>> EMPTY =
            SLOT_REFERENCE_SERIALIZERS.register("empty", EmptySlotReference.Serializer::new);

    public static final RegistryObject<SlotReferenceSerializer<VanillaSlotReference>> VANILLA =
            SLOT_REFERENCE_SERIALIZERS.register("vanilla", VanillaSlotReference.Serializer::new);

    public static final RegistryObject<SlotReferenceSerializer<NestedSlotReference>> NESTED =
            SLOT_REFERENCE_SERIALIZERS.register("nested", NestedSlotReference.Serializer::new);

    public static final Supplier<IForgeRegistry<SlotReferenceSerializer<? extends SlotReference>>> REGISTRY =
            SLOT_REFERENCE_SERIALIZERS.makeRegistry(() -> new RegistryBuilder<SlotReferenceSerializer<?>>()
                    .setDefaultKey(EMPTY.getId())
                    .setName(CRRegistryKeys.SLOT_REFERENCE_SERIALIZER.location()));

}
