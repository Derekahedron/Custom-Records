package derekahedron.customrecords.mixin;

import derekahedron.customrecords.item.CustomRecordItem;
import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.PlayCustomRecordPacket;
import derekahedron.customrecords.sound.MusicTrack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JukeboxBlockEntity.class)
public class JukeboxBlockEntityMixin {

    @Shadow
    private long tickCount;

    @Shadow
    private long recordStartedTick;

    @Inject(
            method = "startPlaying",
            at = @At("RETURN")
    )
    private void startPlaying(CallbackInfo ci) {
        JukeboxBlockEntity self = (JukeboxBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;
        ItemStack record = self.getItem(0);

        if (!(record.getItem() instanceof CustomRecordItem<?> item)) return;

        Holder.Reference<? extends MusicTrack> track = item.getMusicTrack(record, level);
        if (track == null) return;

        if (self.getLevel() instanceof ServerLevel serverLevel) {
            BlockPos pos = self.getBlockPos();
            CRPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                    null,
                    pos.getX(), pos.getY(), pos.getZ(),
                    64.0D,
                    serverLevel.dimension()
            )), new PlayCustomRecordPacket(pos, item, track.key().location()));
        }
    }

    @Inject(
            method = "shouldRecordStopPlaying",
            at = @At("RETURN"),
            cancellable = true
    )
    private void shouldStopPlaying(RecordItem item, CallbackInfoReturnable<Boolean> cir) {
        JukeboxBlockEntity self = (JukeboxBlockEntity) (Object) this;

        if (self.getLevel() == null) return;
        ItemStack stack = self.getItem(0);

        if (stack.getItem() instanceof CustomRecordItem<?> record) {
            Holder.Reference<? extends MusicTrack> track = record.getMusicTrack(stack, self.getLevel());

            if (track == null) return;
            if (cir.getReturnValue() != (tickCount >= recordStartedTick + track.get().lengthInTicks() + 20L)) {
                cir.setReturnValue(!cir.getReturnValue());
            }
        }
    }
}
