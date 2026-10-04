package derekahedron.customrecords.sound;

import derekahedron.customrecords.CustomRecords;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.Optional;

public class JukeboxTrackEvent {

    @Cancelable
    public static class Get extends Event {

        private final ItemStack stack;
        private final RegistryAccess registries;
        @Nullable
        private JukeboxTrack track;

        public Get(ItemStack stack, RegistryAccess registries) {
            this.stack = stack;
            this.registries = registries;
            this.track = null;
        }

        public ItemStack getStack() {
            return this.stack;
        }

        public RegistryAccess getRegistries() {
            return this.registries;
        }

        public Optional<JukeboxTrack> getTrack() {
            return Optional.ofNullable(track);
        }

        public void setTrack(@Nullable JukeboxTrack track) {
            this.track = track;
        }
    }

    public static Optional<JukeboxTrack> getJukeboxTrack(ItemStack stack, RegistryAccess registries) {
        Get event = new Get(stack, registries);
        if (CustomRecords.EVENT_BUS.post(event)) return Optional.empty();
        return event.getTrack();
    }
}
