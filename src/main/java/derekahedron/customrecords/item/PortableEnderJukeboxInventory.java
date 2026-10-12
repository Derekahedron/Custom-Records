package derekahedron.customrecords.item;

import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.Tuple;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.LinkedList;

public class PortableEnderJukeboxInventory {

    public static final ResourceLocation ID = CRUtil.location("portable_ender_jukebox");
    public static final Capability<PortableEnderJukeboxInventory> CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});

    public final LinkedList<Tuple<ItemStack, Integer>> records = new LinkedList<>();

    public void setRecords(NonNullList<ItemStack> newRecords) {
        records.removeIf(tuple -> tuple.b() < newRecords.size());

        for (int i = Math.min(newRecords.size(), 0x000000FF) - 1; i >= 0; i--) {
            ItemStack stack = newRecords.get(i);
            if (stack.isEmpty()) continue;
            records.addFirst(new Tuple<>(stack.copy(), i));
        }
    }

    public void loadRecords(NonNullList<ItemStack> newRecords) {
        newRecords.clear();
        records.forEach(tuple -> {
            ItemStack stack = tuple.a();
            int slot = tuple.b();
            if (slot >= 0 && slot < newRecords.size()) {
                newRecords.set(slot, stack.copy());
            }
        });
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {

        public static final String TAG_ITEMS = "Items";
        public static final String TAG_SLOT = "Slot";

        private final PortableEnderJukeboxInventory inventory = new PortableEnderJukeboxInventory();
        private final LazyOptional<PortableEnderJukeboxInventory> optional = LazyOptional.of(() -> inventory);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
            return CAPABILITY.orEmpty(capability, optional);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            if (inventory.records.isEmpty()) return tag;

            tag.put(TAG_ITEMS, Util.make(new ListTag(), items ->
                    inventory.records.forEach(tuple ->
                            items.add(Util.make(new CompoundTag(), x -> {
                                x.putByte(TAG_SLOT, (byte) (int) tuple.b());
                                tuple.a().save(x);
                            })))));

            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            inventory.records.clear();
            if (!tag.contains(TAG_ITEMS, ListTag.TAG_LIST)) return;
            ListTag items = tag.getList(TAG_ITEMS, ListTag.TAG_COMPOUND);

            for (int i = 0; i < items.size(); ++i) {
                CompoundTag itemTag = items.getCompound(i);
                int slot = itemTag.getByte(TAG_SLOT) & 0x000000FF;
                inventory.records.add(new Tuple<>(ItemStack.of(itemTag), slot));
            }
        }
    }
}
