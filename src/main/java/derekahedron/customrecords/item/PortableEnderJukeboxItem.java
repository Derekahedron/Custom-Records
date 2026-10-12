package derekahedron.customrecords.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.Optional;

public class PortableEnderJukeboxItem extends PortableJukeboxItem {

    public PortableEnderJukeboxItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return null;
    }

    @Override
    public Optional<PortableJukeboxItemHandler> getHandler(ItemStack stack, @Nullable Player player) {
        if (player == null) return Optional.empty();
        if (!player.getCapability(PortableEnderJukeboxInventory.CAPABILITY).isPresent()) return Optional.empty();
        PortableEnderJukeboxItemHandler handler = new PortableEnderJukeboxItemHandler(stack, player);
        handler.load();
        return Optional.of(handler);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        // Records are kept in ender storage, so nothing drops
    }
}
