package derekahedron.customrecords.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class CustomRecordsMixinPlugin implements IMixinConfigPlugin {

    private static final Map<String, Supplier<Boolean>> CONDITIONS = ImmutableMap.of(
            "derekahedron.customrecords.mixin.client.MinecartJukeboxMixin", () -> FMLLoader.getLoadingModList().getModFileById("railways") != null,
            "derekahedron.customrecords.mixin.MinecartJukeboxMixin", () -> FMLLoader.getLoadingModList().getModFileById("railways") != null
    );

    @Override
    public void onLoad(String s) {}

    @Override
    @Nullable
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return CONDITIONS.getOrDefault(mixinClassName, () -> true).get();
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {}

    @Override
    @Nullable
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}
