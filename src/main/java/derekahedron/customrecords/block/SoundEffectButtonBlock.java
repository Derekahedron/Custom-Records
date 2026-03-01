package derekahedron.customrecords.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

public class SoundEffectButtonBlock extends AbstractSoundEffectButton {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty POWERING = BooleanProperty.create("powering");

    public SoundEffectButtonBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(POWERED, false)
                .setValue(POWERING, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED, POWERING);
    }

    public void press(BlockState state, Level level, BlockPos pos, @Nullable Entity pressingEntity) {
        if (pressingEntity != null) {
            state = state.setValue(POWERING, true);
        }
        state = state.setValue(POWERED, true);
        super.press(state, level, pos, pressingEntity);

        if (state.getValue(POWERING)) {
            updateNeighbours(state, level, pos);
        }
    }

    @Override
    public void unpress(BlockState state, Level level, BlockPos pos) {
        state = state.setValue(POWERING, false);
        super.unpress(state, level, pos);
    }

    public void playSound(@Nullable Player player, Level level, BlockPos pos, SoundEvent soundEffect) {
        level.playSound(player, pos, soundEffect, SoundSource.BLOCKS, 1, 1);
    }

    public boolean isPowering(BlockState state) {
        return state.getValue(POWERING);
    }

    public boolean isPowered(BlockState state, Level level, BlockPos pos) {
        Direction facing = state.getValue(FACING);

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);

            if (level.hasSignal(neighborPos, direction)
                    && !(level.getBlockState(neighborPos).getBlock() instanceof SoundEffectButtonBlock)
                    && (direction == facing.getOpposite() || level.getDirectSignal(neighborPos, direction) > 0)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(
            BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean p_55046_) {
        boolean powered = isPowered(state, level, pos);

        if (powered != state.getValue(POWERED)) {
            state = state.setValue(POWERED, powered);

            if (!state.getValue(PRESSED) && powered) {
                press(state, level, pos, null);
            } else {
                level.setBlock(pos, state, 3);
            }
        }
    }
}
