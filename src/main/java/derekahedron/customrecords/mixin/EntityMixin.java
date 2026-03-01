package derekahedron.customrecords.mixin;

import derekahedron.customrecords.damage.CRDamageTypeTags;
import derekahedron.customrecords.item.CRItemTags;
import derekahedron.customrecords.item.MusicDiscTrackHolder;
import derekahedron.customrecords.sound.MusicDiscTrack;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(
            method = "isInvulnerableTo",
            at = @At("RETURN"),
            cancellable = true
    )
    private void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;

        Entity self = (Entity) (Object) this;
        if (self instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();

            if (stack.is(CRItemTags.IMMUNE_TO_DAMAGE) && source.is(CRDamageTypeTags.DAMAGES_ITEMS)) {
                cir.setReturnValue(true);
            } else if (stack.getItem() instanceof MusicDiscTrackHolder item) {
                Holder.Reference<MusicDiscTrack> track = item.getMusicTrack(stack, self.level());

                if (track != null && track.get().immuneTo().isPresent() && source.is(track.get().immuneTo().get())) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
