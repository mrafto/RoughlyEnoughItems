package net.minecraft.client.gui;

import me.shedaniel.rei.impl.client.gui.fabric.REIGuiGraphicsCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;

/**
 * Compatibility shim for code that still targets the pre-26.1 GuiGraphics type.
 */
public class GuiGraphics extends GuiGraphicsExtractor {
    private static final Field MINECRAFT_FIELD = findField("minecraft");
    private static final Field GUI_RENDER_STATE_FIELD = findField("guiRenderState");
    private static final Field MOUSE_X_FIELD = findField("mouseX");
    private static final Field MOUSE_Y_FIELD = findField("mouseY");
    public GuiGraphics(Minecraft minecraft, GuiRenderState renderState, int guiWidth, int guiHeight) {
        super(minecraft, renderState, guiWidth, guiHeight);
    }

    public GuiGraphics(GuiGraphicsExtractor extractor) {
        this(readField(MINECRAFT_FIELD, extractor), readField(GUI_RENDER_STATE_FIELD, extractor),
                readIntField(MOUSE_X_FIELD, extractor), readIntField(MOUSE_Y_FIELD, extractor));
    }

    public void drawString(Font font, FormattedCharSequence text, int x, int y, int color) {
        text(font, text, x, y, color);
    }

    public void drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean shadow) {
        text(font, text, x, y, color, shadow);
    }

    public void drawString(Font font, Component text, int x, int y, int color) {
        text(font, text, x, y, color);
    }

    public void drawString(Font font, Component text, int x, int y, int color, boolean shadow) {
        text(font, text, x, y, color, shadow);
    }

    public void drawString(Font font, String text, int x, int y, int color) {
        text(font, text, x, y, color);
    }

    public void drawString(Font font, String text, int x, int y, int color, boolean shadow) {
        text(font, text, x, y, color, shadow);
    }

    public void drawCenteredString(Font font, String text, int x, int y, int color) {
        centeredText(font, text, x, y, color);
    }

    public void drawCenteredString(Font font, Component text, int x, int y, int color) {
        centeredText(font, text, x, y, color);
    }

    public void drawWordWrap(Font font, Component text, int x, int y, int width, int color) {
        textWithWordWrap(font, text, x, y, width, color);
    }

    public void renderDeferredElements() {
        extractDeferredElements(readIntField(MOUSE_X_FIELD, this), readIntField(MOUSE_Y_FIELD, this), 0.0F);
    }

    public void renderOutline(int x, int y, int width, int height, int color) {
        outline(x, y, width, height, color);
    }

    public void hLine(int minX, int maxX, int y, int color) {
        horizontalLine(minX, maxX, y, color);
    }

    public void vLine(int x, int minY, int maxY, int color) {
        verticalLine(x, minY, maxY, color);
    }

    public void renderItem(ItemStack stack, int x, int y) {
        item(stack, x, y);
    }

    public void renderItem(ItemStack stack, int x, int y, int seed) {
        item(stack, x, y, seed);
    }

    public void renderItemDecorations(Font font, ItemStack stack, int x, int y) {
        itemDecorations(font, stack, x, y);
    }

    public void renderItemDecorations(Font font, ItemStack stack, int x, int y, String text) {
        itemDecorations(font, stack, x, y, text);
    }

    public void withFreshScissorStack(Runnable runnable) {
        REIGuiGraphicsCompat.withFreshScissorStack(this, runnable);
    }

    private static Field findField(String name) {
        try {
            Field field = GuiGraphicsExtractor.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to resolve GuiGraphicsExtractor field: " + name, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T readField(Field field, Object instance) {
        try {
            return (T) field.get(instance);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to read GuiGraphicsExtractor field: " + field.getName(), e);
        }
    }

    private static int readIntField(Field field, Object instance) {
        try {
            return field.getInt(instance);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to read GuiGraphicsExtractor field: " + field.getName(), e);
        }
    }
}
