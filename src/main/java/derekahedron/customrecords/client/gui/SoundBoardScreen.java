package derekahedron.customrecords.client.gui;

import derekahedron.customrecords.inventory.SoundBoardMenu;
import derekahedron.customrecords.util.CRUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SoundBoardScreen extends AbstractContainerScreen<SoundBoardMenu> {

    public static final ResourceLocation FOLDER = CRUtil.location("textures/gui/container/sound_board/");
    public static final ResourceLocation BACKGROUND_TEXTURE = FOLDER.withSuffix("background.png");

    public SoundBoardScreen(SoundBoardMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        imageWidth = SoundBoardMenu.WIDTH;
        imageHeight = SoundBoardMenu.HEIGHT;
        inventoryLabelY = SoundBoardMenu.BORDER
                + SoundBoardMenu.FONT_PADDING_BOTTOM
                + SoundBoardMenu.FONT_HEIGHT
                + SoundBoardMenu.FONT_PADDING_TOP
                + SoundBoardMenu.SLOT_SIZE * 2
                + SoundBoardMenu.FONT_PADDING_TOP;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(
                BACKGROUND_TEXTURE,
                leftPos,
                topPos,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
