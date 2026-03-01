package derekahedron.customrecords.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GlobalSoundEffectButtonBlock extends AbstractSoundEffectButton {

    public GlobalSoundEffectButtonBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void playSound(@Nullable Player player, Level level, BlockPos pos, SoundEvent soundEffect) {
        if (!level.isClientSide()) {
            for (ServerPlayer serverPlayer : ((ServerLevel) level).getServer().getPlayerList().getPlayers()) {
                serverPlayer.playNotifySound(soundEffect, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}
