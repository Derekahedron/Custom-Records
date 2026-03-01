package derekahedron.customrecords.block;

import derekahedron.customrecords.block.entity.SoundEffectButtonBlockEntity;
import derekahedron.customrecords.item.SoundEffectButtonItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public abstract class AbstractSoundEffectButton extends DirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final int TICKS_TO_STAY_PRESSED = 10;
    public static final BooleanProperty PRESSED = BooleanProperty.create("pressed");
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    // Shapes
    public static final VoxelShape NORTH_AABB = Shapes.or(
            Block.box(4.0F, 4.0F, 14.0F, 12.0F, 12.0F, 16.0F),
            Block.box(5.0F, 5.0F, 12.0F, 11.0F, 11.0F, 14.0F));
    public static final VoxelShape SOUTH_AABB = Shapes.or(
            Block.box(4.0F, 4.0F, 0.0F, 12.0F, 12.0F, 2.0F),
            Block.box(5.0F, 5.0F, 2.0F, 11.0F, 11.0F, 4.0F));
    public static final VoxelShape WEST_AABB = Shapes.or(
            Block.box(14.0F, 4.0F, 4.0F, 16.0F, 12.0F, 12.0F),
            Block.box(12.0F, 5.0F, 5.0F, 14.0F, 11.0F, 11.0F));
    public static final VoxelShape EAST_AABB = Shapes.or(
            Block.box(0.0F, 4.0F, 4.0F, 2.0F, 12.0F, 12.0F),
            Block.box(2.0F, 5.0F, 5.0F, 4.0F, 11.0F, 11.0F));
    public static final VoxelShape DOWN_AABB = Shapes.or(
            Block.box(4.0F, 14.0F, 4.0F, 12.0F, 16.0F, 12.0F),
            Block.box(5.0F, 12.0F, 5.0F, 11.0F, 14.0F, 11.0F));
    public static final VoxelShape UP_AABB = Shapes.or(
            Block.box(4.0F, 0.0F, 4.0F, 12.0F, 2.0F, 12.0F),
            Block.box(5.0F, 2.0F, 5.0F, 11.0F, 4.0F, 11.0F));

    public static final VoxelShape PRESSED_NORTH_AABB = Shapes.or(
            Block.box(4.0F, 4.0F, 14.0F, 12.0F, 12.0F, 16.0F),
            Block.box(5.0F, 5.0F, 13.0F, 11.0F, 11.0F, 14.0F));
    public static final VoxelShape PRESSED_SOUTH_AABB = Shapes.or(
            Block.box(4.0F, 4.0F, 0.0F, 12.0F, 12.0F, 2.0F),
            Block.box(5.0F, 5.0F, 2.0F, 11.0F, 11.0F, 3.0F));
    public static final VoxelShape PRESSED_WEST_AABB = Shapes.or(
            Block.box(14.0F, 4.0F, 4.0F, 16.0F, 12.0F, 12.0F),
            Block.box(13.0F, 5.0F, 5.0F, 14.0F, 11.0F, 11.0F));
    public static final VoxelShape PRESSED_EAST_AABB = Shapes.or(
            Block.box(0.0F, 4.0F, 4.0F, 2.0F, 12.0F, 12.0F),
            Block.box(2.0F, 5.0F, 5.0F, 3.0F, 11.0F, 11.0F));
    public static final VoxelShape PRESSED_DOWN_AABB = Shapes.or(
            Block.box(4.0F, 14.0F, 4.0F, 12.0F, 16.0F, 12.0F),
            Block.box(5.0F, 13.0F, 5.0F, 11.0F, 14.0F, 11.0F));
    public static final VoxelShape PRESSED_UP_AABB = Shapes.or(
            Block.box(4.0F, 0.0F, 4.0F, 12.0F, 2.0F, 12.0F),
            Block.box(5.0F, 2.0F, 5.0F, 11.0F, 3.0F, 11.0F));

    public AbstractSoundEffectButton(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(PRESSED, false)
                .setValue(WATERLOGGED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, PRESSED, WATERLOGGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean pressed = state.getValue(PRESSED);

        return switch (facing) {
            case NORTH -> pressed ? PRESSED_NORTH_AABB : NORTH_AABB;
            case SOUTH -> pressed ? PRESSED_SOUTH_AABB : SOUTH_AABB;
            case WEST -> pressed ? PRESSED_WEST_AABB : WEST_AABB;
            case EAST -> pressed ? PRESSED_EAST_AABB : EAST_AABB;
            case DOWN -> pressed ? PRESSED_DOWN_AABB : DOWN_AABB;
            case UP -> pressed ? PRESSED_UP_AABB : UP_AABB;
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(
            BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(PRESSED)) return InteractionResult.CONSUME;

        press(state, level, pos, player);

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public void press(BlockState state, Level level, BlockPos pos, @Nullable Entity pressingEntity) {
        state = state.setValue(PRESSED, true);
        Player player = pressingEntity instanceof Player ? (Player) pressingEntity : null;

        level.setBlock(pos, state, 3);
        if (level.getBlockEntity(pos) instanceof SoundEffectButtonBlockEntity blockEntity && blockEntity.soundEffect != null) {
            playSound(player, level, pos, blockEntity.soundEffect);
        }

        level.gameEvent(pressingEntity, GameEvent.BLOCK_ACTIVATE, pos);
        level.scheduleTick(pos, this, TICKS_TO_STAY_PRESSED);
    }

    public void unpress(BlockState state, Level level, BlockPos pos) {
        state = state.setValue(PRESSED, false);
        level.setBlock(pos, state, 3);
        updateNeighbours(state, level, pos);
        level.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, pos);
    }

    public abstract void playSound(@Nullable Player player, Level level, BlockPos pos, SoundEvent soundEffect);

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.is(newState.getBlock())) {
            if (isPowering(state)) {
                updateNeighbours(state, level, pos);
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return isPowering(state) ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(FACING) == direction ? getSignal(state, level, pos, direction) : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(PRESSED)) return;

        if (hasArrowInside(state, level, pos)) {
            level.scheduleTick(new BlockPos(pos), this, TICKS_TO_STAY_PRESSED);
        } else {
            unpress(state, level, pos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && !state.getValue(PRESSED) && entity instanceof AbstractArrow && hasArrowInside(state, level, pos)) {
            press(state, level, pos, entity);
        }
    }

    public boolean hasArrowInside(BlockState state, LevelAccessor level, BlockPos pos) {
        return !level.getEntitiesOfClass(AbstractArrow.class, state.getShape(level, pos).bounds().move(pos)).isEmpty();
    }

    public void updateNeighbours(BlockState state, Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    }

    public boolean isPowering(BlockState state) {
        return state.getValue(PRESSED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (state.getValue(FACING).getOpposite() == direction && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos attachedPos = pos.relative(facing.getOpposite());
        return level.getBlockState(attachedPos).isFaceSturdy(level, attachedPos, facing);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);

        if (state != null) {
            FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
            state = state.setValue(FACING, context.getClickedFace()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }
        return state;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SoundEffectButtonBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (stack.getItem() instanceof SoundEffectButtonItem item) {
            ResourceLocation soundEffectId = item.getSoundEffectId(stack);

            if (soundEffectId != null && level.getBlockEntity(pos) instanceof SoundEffectButtonBlockEntity blockEntity) {
                blockEntity.soundEffect = SoundEvent.createVariableRangeEvent(soundEffectId);
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, world, pos, player);
        if (stack.getItem() instanceof SoundEffectButtonItem item
                && world.getBlockEntity(pos) instanceof SoundEffectButtonBlockEntity blockEntity) {
            ResourceLocation soundEffectId = blockEntity.soundEffect != null ? blockEntity.soundEffect.getLocation() : null;
            item.putSoundEffectId(stack, soundEffectId);
        }
        return stack;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }
}
