package derekahedron.customrecords.datagen.models;

import derekahedron.customrecords.CustomRecords;
import derekahedron.customrecords.block.CRBlocks;
import derekahedron.customrecords.block.SoundEffectButtonBlock;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CRBlockStateProvider extends BlockStateProvider {

    public CRBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CustomRecords.MOD_ID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    protected void registerStatesAndModels() {
        // Sound Effect Button
        ModelFile soundEffectButtonBase = SoundEffectButtonBase(CRUtil.location("sound_effect_button_base"));
        ModelFile soundEffectButtonPressedBase = SoundEffectButtonPressedBase(CRUtil.location("sound_effect_button_pressed_base"));
        ModelFile soundEffectButtonInventoryBase = SoundEffectButtonInventoryBase(CRUtil.location("sound_effect_button_inventory_base"));

        SoundEffectButton(CRBlocks.WHITE_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.WHITE_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.ORANGE_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.ORANGE_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.MAGENTA_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.MAGENTA_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.LIGHT_BLUE_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.LIGHT_BLUE_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.YELLOW_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.YELLOW_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.LIME_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.LIME_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.PINK_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.PINK_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.GRAY_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.GRAY_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.LIGHT_GRAY_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.LIGHT_GRAY_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.PURPLE_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.PURPLE_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.CYAN_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.CYAN_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.BLUE_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.BLUE_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.BROWN_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.BROWN_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.GREEN_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.GREEN_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.RED_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.RED_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
        SoundEffectButton(CRBlocks.BLACK_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.BLACK_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);

        SoundEffectButton(CRBlocks.GLOBAL_SOUND_EFFECT_BUTTON.get(),
                CRBlocks.GLOBAL_SOUND_EFFECT_BUTTON.getId(),
                soundEffectButtonBase,
                soundEffectButtonPressedBase,
                soundEffectButtonInventoryBase);
    }

    public void SoundEffectButton(Block block, ResourceLocation id, ModelFile base, ModelFile pressedBase, ModelFile inventoryBase) {
        ModelFile model = models().getBuilder(id.getPath()).parent(base)
                .texture("texture", id.withPrefix("block/"));
        ModelFile pressedModel = models().getBuilder(id.withSuffix("_pressed").getPath()).parent(pressedBase)
                .texture("texture", id.withPrefix("block/"));
        ModelFile inventoryModel = models().getBuilder(id.withSuffix("_inventory").getPath()).parent(inventoryBase)
                .texture("texture", id.withPrefix("block/"));

        getVariantBuilder(block)
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.DOWN).with(SoundEffectButtonBlock.PRESSED, false).addModels(
                        new ConfiguredModel(model, 180, 0, false)
                )
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.EAST).with(SoundEffectButtonBlock.PRESSED, false).addModels(
                        new ConfiguredModel(model, 270, 270, false)
                )
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.NORTH).with(SoundEffectButtonBlock.PRESSED, false).addModels(
                        new ConfiguredModel(model, 270, 180, false)
                )
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.SOUTH).with(SoundEffectButtonBlock.PRESSED, false).addModels(
                        new ConfiguredModel(model, 270, 0, false)
                )
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.UP).with(SoundEffectButtonBlock.PRESSED, false).addModels(
                        new ConfiguredModel(model)
                )
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.WEST).with(SoundEffectButtonBlock.PRESSED, false).addModels(
                        new ConfiguredModel(model, 270, 90, false)
                )
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.DOWN).with(SoundEffectButtonBlock.PRESSED, true).addModels(
                        new ConfiguredModel(pressedModel, 180, 0, false)
                )
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.EAST).with(SoundEffectButtonBlock.PRESSED, true).addModels(
                        new ConfiguredModel(pressedModel, 270, 270, false)
                )
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.NORTH).with(SoundEffectButtonBlock.PRESSED, true).addModels(
                        new ConfiguredModel(pressedModel, 270, 180, false)
                )
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.SOUTH).with(SoundEffectButtonBlock.PRESSED, true).addModels(
                        new ConfiguredModel(pressedModel, 270, 0, false)
                )
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.UP).with(SoundEffectButtonBlock.PRESSED, true).addModels(
                        new ConfiguredModel(pressedModel)
                )
                .partialState().with(SoundEffectButtonBlock.FACING, Direction.WEST).with(SoundEffectButtonBlock.PRESSED, true).addModels(
                        new ConfiguredModel(pressedModel, 270, 90, false)
                );

        itemModels().getBuilder(id.getPath()).parent(inventoryModel);
    }

    public BlockModelBuilder SoundEffectButtonBase(ResourceLocation id) {
        return models().withExistingParent(id.getPath(), ResourceLocation.tryParse("block/block"))
                .texture("particle", "#texture")
                .element()
                .from(4, 0, 4)
                .to(12, 2, 12)
                .face(Direction.DOWN).cullface(Direction.DOWN).texture("#texture").uvs(8, 0, 16, 8).end()
                .face(Direction.UP).texture("#texture").uvs(0, 0, 8, 8).end()
                .face(Direction.NORTH).texture("#texture").uvs(0, 14, 8, 16).end()
                .face(Direction.SOUTH).texture("#texture").uvs(0, 14, 8, 16).end()
                .face(Direction.EAST).texture("#texture").uvs(0, 14, 8, 16).end()
                .face(Direction.WEST).texture("#texture").uvs(0, 14, 8, 16).end()
                .end()
                .element()
                .from(5, 2, 5)
                .to(11, 4, 11)
                .face(Direction.UP).texture("#texture").uvs(1, 1, 7, 7).end()
                .face(Direction.NORTH).texture("#texture").uvs(1, 12, 7, 14).end()
                .face(Direction.SOUTH).texture("#texture").uvs(1, 12, 7, 14).end()
                .face(Direction.EAST).texture("#texture").uvs(1, 12, 7, 14).end()
                .face(Direction.WEST).texture("#texture").uvs(1, 12, 7, 14).end()
                .end();
    }

    public BlockModelBuilder SoundEffectButtonPressedBase(ResourceLocation id) {
        return models().withExistingParent(id.getPath(), ResourceLocation.tryParse("block/block"))
                .texture("particle", "#texture")
                .element()
                .from(4, 0, 4)
                .to(12, 2, 12)
                .face(Direction.DOWN).cullface(Direction.DOWN).texture("#texture").uvs(8, 0, 16, 8).end()
                .face(Direction.UP).texture("#texture").uvs(0, 0, 8, 8).end()
                .face(Direction.NORTH).texture("#texture").uvs(0, 14, 8, 16).end()
                .face(Direction.SOUTH).texture("#texture").uvs(0, 14, 8, 16).end()
                .face(Direction.EAST).texture("#texture").uvs(0, 14, 8, 16).end()
                .face(Direction.WEST).texture("#texture").uvs(0, 14, 8, 16).end()
                .end()
                .element()
                .from(5, 2, 5)
                .to(11, 3, 11)
                .face(Direction.UP).texture("#texture").uvs(1, 1, 7, 7).end()
                .face(Direction.NORTH).texture("#texture").uvs(1, 12, 7, 13).end()
                .face(Direction.SOUTH).texture("#texture").uvs(1, 12, 7, 13).end()
                .face(Direction.EAST).texture("#texture").uvs(1, 12, 7, 13).end()
                .face(Direction.WEST).texture("#texture").uvs(1, 12, 7, 13).end()
                .end();
    }

    public BlockModelBuilder SoundEffectButtonInventoryBase(ResourceLocation id) {
        return models().withExistingParent(id.getPath(), ResourceLocation.tryParse("block/block"))
                .texture("particle", "#texture")
                .element()
                .from(4, 6, 4)
                .to(12, 8, 12)
                .face(Direction.DOWN).cullface(Direction.DOWN).texture("#texture").uvs(8, 0, 16, 8).end()
                .face(Direction.UP).texture("#texture").uvs(0, 0, 8, 8).end()
                .face(Direction.NORTH).texture("#texture").uvs(0, 14, 8, 16).end()
                .face(Direction.SOUTH).texture("#texture").uvs(0, 14, 8, 16).end()
                .face(Direction.EAST).texture("#texture").uvs(0, 14, 8, 16).end()
                .face(Direction.WEST).texture("#texture").uvs(0, 14, 8, 16).end()
                .end().element()
                .from(5, 8, 5)
                .to(11, 10, 11)
                .face(Direction.UP).texture("#texture").uvs(1, 1, 7, 7).end()
                .face(Direction.NORTH).texture("#texture").uvs(1, 12, 7, 14).end()
                .face(Direction.SOUTH).texture("#texture").uvs(1, 12, 7, 14).end()
                .face(Direction.EAST).texture("#texture").uvs(1, 12, 7, 14).end()
                .face(Direction.WEST).texture("#texture").uvs(1, 12, 7, 14).end()
                .end();
    }
}
