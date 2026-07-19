package derekahedron.customrecords.client.render;

import derekahedron.customrecords.client.util.ClientPressedSoundEffectButtonsManager;
import derekahedron.customrecords.item.CRItems;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CRItemModelOverrides {

    public static final ResourceLocation PRESSED = CRUtil.location("pressed");

    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(CRItems.WHITE_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.ORANGE_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.MAGENTA_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.LIGHT_BLUE_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.YELLOW_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.LIME_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.PINK_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.GRAY_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.LIGHT_GRAY_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.PURPLE_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.CYAN_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.BLUE_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.BROWN_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.GREEN_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.LIME_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.RED_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.BLACK_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.BLANK_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
            ItemProperties.register(CRItems.GLOBAL_SOUND_EFFECT_BUTTON.get(), PRESSED,
                    (stack, level, entity, id) ->
                            entity instanceof Player player && ClientPressedSoundEffectButtonsManager.isPressed(player, stack)
                                    ? 1.0F : 0.0F);
        });
    }
}
