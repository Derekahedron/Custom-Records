package derekahedron.customrecords.item;

import derekahedron.customrecords.util.CRUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

public class SoundEffectButtonItem extends BlockItem {
    public static final String SOUND_EFFECT_KEY = "SoundEffect";

    public SoundEffectButtonItem(Block block, Properties properties) {
        super(block, properties);
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag advanced) {
        super.appendHoverText(stack, level, components, advanced);
        ResourceLocation soundEffectId = getSoundEffectId(stack);
        if (soundEffectId == null) return;
        String descriptionId = Util.makeDescriptionId("sound_effect_button", soundEffectId);
        components.add(Component.translatable(descriptionId).withStyle(ChatFormatting.GRAY));
    }

    @Nullable
    public ResourceLocation getSoundEffectId(ItemStack stack) {
        if (!(stack.getItem() instanceof SoundEffectButtonItem)) return null;
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(SOUND_EFFECT_KEY)) return null;
        return ResourceLocation.tryParse(tag.getString(SOUND_EFFECT_KEY));
    }

    public void putSoundEffectId(ItemStack stack, @Nullable ResourceLocation soundEffectId) {
        CRUtil.putResourceLocation(stack, SOUND_EFFECT_KEY, soundEffectId);
    }
}
