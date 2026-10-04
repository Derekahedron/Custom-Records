package derekahedron.customrecords.client.gui;

import derekahedron.customrecords.client.util.CRClientUtil;
import derekahedron.customrecords.client.util.ClientJukeboxPlaybackManager;
import derekahedron.customrecords.inventory.PortableJukeboxMenu;
import derekahedron.customrecords.item.PortableJukeboxItemHandler;
import derekahedron.customrecords.network.CRPacketHandler;
import derekahedron.customrecords.network.UpdatePortableJukeboxAutoplayPacket;
import derekahedron.customrecords.network.UpdatePortableJukeboxPacket;
import derekahedron.customrecords.network.UpdatePortableJukeboxShuffleSeedPacket;
import derekahedron.customrecords.sound.JukeboxTrack;
import derekahedron.customrecords.util.CRUtil;
import derekahedron.customrecords.util.JukeboxPlayback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.RandomSupport;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.Optional;

import static derekahedron.customrecords.inventory.PortableJukeboxMenu.SLOT_SIZE;

public class PortableJukeboxScreen extends AbstractContainerScreen<PortableJukeboxMenu> {

    public static final ResourceLocation FOLDER = CRUtil.location("textures/gui/container/portable_jukebox/");
    public static final ResourceLocation BACKGROUND_TEXTURE = FOLDER.withSuffix("background.png");
    public static final ResourceLocation SELECTED_SLOT_TEXTURE = FOLDER.withSuffix("selected_slot.png");

    private static final int SELECTOR_SIZE = 24;
    private static final int SELECTOR_INSET = 4;

    @Nullable
    public DurationSlider durationSlider;

    public PortableJukeboxScreen(PortableJukeboxMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        imageWidth = PortableJukeboxMenu.WIDTH;
        imageHeight = PortableJukeboxMenu.HEIGHT;
        inventoryLabelY = PortableJukeboxMenu.BORDER
                + PortableJukeboxMenu.FONT_PADDING_TOP
                + PortableJukeboxMenu.FONT_HEIGHT
                + PortableJukeboxMenu.FONT_PADDING_BOTTOM
                + PortableJukeboxMenu.SLOT_SIZE * PortableJukeboxItemHandler.NUM_DISC_ROWS
                + PortableJukeboxMenu.CONTROLS_GAP
                + PortableJukeboxMenu.SLIDER_HEIGHT
                + PortableJukeboxMenu.CONTROLS_GAP
                + PortableJukeboxMenu.BUTTON_SIZE
                + PortableJukeboxMenu.FONT_PADDING_TOP;
    }

    @Override
    protected void init() {
        super.init();
        int x = PortableJukeboxMenu.BORDER
                + PortableJukeboxMenu.PADDING;
        int y = PortableJukeboxMenu.BORDER
                + PortableJukeboxMenu.FONT_PADDING_TOP
                + PortableJukeboxMenu.FONT_HEIGHT
                + PortableJukeboxMenu.FONT_PADDING_BOTTOM
                + SLOT_SIZE * PortableJukeboxItemHandler.NUM_DISC_ROWS
                + PortableJukeboxMenu.CONTROLS_GAP;

        durationSlider = addRenderableWidget(new DurationSlider(
                leftPos + x,
                topPos + y));

        y += PortableJukeboxMenu.SLIDER_HEIGHT
                + PortableJukeboxMenu.BUTTON_GAP;
        addRenderableWidget(new AutoplayButton(
                leftPos + x,
                topPos + y));

        x = (PortableJukeboxMenu.WIDTH
                - PortableJukeboxMenu.BUTTON_SIZE) / 2
                - PortableJukeboxMenu.BUTTON_GAP
                - PortableJukeboxMenu.BUTTON_SIZE;
        addRenderableWidget(new PreviousTrackButton(
                leftPos + x,
                topPos + y));

        x += PortableJukeboxMenu.BUTTON_SIZE
                + PortableJukeboxMenu.BUTTON_GAP;
        addRenderableWidget(new PlayPauseButton(
                leftPos + x,
                topPos + y));

        x += PortableJukeboxMenu.BUTTON_SIZE
                + PortableJukeboxMenu.BUTTON_GAP;
        addRenderableWidget(new NextTrackButton(
                leftPos + x,
                topPos + y));

        x = PortableJukeboxMenu.WIDTH
                - PortableJukeboxMenu.BORDER
                - PortableJukeboxMenu.PADDING
                - PortableJukeboxMenu.BUTTON_SIZE;
        addRenderableWidget(new ShuffleButton(
                leftPos + x,
                topPos + y));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderSelectedSlot(graphics);
        renderTooltip(graphics, mouseX, mouseY);
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

    public void renderSelectedSlot(GuiGraphics graphics) {
        PortableJukeboxItemHandler handler = getHandler().orElse(null);
        if (handler == null) return;
        if (handler.selectedSlot == -1) return;
        if (!handler.isItemValid(handler.selectedSlot, handler.getSelectedStack())) return;

        int column = handler.selectedSlot % PortableJukeboxItemHandler.NUM_RECORD_COLUMNS;
        int row = handler.selectedSlot / PortableJukeboxItemHandler.NUM_RECORD_COLUMNS;

        int x = leftPos
                + PortableJukeboxMenu.BORDER
                + PortableJukeboxMenu.PADDING
                + PortableJukeboxMenu.SLOT_BORDER
                + column * SLOT_SIZE
                - SELECTOR_INSET;
        int y = topPos
                + PortableJukeboxMenu.BORDER
                + PortableJukeboxMenu.FONT_PADDING_TOP
                + PortableJukeboxMenu.FONT_HEIGHT
                + PortableJukeboxMenu.FONT_PADDING_BOTTOM
                + PortableJukeboxMenu.SLOT_BORDER
                + row * SLOT_SIZE
                - SELECTOR_INSET;

        graphics.pose().pushPose();

        graphics.pose().translate(0.0F, 0.0F, 300.0F);
        graphics.blit(
                SELECTED_SLOT_TEXTURE,
                x, y,
                0, 0,
                SELECTOR_SIZE, SELECTOR_SIZE,
                SELECTOR_SIZE, SELECTOR_SIZE);

        graphics.pose().popPose();
    }

    public Optional<PortableJukeboxItemHandler> getHandler() {
        return menu.getHandler();
    }

    public Optional<JukeboxPlayback> getPlayback() {
        PortableJukeboxItemHandler handler = getHandler().orElse(null);
        if (handler == null) return Optional.empty();

        return ClientJukeboxPlaybackManager.getPlayback(
                menu.container.player().getUUID(),
                menu.slotReference)
                .filter(playback -> playback.isSameTrack(handler));
    }

    public int getTickCount() {
        JukeboxPlayback playback = getPlayback().orElse(null);
        if (playback != null) {
            return playback.getTrack(getRegistryAccess()).map(track -> playback.getTickCount(track.lengthInTicks()))
                    .orElse(0);
        } else {
            return getHandler().map(handler -> handler.tickCount).orElse(0);
        }
    }

    public boolean isPlaying() {
        return getPlayback().isPresent();
    }

    public Optional<JukeboxTrack> getTrack() {
        return getHandler()
                .flatMap(handler -> handler.getCurrentTrack(getRegistryAccess()));
    }

    public Player getPlayer() {
        return menu.container.player();
    }

    public Level getLevel() {
        return getPlayer().level();
    }

    public RegistryAccess getRegistryAccess() {
        return getLevel().registryAccess();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled;
        if (button == 1 && selectRecord()) {
            CRClientUtil.playClick();
            handled = true;
        } else {
            handled = super.mouseClicked(mouseX, mouseY, button);
        }

        if (getFocused() instanceof PortableJukeboxScreenButton
                || getFocused() instanceof DurationSlider) {
            setFocused(null);
        }
        return handled;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Widgets only receive releases while under the cursor, so finish a drag that ends elsewhere here
        if (button == 0 && durationSlider != null && durationSlider.isDragging()) {
            durationSlider.onRelease(mouseX, mouseY);
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void play(PortableJukeboxItemHandler handler, int tickCount) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        JukeboxTrack track = getTrack().orElse(null);
        if (track == null) return;

        // Start next track if pressing play at the end of the current track
        int newTickCount;
        if (tickCount < track.lengthInTicks()) {
            newTickCount = tickCount;
        } else if (handler.isAutoplay) {
            int selectedSlot = handler.getSlotAtOffset(handler.selectedSlot, 1, getRegistryAccess());
            if (selectedSlot == -1) return;

            handler.selectedSlot = selectedSlot;
            newTickCount = 0;
        } else {
            newTickCount = 0;
        }

        handler.createNewPlayback(getRegistryAccess(), newTickCount).ifPresent(playback -> {
            handler.tickCount = 0; // Set to 0 while playing
            handler.saveTickCount();

            ClientJukeboxPlaybackManager.announceTrack(player.getUUID(), menu.slotReference, playback);
            ClientJukeboxPlaybackManager.startTrack(player.getUUID(), menu.slotReference, playback);
            ClientJukeboxPlaybackManager.addPlayback(player.getUUID(), menu.slotReference, playback);

            CRPacketHandler.INSTANCE.sendToServer(
                    new UpdatePortableJukeboxPacket(
                            menu.slotReference,
                            handler.selectedSlot,
                            newTickCount,
                            true));
        });
    }

    public void pause(PortableJukeboxItemHandler handler, int tickCount) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        handler.tickCount = tickCount;
        handler.saveTickCount();

        ClientJukeboxPlaybackManager.removePlayback(player.getUUID(), menu.slotReference);
        CRPacketHandler.INSTANCE.sendToServer(
                new UpdatePortableJukeboxPacket(
                        menu.slotReference,
                        handler.selectedSlot,
                        tickCount,
                        false));
    }

    private boolean canSkip(PortableJukeboxItemHandler handler) {
        Level level = getLevel();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (i != handler.selectedSlot
                    && handler.isItemValid(i, handler.getStackInSlot(i))
                    && handler.getTrack(i, level.registryAccess()).isPresent()) {
                return true;
            }
        }

        return false;
    }

    public void skip(PortableJukeboxItemHandler handler, int offset) {
        int nextSlot = handler.getSlotAtOffset(handler.selectedSlot, offset, getRegistryAccess());
        if (nextSlot == -1) return;

        select(handler, nextSlot);
    }

    public void select(PortableJukeboxItemHandler handler, int slot) {
        boolean wasPlaying = isPlaying() && handler.getCurrentTrack(getRegistryAccess()).isPresent();
        handler.selectedSlot = slot;
        handler.saveSelectedSlot();

        if (wasPlaying) {
            play(handler, 0);
        } else {
            pause(handler, 0);
        }
    }

    public boolean seek(PortableJukeboxItemHandler handler, int tickCount) {
        JukeboxTrack track = handler.getCurrentTrack(getRegistryAccess()).orElse(null);
        if (track == null) return false;

        tickCount = Mth.clamp(tickCount, 0, Math.max(track.lengthInTicks(), 0));
        if (isPlaying()) {
            // Seeking to the end should pause at the start if autoplay is disabled
            if (tickCount >= track.lengthInTicks() && !handler.isAutoplay) {
                pause(handler, 0);
            } else {
                play(handler, tickCount);
            }
        } else {
            pause(handler, tickCount);
        }
        return true;
    }

    public boolean selectRecord() {
        if (hoveredSlot == null
                || hoveredSlot.container != menu.container) return false;
        if (!menu.getCarried().isEmpty()) return false;

        PortableJukeboxItemHandler handler = getHandler().orElse(null);
        if (handler == null) return false;

        int slot = hoveredSlot.getSlotIndex();
        if (!handler.isItemValid(slot, handler.getStackInSlot(slot))) return false;

        select(handler, slot);
        return true;
    }

    public class DurationSlider extends AbstractWidget {

        public static final ResourceLocation BACKGROUND_TEXTURE = FOLDER.withSuffix("slider/background.png");
        public static final ResourceLocation FILL_TEXTURE = FOLDER.withSuffix("slider/fill.png");
        public static final ResourceLocation HANDLE_TEXTURE = FOLDER.withSuffix("slider/handle.png");
        public static final ResourceLocation HANDLE_HIGHLIGHTED_TEXTURE = FOLDER.withSuffix("slider/handle_highlighted.png");
        public static final ResourceLocation HANDLE_DISABLED_TEXTURE = FOLDER.withSuffix("slider/handle_disabled.png");

        public static final int HANDLE_WIDTH = 11;
        public static final int HANDLE_HEIGHT = 10;

        public static final String TRANSLATION_KEY = "container.customrecords.portable_jukebox.duration";
        public static final MutableComponent MESSAGE = Component.translatable(TRANSLATION_KEY);
        public static final String TIME_TRANSLATION_KEY = TRANSLATION_KEY + ".time";
        public static final MutableComponent NO_TRACK = Component.translatable(TRANSLATION_KEY + ".no_track");

        private static final int KEY_SEEK_TICKS = 5 * 20;

        private boolean dragging;
        @Nullable
        private Integer keySeekTickCount;

        public DurationSlider(int x, int y) {
            super(x, y, PortableJukeboxMenu.SLIDER_WIDTH, PortableJukeboxMenu.SLIDER_HEIGHT, MESSAGE);
        }

        public boolean isDragging() {
            return dragging;
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            this.active = getTrack().isPresent();
            super.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int x = getX();
            int y = getY();

            graphics.blit(
                    BACKGROUND_TEXTURE,
                    x, y,
                    0, 0,
                    getWidth(), getHeight(),
                    getWidth(), getHeight());

            JukeboxTrack track = getTrack().orElse(null);

            if (active && track != null && track.lengthInTicks() > 0) {
                int lengthInTicks = track.lengthInTicks();
                int tickCount;
                float progress;
                if (isDragging()) {
                    progress = progressAt(mouseX);
                    tickCount = (int) (lengthInTicks * progress);
                } else if (keySeekTickCount != null) {
                    tickCount = keySeekTickCount;
                    progress = Mth.clamp(tickCount / (float) lengthInTicks, 0.0F, 1.0F);
                } else {
                    tickCount = getTickCount();
                    progress = Mth.clamp(tickCount / (float) lengthInTicks, 0.0F, 1.0F);
                }

                int sliderOffset = Math.round(getSliderRange() * progress);
                boolean highlighted = dragging || isHoveredOrFocused();

                graphics.blit(
                        FILL_TEXTURE,
                        x + 1, y + 1,
                        0, 0,
                        sliderOffset, getHeight() - 2,
                        getWidth() - 2, getHeight() - 2);

                graphics.blit(
                        highlighted ? HANDLE_HIGHLIGHTED_TEXTURE : HANDLE_TEXTURE,
                        x + 1 + sliderOffset, y + 1,
                        0, 0,
                        HANDLE_WIDTH, HANDLE_HEIGHT,
                        HANDLE_WIDTH, HANDLE_HEIGHT);

                graphics.drawCenteredString(
                        font,
                        Component.translatable(TIME_TRANSLATION_KEY,
                                CRUtil.ticksToMinutes(tickCount), String.format("%02d", CRUtil.ticksToSeconds(tickCount)),
                                CRUtil.ticksToMinutes(lengthInTicks), String.format("%02d", CRUtil.ticksToSeconds(lengthInTicks))),
                        x + getWidth() / 2,
                        y + getHeight() / 2 - font.lineHeight / 2,
                        0xFFFFFFFF);
            } else {
                graphics.blit(
                        HANDLE_DISABLED_TEXTURE,
                        x + 1, y + 1,
                        0, 0,
                        HANDLE_WIDTH, HANDLE_HEIGHT,
                        HANDLE_WIDTH, HANDLE_HEIGHT);

                graphics.drawCenteredString(
                        font,
                        NO_TRACK,
                        x + getWidth() / 2,
                        y + getHeight() / 2 - font.lineHeight / 2,
                        0xFFFFFFFF);
            }
        }


        public float progressAt(double mouseX) {
            double knobLeft = mouseX - (getX() + 1) - HANDLE_WIDTH / 2.0;
            return Mth.clamp((float) (knobLeft / getSliderRange()), 0.0F, 1.0F);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            dragging = true;
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
            if (!dragging) return;
            dragging = false;

            PortableJukeboxItemHandler handler = getHandler().orElse(null);
            if (handler == null) return;

            JukeboxTrack track = getTrack().orElse(null);
            if (track == null) return;

            if (seek(handler, Math.round(progressAt(mouseX) * track.lengthInTicks()))) {
                CRClientUtil.playClick();
            }
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!isActive()) return false;

            int direction;
            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT -> direction = -1;
                case GLFW.GLFW_KEY_RIGHT -> direction = 1;
                default -> {
                    return false;
                }
            }

            JukeboxTrack track = getTrack().orElse(null);
            if (track == null) return false;

            if (keySeekTickCount == null) {
                keySeekTickCount = getTickCount();
            }

            keySeekTickCount = Mth.clamp(
                    keySeekTickCount + direction * KEY_SEEK_TICKS,
                    0,
                    Math.max(track.lengthInTicks(), 0));
            return true;
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            if (keyCode != GLFW.GLFW_KEY_LEFT && keyCode != GLFW.GLFW_KEY_RIGHT) return false;
            return applyKeySeek();
        }

        public boolean applyKeySeek() {
            if (keySeekTickCount == null) return false;
            int tickCount = keySeekTickCount;
            keySeekTickCount = null;

            PortableJukeboxItemHandler handler = getHandler().orElse(null);
            if (handler == null) return false;

            seek(handler, tickCount);
            return true;
        }

        @Override
        public void playDownSound(SoundManager soundManager) {
            // Click plays on release instead
        }

        @Override
        protected MutableComponent createNarrationMessage() {
            return Component.translatable("gui.narrate.slider", getMessage());
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            output.add(NarratedElementType.TITLE, createNarrationMessage());

            JukeboxTrack track = getTrack().orElse(null);
            if (track != null) {
                int lengthInTicks = track.lengthInTicks();
                int tickCount = getTickCount();

                output.add(NarratedElementType.HINT,
                        Component.translatable(TIME_TRANSLATION_KEY + ".narrator",
                                CRUtil.ticksToMinutes(tickCount), String.format("%02d", CRUtil.ticksToSeconds(tickCount)),
                                CRUtil.ticksToMinutes(lengthInTicks), String.format("%02d", CRUtil.ticksToSeconds(lengthInTicks))));
            }

            if (active) {
                output.add(NarratedElementType.USAGE, Component.translatable(isFocused()
                        ? "narration.slider.usage.focused"
                        : "narration.slider.usage.hovered"));
            }
        }

        public int getSliderRange() {
            return getWidth() - 2 - HANDLE_WIDTH;
        }
    }

    public static abstract class PortableJukeboxScreenButton extends AbstractButton {

        public static final ResourceLocation TEXTURE = FOLDER.withSuffix("button/normal.png");
        public static final ResourceLocation HOVERED_TEXTURE = FOLDER.withSuffix("button/hovered.png");
        public static final ResourceLocation PRESSED_TEXTURE = FOLDER.withSuffix("button/pressed.png");
        public static final ResourceLocation PRESSED_HOVERED_TEXTURE = FOLDER.withSuffix("button/pressed_hovered.png");
        public static final ResourceLocation DISABLED_TEXTURE = FOLDER.withSuffix("button/disabled.png");

        public PortableJukeboxScreenButton(int x, int y, int width, int height, Component message) {
            super(x, y, width, height, message);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            graphics.blit(
                    getBackgroundTexture(),
                    getX(), getY(),
                    0, 0,
                    getWidth(), getHeight(),
                    getWidth(), getHeight());
            graphics.blit(
                    getIconTexture(),
                    getX() + 1, getY() + 1,
                    0, 0,
                    getWidth() - 2, getHeight() - 2,
                    getWidth() - 2, getHeight() - 2);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }

        public ResourceLocation getBackgroundTexture() {
            if (!isActive()) {
                return DISABLED_TEXTURE;
            } else if (isPressed()) {
                if (isHoveredOrFocused()) {
                    return PRESSED_HOVERED_TEXTURE;
                } else {
                    return PRESSED_TEXTURE;
                }
            } else if (isHoveredOrFocused()) {
                return HOVERED_TEXTURE;
            } else {
                return TEXTURE;
            }
        }

        public boolean isPressed() {
            return false;
        }

        public abstract ResourceLocation getIconTexture();
    }

    public class PreviousTrackButton extends PortableJukeboxScreenButton {

        public static final MutableComponent MESSAGE =
                Component.translatable("container.customrecords.portable_jukebox.previous_track");
        public static final ResourceLocation ICON_TEXTURE = FOLDER.withSuffix("icon/previous.png");

        public PreviousTrackButton(int x, int y) {
            super(x, y, PortableJukeboxMenu.BUTTON_SIZE, PortableJukeboxMenu.BUTTON_SIZE, MESSAGE);
        }

        @Override
        public void onPress() {
            getHandler()
                    .filter(PortableJukeboxScreen.this::canSkip)
                    .ifPresent(handler -> skip(handler, -1));
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            this.active = getHandler().map(PortableJukeboxScreen.this::canSkip).orElse(false);
            super.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public ResourceLocation getIconTexture() {
            return ICON_TEXTURE;
        }
    }

    public class PlayPauseButton extends PortableJukeboxScreenButton {

        public static final MutableComponent PLAY_MESSAGE =
                Component.translatable("container.customrecords.portable_jukebox.play");
        public static final MutableComponent PAUSE_MESSAGE =
                Component.translatable("container.customrecords.portable_jukebox.pause");
        public static final ResourceLocation PLAY_ICON_TEXTURE = FOLDER.withSuffix("icon/play.png");
        public static final ResourceLocation PAUSE_ICON_TEXTURE = FOLDER.withSuffix("icon/pause.png");

        public PlayPauseButton(int x, int y) {
            super(x, y, PortableJukeboxMenu.BUTTON_SIZE, PortableJukeboxMenu.BUTTON_SIZE, PLAY_MESSAGE);
        }

        @Override
        public void onPress() {
            PortableJukeboxItemHandler handler = getHandler().orElse(null);
            if (handler == null) return;

            JukeboxTrack track = getTrack().orElse(null);
            if (track == null) return;

            getPlayback().ifPresentOrElse(
                    playback -> pause(handler, playback.getTickCount(track.lengthInTicks())),
                    () -> play(handler, handler.tickCount));
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            this.active = getTrack().isPresent();
            super.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean isPressed() {
            return isPlaying();
        }

        @Override
        public Component getMessage() {
            return isPlaying() ? PAUSE_MESSAGE : PLAY_MESSAGE;
        }

        @Override
        public ResourceLocation getIconTexture() {
            return isPlaying() ? PAUSE_ICON_TEXTURE : PLAY_ICON_TEXTURE;
        }
    }

    public class NextTrackButton extends PortableJukeboxScreenButton {

        public static final MutableComponent MESSAGE =
                Component.translatable("container.customrecords.portable_jukebox.next_track");
        public static final ResourceLocation ICON_TEXTURE = FOLDER.withSuffix("icon/next.png");

        public NextTrackButton(int x, int y) {
            super(x, y, PortableJukeboxMenu.BUTTON_SIZE, PortableJukeboxMenu.BUTTON_SIZE, MESSAGE);
        }

        @Override
        public void onPress() {
            getHandler()
                    .filter(PortableJukeboxScreen.this::canSkip)
                    .ifPresent(handler -> skip(handler, 1));
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            this.active = getHandler().map(PortableJukeboxScreen.this::canSkip).orElse(false);
            super.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public ResourceLocation getIconTexture() {
            return ICON_TEXTURE;
        }
    }

    public class AutoplayButton extends PortableJukeboxScreenButton {

        public static final MutableComponent MESSAGE =
                Component.translatable("container.customrecords.portable_jukebox.autoplay");
        public static final ResourceLocation ICON_TEXTURE = FOLDER.withSuffix("icon/autoplay.png");

        public AutoplayButton(int x, int y) {
            super(x, y, PortableJukeboxMenu.BUTTON_SIZE, PortableJukeboxMenu.BUTTON_SIZE, MESSAGE);
        }

        @Override
        public void onPress() {
            getHandler().ifPresent(handler -> {
                handler.isAutoplay = !handler.isAutoplay;
                handler.saveAutoplay();
                CRPacketHandler.INSTANCE.sendToServer(new UpdatePortableJukeboxAutoplayPacket(
                        menu.slotReference,
                        handler.isAutoplay));
            });
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            setTooltip(getHandler()
                    .map(handler ->
                            Tooltip.create(CommonComponents.optionStatus(MESSAGE, handler.isAutoplay)))
                    .orElse(null));
            super.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean isPressed() {
            return getHandler()
                    .map(handler -> handler.isAutoplay)
                    .orElse(false);
        }

        @Override
        public ResourceLocation getIconTexture() {
            return ICON_TEXTURE;
        }
    }

    public class ShuffleButton extends PortableJukeboxScreenButton {

        public static final MutableComponent MESSAGE = Component.translatable("container.customrecords.portable_jukebox.shuffle");
        public static final ResourceLocation ICON_TEXTURE = FOLDER.withSuffix("icon/shuffle.png");

        public ShuffleButton(int x, int y) {
            super(x, y, PortableJukeboxMenu.BUTTON_SIZE, PortableJukeboxMenu.BUTTON_SIZE, MESSAGE);
        }

        @Override
        public void onPress() {
            getHandler().ifPresent(handler -> {
                if (handler.isShuffled()) {
                    handler.shuffleSeed = null;
                } else {
                    handler.shuffleSeed = RandomSupport.generateUniqueSeed();
                }
                handler.saveShuffleSeed();
                CRPacketHandler.INSTANCE.sendToServer(new UpdatePortableJukeboxShuffleSeedPacket(
                        menu.slotReference,
                        Optional.ofNullable(handler.shuffleSeed)));
            });
        }

        @Override
        public boolean isPressed() {
            return getHandler()
                    .map(PortableJukeboxItemHandler::isShuffled)
                    .orElse(false);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            setTooltip(getHandler()
                    .map(handler ->
                            Tooltip.create(CommonComponents.optionStatus(MESSAGE, handler.isShuffled())))
                    .orElse(null));
            super.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public ResourceLocation getIconTexture() {
            return ICON_TEXTURE;
        }
    }
}
