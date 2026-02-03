package io.wispforest.owo.braid.util;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import io.wispforest.owo.Owo;
import io.wispforest.owo.braid.core.Surface;
import io.wispforest.owo.mixin.braid.GameRendererAccessor;
import io.wispforest.owo.mixin.braid.GuiRendererAccessor;
import io.wispforest.owo.util.pond.BraidGuiRendererExtension;
import java.util.ArrayList;
import net.minecraft.class_11228;
import net.minecraft.class_11246;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_758;

public class BraidGuiRenderer extends class_11228 {

    private final class_310 client;

    public BraidGuiRenderer(class_310 client) {
        super(
            new class_11246(),
            client.method_22940().method_23000(),
            client.field_1773.method_72910(),
            client.field_1773.method_72911(),
            new ArrayList<>(((GuiRendererAccessor) ((GameRendererAccessor) client.field_1773).owo$getGuiRenderer()).owo$getPictureInPictureRenderers().values())
        );
        this.client = client;
    }

    public class_332 newGraphics(double mouseX, double mouseY) {
        this.trySetFabricState();
        return new class_332(
            this.client,
            ((GuiRendererAccessor) this).owo$getRenderState(),
            (int) mouseX, (int) mouseY
        );
    }

    private boolean fabricStateSet = false;
    private void trySetFabricState() {
        if (this.fabricStateSet) {
            return;
        }

        try {
            var initField = class_11228.class.getDeclaredField("hasFabricInitialized");
            initField.setAccessible(true);
            initField.set(this, true);

            var commandQueueField = class_11228.class.getDeclaredField("orderedRenderCommandQueue");
            commandQueueField.setAccessible(true);
            commandQueueField.set(this, this.client.field_1773.method_72910());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Owo.LOGGER.warn("Failed to apply braid's Fabric API GuiRendererMixin workaround, there might be crashes with texture and window surfaces");
        } finally {
            this.fabricStateSet = true;
        }
    }

    public void render(Target target) {
        ((BraidGuiRendererExtension) this).owo$setTarget(target);
        this.method_70890(((GameRendererAccessor) this.client.field_1773).owo$getFogRenderer().method_71109(class_758.class_4596.field_60101));
    }

    @Override
    @Deprecated
    public void method_70890(GpuBufferSlice fogBuffer) {
        super.method_70890(fogBuffer);
    }

    public record Target(class_276 framebuffer, Surface surface) {}
}
