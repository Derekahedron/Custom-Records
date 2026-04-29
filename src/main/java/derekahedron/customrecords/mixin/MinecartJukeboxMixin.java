package derekahedron.customrecords.mixin;

import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import derekahedron.customrecords.item.CustomRecordItem;
import derekahedron.customrecords.sound.MusicTrack;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartJukebox.class)
public class MinecartJukeboxMixin {

    @Shadow
    private ItemStack disc;

    @Inject(at = @At("HEAD"), method = "getComparatorOutput", cancellable = true, remap = false)
    private void getCustomComparatorOutput(CallbackInfoReturnable<Integer> cir) {
        if (!(disc.getItem() instanceof CustomRecordItem<?> item)) return;
        MinecartJukebox self = (MinecartJukebox) (Object) this;

        Holder.Reference<? extends MusicTrack> track = item.getMusicTrack(disc, self.level());
        if (track == null) return;

        cir.setReturnValue(track.get().analogOutput());
    }
}
