package derekahedron.customrecords.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class SoundEffectButtonBlockEntity extends BlockEntity {
    public static final String SOUND_EFFECT_KEY = "SoundEffect";
    @Nullable
    public SoundEvent soundEffect = null;

    public SoundEffectButtonBlockEntity(BlockPos pos, BlockState state) {
        super(CRBlockEntityTypes.SOUND_EFFECT_BUTTON.get(), pos, state);
    }

    public void load(CompoundTag tag) {
        super.load(tag);

        soundEffect = null;
        if (tag.contains(SOUND_EFFECT_KEY)) {
            ResourceLocation soundEffectId = ResourceLocation.tryParse(tag.getString(SOUND_EFFECT_KEY));
            if (soundEffectId != null) {
                soundEffect = SoundEvent.createVariableRangeEvent(soundEffectId);
            }
        }
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (soundEffect != null) {
            tag.putString(SOUND_EFFECT_KEY, soundEffect.getLocation().toString());
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }
}
