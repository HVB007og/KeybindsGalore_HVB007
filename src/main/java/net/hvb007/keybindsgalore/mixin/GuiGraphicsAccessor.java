package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes GuiGraphics#bufferSource (MultiBufferSource.BufferSource) so that
 * TriangleStripRenderer can submit vertices to the same shared render buffer
 * used by GuiGraphics. This avoids the overhead of creating a separate buffer
 * and ensures proper flushing via the normal render lifecycle.
 *
 * Required because BufferUploader/Tesselator were removed in 1.21.5 — all
 * GUI rendering must go through a registered RenderType layer.
 */
@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {
    @Accessor("bufferSource")
    MultiBufferSource.BufferSource kbg$getBufferSource();
}
