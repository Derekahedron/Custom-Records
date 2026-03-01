package derekahedron.customrecords.loot;

import derekahedron.customrecords.CustomRecords;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.*;

public class UniqueLootData extends SavedData {
    public static final String LOOT_RECORDS_KEY = "LootRecords";
    public static final String STORAGE_KEY = CustomRecords.MOD_ID + "_unique_loot_log";
    public final List<UniqueLootRecord> uniqueLootRecords;
    public final Map<ResourceLocation, Integer> uniqueLootRecordCounts;

    public UniqueLootData(int initialCapacity) {
        uniqueLootRecords = new ArrayList<>(initialCapacity);
        uniqueLootRecordCounts = new HashMap<>();
    }

    public UniqueLootData() {
        this(0);
    }

    public static UniqueLootData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage()
                .computeIfAbsent(UniqueLootData::load, UniqueLootData::new, STORAGE_KEY);
    }

    public int getNumRecords(ResourceLocation name) {
        return uniqueLootRecordCounts.getOrDefault(name, 0);
    }

    public void addRecord(UniqueLootRecord record) {
        uniqueLootRecords.add(record);
        uniqueLootRecordCounts.put(record.name, getNumRecords(record.name) + 1);
        setDirty(true);
    }

    public static UniqueLootData load(CompoundTag tag) {
        ListTag lootRecords = tag.getList(LOOT_RECORDS_KEY, 10);
        UniqueLootData data = new UniqueLootData(lootRecords.size());

        for (int i = 0; i < lootRecords.size(); i++) {
            UniqueLootRecord record = UniqueLootRecord.load(lootRecords.getCompound(i));
            if (record != null) {
                data.uniqueLootRecords.add(record);
                data.uniqueLootRecordCounts.put(
                        record.name,
                        data.getNumRecords(record.name) + 1);
            }
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag lootRecords = new ListTag();
        uniqueLootRecords.forEach(record ->
                lootRecords.add(record.save(new CompoundTag())));
        tag.put(LOOT_RECORDS_KEY, lootRecords);
        return tag;
    }

    public record UniqueLootRecord(
            ResourceLocation name,
            Optional<UUID> playerUUID,
            Optional<String> playerName,
            Optional<Double> distance,
            Optional<BlockPos> pos,
            ResourceKey<Level> dimension,
            long timestamp) {
        public static final String NAME_KEY = "Name";
        public static final String PLAYER_UUID_KEY = "PlayerUUID";
        public static final String PLAYER_NAME_KEY = "PlayerName";
        public static final String DISTANCE_KEY = "Distance";
        public static final String POSITION_KEY = "Position";
        public static final String DIMENSION_KEY = "Dimension";
        public static final String TIMESTAMP_KEY = "Timestamp";

        @Nullable
        public static UniqueLootRecord load(CompoundTag tag) {
            ResourceLocation name = ResourceLocation.tryParse(tag.getString(NAME_KEY));
            if (name == null) return null;

            Optional<UUID> playerUUID = tag.contains(PLAYER_UUID_KEY)
                    ? Optional.of(tag.getUUID(PLAYER_UUID_KEY))
                    : Optional.empty();

            Optional<String> playerName = tag.contains(PLAYER_NAME_KEY)
                    ? Optional.of(tag.getString(PLAYER_NAME_KEY))
                    : Optional.empty();

            Optional<Double> distance = tag.contains(DISTANCE_KEY)
                    ? Optional.of(tag.getDouble(DISTANCE_KEY))
                    : Optional.empty();

            Optional<BlockPos> pos = tag.contains(POSITION_KEY)
                    ? Optional.of(NbtUtils.readBlockPos(tag.getCompound(POSITION_KEY)))
                    : Optional.empty();

            ResourceLocation dimensionLocation = ResourceLocation.tryParse(tag.getString(DIMENSION_KEY));
            if (dimensionLocation == null) return null;
            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, dimensionLocation);

            if (!tag.contains(TIMESTAMP_KEY)) return null;
            long timestamp = tag.getLong(TIMESTAMP_KEY);

            return new UniqueLootRecord(
                    name,
                    playerUUID,
                    playerName,
                    distance,
                    pos,
                    dimension,
                    timestamp);
        }

        public CompoundTag save(CompoundTag tag) {
            tag.putString(NAME_KEY, name.toString());
            playerUUID.ifPresent(p -> tag.putUUID(PLAYER_UUID_KEY, p));
            playerName.ifPresent(p -> tag.putString(PLAYER_NAME_KEY, p));
            distance.ifPresent(d -> tag.putDouble(DISTANCE_KEY, d));
            pos.ifPresent(p -> tag.put(POSITION_KEY, NbtUtils.writeBlockPos(p)));
            tag.putString(DIMENSION_KEY, dimension.location().toString());
            tag.putLong(TIMESTAMP_KEY, timestamp);
            return tag;
        }
    }
}
