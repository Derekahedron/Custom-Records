package derekahedron.customrecords.mixin.client;

import derekahedron.customrecords.item.RecordTrackHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Shadow
    protected ItemStack lastToolHighlight;

    @Inject(
            method = "tick()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getSelected()Lnet/minecraft/world/item/ItemStack;"))
    private void onTick(CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack newStack = player.getMainHandItem();
        if (newStack.getItem() instanceof RecordTrackHolder
                && newStack.getItem() == lastToolHighlight.getItem()
                && !RecordTrackHolder.hasSameTrack(newStack, lastToolHighlight, player.level().registryAccess())) {
            lastToolHighlight = ItemStack.EMPTY;
        }
    }
}
