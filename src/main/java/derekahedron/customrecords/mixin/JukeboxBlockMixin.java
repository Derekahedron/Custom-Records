package derekahedron.customrecords.mixin;

import derekahedron.customrecords.item.CustomRecordItem;
import derekahedron.customrecords.sound.MusicTrack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JukeboxBlock.class)
public class JukeboxBlockMixin {

    @Inject(
            method = "getAnalogOutputSignal",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getGoldenRecordAnalog(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() != CustomRecordItem.DEFAULT_COMPARATOR_VALUE) return;
        if (level.getBlockEntity(pos) instanceof JukeboxBlockEntity blockEntity) {
            ItemStack stack = blockEntity.getFirstItem();

            if (stack.getItem() instanceof CustomRecordItem<?> item) {
                Holder.Reference<? extends MusicTrack> track = item.getMusicTrack(stack, level);

                if (track == null) return;

                cir.setReturnValue(track.get().analogOutput());
            }
        }
    }
}
