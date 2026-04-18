package derekahedron.customrecords.block;

import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.PlayGlobalSoundEffectButtonPacket;
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
            CRPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PlayGlobalSoundEffectButtonPacket(level.dimension(), pos, soundEffect));
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.is(newState.getBlock()) && !level.isClientSide()) {
            CRPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PlayGlobalSoundEffectButtonPacket(level.dimension(), pos));
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
