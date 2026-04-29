package derekahedron.customrecords.mixin.client;

import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import derekahedron.customrecords.item.CustomRecordItem;
import derekahedron.customrecords.sound.MusicTrack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecartJukebox.class)
public class MinecartJukeboxMixin {

    @Shadow
    private ItemStack disc;

    @Shadow
    private MinecartJukebox.JukeboxCartSoundInstance sound;

    @Inject(at = @At("HEAD"), method = "startPlaying", cancellable = true, remap = false)
    private void startPlayingCustomTrack(CallbackInfo ci) {
        if (!(disc.getItem() instanceof CustomRecordItem<?> item)) return;
        MinecartJukebox self = (MinecartJukebox) (Object) this;

        Holder.Reference<? extends MusicTrack> track = item.getMusicTrack(disc, self.level());
        if (track == null) return;

        sound = self.new JukeboxCartSoundInstance(track.get().soundEvent());
        Minecraft.getInstance().getSoundManager().play(sound);
        ci.cancel();
    }

}
