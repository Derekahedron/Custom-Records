package derekahedron.customrecords.item;

import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public class GlobalSoundEffectButtonItem extends SoundEffectButtonItem {

    public GlobalSoundEffectButtonItem(Block block, Properties properties) {
        super(block, properties);
    }

    public boolean isGlobal(Player player, SlotReference slotReference) {
        return true;
    }
}
