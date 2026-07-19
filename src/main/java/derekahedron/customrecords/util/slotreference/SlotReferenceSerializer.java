package derekahedron.customrecords.util.slotreference;

import net.minecraft.network.FriendlyByteBuf;

public interface SlotReferenceSerializer<T extends SlotReference> {

    T fromNetwork(FriendlyByteBuf buffer);

    void toNetwork(FriendlyByteBuf buffer, T slotReference);
}
