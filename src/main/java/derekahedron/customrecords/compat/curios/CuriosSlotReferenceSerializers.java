package derekahedron.customrecords.compat.curios;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.registry.CRRegistryKeys;
import derekahedron.customrecords.util.slotreference.SlotReferenceSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CuriosSlotReferenceSerializers {

    public static final DeferredRegister<SlotReferenceSerializer<?>> SLOT_REFERENCE_SERIALIZERS =
            DeferredRegister.create(CRRegistryKeys.SLOT_REFERENCE_SERIALIZER, CustomRecords.MOD_ID);

    public static final RegistryObject<SlotReferenceSerializer<CuriosSlotReference>> CURIOS =
            SLOT_REFERENCE_SERIALIZERS.register("curios", CuriosSlotReference.Serializer::new);
}
