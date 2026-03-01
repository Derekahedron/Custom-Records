package derekahedron.customrecords.mixin;

import derekahedron.customrecords.item.CustomRecordItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "levelEvent", at = @At("HEAD"), cancellable = true)
    void levelEvent(Player player, int eventId, BlockPos blockPos, int itemId, CallbackInfo ci) {
        // Stop Golden Records from being played normally
        if (eventId != 1010) return;
        Item item = Item.byId(itemId);
        if (item instanceof CustomRecordItem<?>) {
            ci.cancel();
        }
    }
}
