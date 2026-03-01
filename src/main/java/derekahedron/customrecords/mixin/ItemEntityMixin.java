package derekahedron.customrecords.mixin;

import derekahedron.customrecords.item.MusicDiscTrackHolder;
import derekahedron.customrecords.sound.MusicDiscTrack;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "fireImmune", at = @At("RETURN"), cancellable = true)
    public void immuneToFire(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack stack = self.getItem();

        if (stack.getItem() instanceof MusicDiscTrackHolder item) {
            Holder.Reference<MusicDiscTrack> track = item.getMusicTrack(stack, self.level());
            if (track != null && track.get().isFireResistant()) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
