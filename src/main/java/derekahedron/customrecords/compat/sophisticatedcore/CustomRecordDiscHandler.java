package derekahedron.customrecords.compat.sophisticatedcore;

import derekahedron.customrecords.item.CustomRecordItem;
import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.PlaySophisticatedCustomRecordPacket;
import derekahedron.customrecords.sound.MusicTrack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.p3pp3rf1y.sophisticatedcore.api.IDiscHandler;

import java.util.Optional;
import java.util.UUID;

import static net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.ServerStorageSoundHandler.putSoundInfo;

public class CustomRecordDiscHandler implements IDiscHandler<Holder.Reference<? extends MusicTrack>> {

    @Override
    public Optional<Holder.Reference<? extends MusicTrack>> getSongInfo(ItemStack itemStack, Level level) {
        if (itemStack.getItem() instanceof CustomRecordItem<? extends MusicTrack> item) {
            Holder.Reference<? extends MusicTrack> track = item.getMusicTrack(itemStack, level);
            if (track != null) return Optional.of(track);
        }

        return Optional.empty();
    }

    @Override
    public void playDisc(ServerLevel serverLevel, BlockPos blockPos, UUID uuid, ItemStack itemStack, Runnable onFinished) {
        Holder.Reference<? extends MusicTrack> track = getSongInfo(itemStack, serverLevel).orElse(null);
        if (track == null) return;

        Vec3 position = Vec3.atCenterOf(blockPos);
        CRPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        position.x, position.y, position.z,
                        128, serverLevel.dimension())),
                new PlaySophisticatedCustomRecordPacket(uuid, itemStack.getItem(), track.key().location(), blockPos));

        putSoundInfo(serverLevel, uuid, onFinished, position, serverLevel.getGameTime() + track.get().lengthInTicks());
    }

    @Override
    public void playDisc(ServerLevel serverLevel, Vec3 position, UUID uuid, ItemStack itemStack, int entityId, Runnable onFinished) {
        Holder.Reference<? extends MusicTrack> track = getSongInfo(itemStack, serverLevel).orElse(null);
        if (track == null) return;

        CRPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        position.x, position.y, position.z,
                        128, serverLevel.dimension())),
                new PlaySophisticatedCustomRecordPacket(uuid, itemStack.getItem(), track.key().location(), entityId));

        putSoundInfo(serverLevel, uuid, onFinished, position, serverLevel.getGameTime() + track.get().lengthInTicks());
    }

    @Override
    public Optional<Integer> getMusicLengthInTicks(ItemStack itemStack, Level level) {
        return getSongInfo(itemStack, level)
                .map(reference -> reference.get().lengthInTicks());
    }

    @Override
    public boolean supports(ItemStack itemStack) {
        return itemStack.getItem() instanceof CustomRecordItem<?>;
    }

    @Override
    public Optional<ItemStack> getRandomDisc(RandomSource randomSource) {
        return Optional.empty();
    }

    @Override
    public int getMusicDiscSize() {
        return 0;
    }
}
