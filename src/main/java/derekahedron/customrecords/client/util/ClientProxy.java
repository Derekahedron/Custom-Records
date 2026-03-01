package derekahedron.customrecords.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ClientProxy {

    @Nullable
    public static RegistryAccess getClientRegistryAccess() {
        Level level = Minecraft.getInstance().level;
        return level == null ? null : level.registryAccess();
    }
}
