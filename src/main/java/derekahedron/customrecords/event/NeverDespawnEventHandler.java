package derekahedron.customrecords.event;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.item.CRItemTags;
import derekahedron.customrecords.item.MusicDiscTrackHolder;
import derekahedron.customrecords.sound.MusicDiscTrack;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CustomRecords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NeverDespawnEventHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {

        if (event.getEntity() instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();

            if (stack.is(CRItemTags.NEVER_DESPAWNS)) {
                itemEntity.setUnlimitedLifetime();
            } else if (stack.getItem() instanceof MusicDiscTrackHolder musicTrackItem) {
                Holder.Reference<MusicDiscTrack> track = musicTrackItem.getMusicTrack(stack, itemEntity.level());

                if (track != null && !track.get().despawns()) {
                    itemEntity.setUnlimitedLifetime();
                }
            }
        }
    }
}
