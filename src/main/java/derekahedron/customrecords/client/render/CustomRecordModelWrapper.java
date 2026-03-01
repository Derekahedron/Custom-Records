package derekahedron.customrecords.client.render;

import derekahedron.customrecords.item.RecordTrackHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;

import javax.annotation.Nullable;

public class CustomRecordModelWrapper extends BakedModelWrapper<BakedModel> {

    public CustomRecordModelWrapper(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public ItemOverrides getOverrides() {
        return new ItemOverrides() {
            @Override
            public BakedModel resolve(
                    BakedModel model, ItemStack stack, @Nullable ClientLevel level,
                    @Nullable LivingEntity entity, int seed) {
                if (level == null) return originalModel;
                if (!(stack.getItem() instanceof RecordTrackHolder<?> item)) return originalModel;

                ResourceLocation variantId = item.getMusicTrackId(stack);
                if (variantId == null) return originalModel;
                ResourceLocation modelLoc = variantId.withPrefix("item/" + item.getModelFolder() + "/");

                BakedModel customModel = Minecraft.getInstance().getModelManager().getModel(modelLoc);

                if (customModel == Minecraft.getInstance().getModelManager().getMissingModel()) return originalModel;
                return customModel;
            }
        };
    }
}
