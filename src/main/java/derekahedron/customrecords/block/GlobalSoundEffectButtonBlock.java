package derekahedron.customrecords.block;

import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.PlaySoundEffectButtonPacket;
import derekahedron.customrecords.sound.CRSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class GlobalSoundEffectButtonBlock extends AbstractSoundEffectButton {

    public GlobalSoundEffectButtonBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void playSound(@Nullable Player player, Level level, BlockPos pos, SoundEvent soundEffect) {
        if (!level.isClientSide()) {
            CRPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PlaySoundEffectButtonPacket(level.dimension(), pos, soundEffect, true));
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.is(newState.getBlock()) && !level.isClientSide()) {
            CRPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PlaySoundEffectButtonPacket(level.dimension(), pos, true));
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public SoundEvent getClickOnSoundEvent() {
        return CRSoundEvents.BLOCK_METAL_SOUND_EFFECT_BUTTON_CLICK_ON.get();
    }

    @Override
    public SoundEvent getClickOffSoundEvent() {
        return CRSoundEvents.BLOCK_METAL_SOUND_EFFECT_BUTTON_CLICK_OFF.get();
    }
}
