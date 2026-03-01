package derekahedron.customrecords.item;

import derekahedron.customrecords.sound.MusicTrack;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public abstract class CustomRecordItem<T extends MusicTrack> extends RecordItem implements RecordTrackHolder<T> {
    public static final int DEFAULT_COMPARATOR_VALUE = 0;
    public static final int DEFAULT_LENGTH_IN_TICKS = 0;

    public CustomRecordItem(Properties properties) {
        super(DEFAULT_COMPARATOR_VALUE, () -> null, properties, DEFAULT_LENGTH_IN_TICKS);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag advanced) {
        Holder.Reference<T> track = getMusicTrack(stack, level != null ? level.registryAccess() : CRUtil.getRegistryAccess());
        if (track == null) return;

        String descriptionId = getDescriptionId(track.key().location()) + ".desc";
        components.add(Component.translatable(descriptionId).withStyle(ChatFormatting.GRAY));
    }
}
