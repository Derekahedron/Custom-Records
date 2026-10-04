package derekahedron.customrecords.item;

import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.OpenItemPacket;
import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.slotreference.SlotReference;
import derekahedron.customrecords.util.slotreference.SlotReferenceEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;

public abstract class OpenableItem extends Item {

    public OpenableItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        SlotReference slotReference = CRUtil.getSlotReference(player, hand);
        if (stack != slotReference.getStackForPlayer(player).orElse(null)) {
            return InteractionResultHolder.fail(stack);
        }

        IItemHandlerModifiable handler = slotReference.getStackForPlayer(player)
                .flatMap(this::getHandler)
                .orElse(null);
        if (handler == null) return InteractionResultHolder.pass(stack);

        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(
                    serverPlayer,
                    new SimpleMenuProvider(
                            getMenuConstructor(slotReference),
                            stack.getHoverName()),
                    buffer -> SlotReference.toNetwork(buffer, slotReference));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack stack,
            ItemStack otherStack,
            Slot slot,
            ClickAction clickAction,
            Player player,
            SlotAccess slotAccess) {
        if (clickAction != ClickAction.SECONDARY
                || !otherStack.isEmpty()
                || stack.getCount() > 1) return false;

        SlotReference slotReference = SlotReferenceEvent.getSlotReference(player, slot.getItem()).orElse(null);
        if (slotReference == null) return false;

        IItemHandlerModifiable handler = slotReference.getStackForPlayer(player)
                .flatMap(this::getHandler)
                .orElse(null);
        if (handler == null) return false;

        if (player.level().isClientSide()) {
            CRPacketHandler.INSTANCE.sendToServer(new OpenItemPacket(slotReference));
        }

        return true;
    }

    public abstract MenuConstructor getMenuConstructor(SlotReference slotReference);

    public abstract Optional<? extends IItemHandlerModifiable> getHandler(ItemStack stack);
}
