package derekahedron.customrecords.util;

import derekahedron.customrecords.util.slotreference.SlotReference;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class InventoryCallbacksManager {

    private static final HashMap<UUID, HashMap<SlotReference, ArrayList<Callback>>> CALLBACKS = new HashMap<>();

    public static boolean addCallback(Callback callback) {
        if (callback.stack.isEmpty()) return false;

        CALLBACKS.computeIfAbsent(callback.playerId, k -> new HashMap<>())
                .computeIfAbsent(callback.slotReference, k -> new ArrayList<>())
                .add(callback);

        return true;
    }

    public static void tick(MinecraftServer server) {
        var playerIterator = CALLBACKS.entrySet().iterator();

        while (playerIterator.hasNext()) {
            var playerEntry = playerIterator.next();
            UUID playerId = playerEntry.getKey();
            var playerCallbacks = playerEntry.getValue();
            ServerPlayer player = server.getPlayerList().getPlayer(playerId);

            if (player == null) {
                playerCallbacks.forEach((slotReference, tuples) ->
                        tuples.forEach(callback -> callback.onChange(null)));
                playerCallbacks.clear();
            } else {
                updatePlayer(player, playerCallbacks);
            }

            if (playerCallbacks.isEmpty()) {
                playerIterator.remove();
            }
        }
    }

    public static void clearCallbacks(ServerPlayer player) {
        var playerCallbacks = CALLBACKS.get(player.getUUID());
        if (playerCallbacks == null) return;

        playerCallbacks.forEach((slotReference, callbacks) ->
                callbacks.forEach(callback ->
                        callback.onChange(player)));
        playerCallbacks.clear();
        CALLBACKS.remove(player.getUUID());
    }

    private static void updatePlayer(ServerPlayer player, HashMap<SlotReference, ArrayList<Callback>> playerCallbacks) {
        var slotReferenceIterator = playerCallbacks.entrySet().iterator();

        while (slotReferenceIterator.hasNext()) {
            var slotReferenceEntry = slotReferenceIterator.next();
            SlotReference slotReference = slotReferenceEntry.getKey();
            var callbackIterator = slotReferenceEntry.getValue().iterator();

            while (callbackIterator.hasNext()) {
                var callback = callbackIterator.next();
                ItemStack otherStack = slotReference.getStackForPlayer(player).orElse(null);

                if (otherStack == null
                        || !callback.matches(otherStack)
                        || callback.stack.isEmpty()) {
                    callback.onChange(player);
                    callbackIterator.remove();
                }
            }

            if (slotReferenceEntry.getValue().isEmpty()) {
                slotReferenceIterator.remove();
            }
        }
    }

    public abstract static class Callback {

        public final UUID playerId;
        public final SlotReference slotReference;
        public final ItemStack stack;
        protected final ItemStack copy;

        public Callback(Player player, SlotReference slotReference) {
            this.playerId = player.getUUID();
            this.slotReference = slotReference;
            this.stack = slotReference.getStackForPlayer(player).orElse(ItemStack.EMPTY);
            this.copy = stack.copy();
        }

        public boolean matches(ItemStack stack) {
            return this.stack == stack || ItemStack.matches(stack, this.copy);
        }

        public abstract void onChange(@Nullable ServerPlayer player);
    }
}
