package derekahedron.customrecords.inventory;

import derekahedron.customrecords.CustomRecords;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CRMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CustomRecords.MOD_ID);

    public static final RegistryObject<MenuType<SoundBoardMenu>> SOUND_BOARD =
            MENU_TYPES.register("sound_board", () ->
                    IForgeMenuType.create(SoundBoardMenu::readItem));

    public static final RegistryObject<MenuType<PortableJukeboxMenu>> PORTABLE_JUKEBOX =
            MENU_TYPES.register("portable_jukebox", () ->
                    IForgeMenuType.create(PortableJukeboxMenu::readItem));
}
