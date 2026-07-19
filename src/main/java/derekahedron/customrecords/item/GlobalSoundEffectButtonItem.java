package derekahedron.customrecords.item;

import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.PlaySoundEffectButtonInventoryPacket;
import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.PacketDistributor;

public class GlobalSoundEffectButtonItem extends SoundEffectButtonItem {

    public GlobalSoundEffectButtonItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void playSound(Player player, SlotReference slotReference, SoundEvent soundEffect) {
        if (!player.level().isClientSide()) {
            CRPacketHandler.INSTANCE.send(
                    PacketDistributor.ALL.noArg(),
                    new PlaySoundEffectButtonInventoryPacket(
                            player.getUUID(),
                            slotReference,
                            soundEffect,
                            true));
        }
    }

    @Override
    public void stopSound(Player player, SlotReference slotReference) {
        if (!player.level().isClientSide()) {
            CRPacketHandler.INSTANCE.send(
                    PacketDistributor.ALL.noArg(),
                    new PlaySoundEffectButtonInventoryPacket(
                            player.getUUID(),
                            slotReference,
                            true));
        }
    }
}
