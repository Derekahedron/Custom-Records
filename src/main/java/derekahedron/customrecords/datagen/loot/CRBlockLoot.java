package derekahedron.customrecords.datagen.loot;

import derekahedron.customrecords.block.CRBlocks;
import derekahedron.customrecords.block.entity.SoundEffectButtonBlockEntity;
import derekahedron.customrecords.item.SoundEffectButtonItem;
import com.google.common.collect.ImmutableSet;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class CRBlockLoot extends BlockLootSubProvider {
    public static final Set<Item> EXPLOSION_RESISTANT = ImmutableSet.of();

    public CRBlockLoot() {
        super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSoundEffectButton(CRBlocks.WHITE_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.ORANGE_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.MAGENTA_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.LIGHT_BLUE_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.YELLOW_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.LIME_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.PINK_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.GRAY_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.LIGHT_GRAY_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.PURPLE_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.CYAN_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.BLUE_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.BROWN_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.GREEN_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.RED_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.BLACK_SOUND_EFFECT_BUTTON.get());
        dropSoundEffectButton(CRBlocks.GLOBAL_SOUND_EFFECT_BUTTON.get());
    }

    public void dropSoundEffectButton(Block block) {
        add(block, LootTable.lootTable().withPool(
                applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)
                                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                        .copy(SoundEffectButtonBlockEntity.SOUND_EFFECT_KEY,SoundEffectButtonItem.SOUND_EFFECT_KEY))))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return CRBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
