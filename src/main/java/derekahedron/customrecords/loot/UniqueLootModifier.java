package derekahedron.customrecords.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.LootModifier;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

public class UniqueLootModifier extends LootModifier {
    public static final Codec<UniqueLootModifier> CODEC = RecordCodecBuilder.create(inst ->
            codecStart(inst).and(
                    Entry.CODEC.listOf().fieldOf("items").forGetter(m -> m.items)
            ).and(
                    Codec.LONG.optionalFieldOf("cooldown", 0L).forGetter(m -> m.cooldown)
            ).and(
                    Codec.BOOL.optionalFieldOf("block_during_cooldown", true).forGetter(m -> m.blockDuringCooldown)
            ).apply(inst, UniqueLootModifier::new));

    public final List<Entry> items;
    public final long cooldown;
    public final boolean blockDuringCooldown;

    @SuppressWarnings("unused")
    public UniqueLootModifier(LootItemCondition[] conditionsIn, List<Entry> items) {
        this(conditionsIn, items, 0L, true);
    }

    public UniqueLootModifier(LootItemCondition[] conditionsIn, List<Entry> items, long cooldown, boolean blockDuringCooldown) {
        super(conditionsIn);
        this.items = items;
        this.cooldown = cooldown;
        this.blockDuringCooldown = blockDuringCooldown;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        UniqueLootData data = UniqueLootData.get(context.getLevel());
        List<Entry> validEntries = items.stream()
                .filter(entry -> data.getNumRecords(entry.name) < entry.maxItems)
                .toList();

        if (!validEntries.isEmpty()) {

            Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
            Optional<BlockPos> pos = origin != null
                    ? Optional.of(BlockPos.containing(origin))
                    : Optional.empty();

            Player player;
            Optional<Double> distance = Optional.empty();

            Entity entity;
            if ((entity = context.getParamOrNull(LootContextParams.KILLER_ENTITY)) instanceof Player
                    || (entity = context.getParamOrNull(LootContextParams.THIS_ENTITY)) instanceof Player) {
                player = (Player) entity;
            } else if (origin != null) {
                player = context.getLevel().getNearestPlayer(TargetingConditions.forNonCombat(),
                        origin.x,
                        origin.y,
                        origin.z);
                if (player != null) {
                    distance = Optional.of(Mth.length(
                            origin.x - player.position().x,
                            origin.y - player.position().y,
                            origin.z - player.position().z));
                }
            } else {
                player = null;
            }

            Optional<UUID> playerUUID;
            Optional<String> playerName;
            if (player != null) {
                playerUUID = Optional.of(player.getUUID());
                playerName = Optional.of(player.getGameProfile().getName());
            } else {
                playerUUID = Optional.empty();
                playerName = Optional.empty();
                distance = Optional.empty();
            }

            // Get latest time that the player has gotten a unique entry
            long now = System.currentTimeMillis();
            if (playerUUID.isPresent() && cooldown > 0) {
                OptionalLong latestTimestamp = items.stream()
                        .map(entry -> data.getRecordTimestamp(playerUUID.get(), entry.name))
                        .filter(Optional::isPresent)
                        .mapToLong(Optional::get)
                        .max();

                if (latestTimestamp.isPresent()) {
                    long timeSince = now - latestTimestamp.getAsLong();

                    if (timeSince >= 0 && timeSince < cooldown) {
                        if (blockDuringCooldown || context.getRandom().nextFloat() >= (float) timeSince / cooldown) {
                            return generatedLoot;
                        }
                    }
                }
            }

            Entry entry = validEntries.get(context.getRandom().nextInt(validEntries.size()));

            data.addRecord(new UniqueLootData.UniqueLootRecord(
                    entry.name,
                    playerUUID,
                    playerName,
                    distance,
                    pos,
                    context.getLevel().dimension(),
                    now));
            generatedLoot.add(entry.item);
        }

        return generatedLoot;
    }

    @Override
    public Codec<UniqueLootModifier> codec() {
        return CRLootModifiers.UNIQUE_LOOT.get();
    }

    public record Entry(ResourceLocation name, ItemStack item, int maxItems) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        ResourceLocation.CODEC.fieldOf("name").forGetter(Entry::name),
                        ItemStack.CODEC.fieldOf("item").forGetter(Entry::item),
                        Codec.INT.optionalFieldOf("max_items", 1).forGetter(Entry::maxItems)
                ).apply(inst, Entry::new));
    }
}
