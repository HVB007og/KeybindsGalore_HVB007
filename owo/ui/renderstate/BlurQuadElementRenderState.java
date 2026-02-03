package io.wispforest.owo.ui.renderstate;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import io.wispforest.owo.ui.core.OwoUIPipelines;
import io.wispforest.owo.ui.event.ClientRenderCallback;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11231;
import net.minecraft.class_11244;
import net.minecraft.class_11280;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_6367;
import net.minecraft.class_8030;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector2i;

import java.nio.ByteBuffer;
import java.util.Map;

public record BlurQuadElementRenderState(
    RenderPipeline pipeline,
    Matrix3x2f pose,
    class_8030 bounds,
    class_8030 scissorArea,
    class_11231 textureSetup
) implements class_11244 {

    public static Uniforms uniforms;
    public static class_276 input;
    public static GpuTextureView inputView;

    @ApiStatus.Internal
    public static void initialize(class_310 client) {
        uniforms = new Uniforms();

        var window = client.method_22683();

        input = new class_6367("owo_blur_input", window.method_4489(), window.method_4506(), false);
        inputView = RenderSystem.getDevice().createTextureView(input.method_30277());

        WindowResizeCallback.EVENT.register((innerClient, innerWindow) -> {
            if (input == null) return;
            input.method_1234(innerWindow.method_4489(), innerWindow.method_4506());

            inputView.close();
            inputView = RenderSystem.getDevice().createTextureView(input.method_30277());
        });

        ClientRenderCallback.AFTER.register($ -> {
            uniforms.clear();
        });
    }

    @ApiStatus.Internal
    public BlurQuadElementRenderState {}

    public BlurQuadElementRenderState(Matrix3x2f pose, class_8030 bounds, class_8030 scissorArea, int directions, float quality, float size) {
        this(OwoUIPipelines.GUI_BLUR, pose, bounds, scissorArea, createTextureSetup(directions, quality, size));
    }

    @Override
    public void method_70917(class_4588 vertices) {
        vertices.method_70815(this.pose(), (float) this.bounds.method_49620(), (float) this.bounds.method_49618());
        vertices.method_70815(this.pose(), (float) this.bounds.method_49620(), (float) this.bounds.method_49619());
        vertices.method_70815(this.pose(), (float) this.bounds.method_49621(), (float) this.bounds.method_49619());
        vertices.method_70815(this.pose(), (float) this.bounds.method_49621(), (float) this.bounds.method_49618());
    }

    @Override
    public RenderPipeline comp_4055() {
        return this.pipeline;
    }

    @Override
    public class_11231 comp_4056() {
        return this.textureSetup;
    }

    @Override
    public @Nullable class_8030 comp_4069() {
        return this.scissorArea;
    }

    @Override
    public @Nullable class_8030 comp_4274() {
        return this.scissorArea != null ? this.scissorArea.method_49701(this.bounds) : this.bounds;
    }

    // ---

    private static final Map<class_11231, BlurSetup> blurSetups = new MapMaker().weakKeys().makeMap();

    public static boolean hasBlurSetupFor(class_11231 textureSetup) {
        return blurSetups.containsKey(textureSetup);
    }

    public static @Nullable BlurSetup getBlurSetupOf(class_11231 textureSetup) {
        return blurSetups.get(textureSetup);
    }

    private static class_11231 createTextureSetup(int directions, float quality, float size) {
        var setup = class_11231.method_70900(null, null);
        blurSetups.put(setup, new BlurSetup(directions, quality, size));
        return setup;
    }

    public record BlurSetup(int directions, float quality, float size) {}

    // ---

    public static class Uniforms {
        public static final int SIZE = new Std140SizeCalculator().putVec2().putFloat().putFloat().putFloat().get();
        private final class_11280<Value> storage = new class_11280<>("Blur Settings UBO", SIZE, 4);

        public void clear() {
            this.storage.method_71100();
        }

        public GpuBufferSlice write(Vector2i inputResolution, int directions, float quality, float size) {
            return this.storage.method_71102(new Value(inputResolution, directions, quality, size));
        }

        @Environment(EnvType.CLIENT)
        public record Value(Vector2i inputResolution, int directions, float quality, float size) implements class_11280.class_11281 {
            @Override
            public void method_71104(ByteBuffer buffer) {
                Std140Builder.intoBuffer(buffer)
                    .putVec2(inputResolution.x, inputResolution.y)
                    .putFloat(this.directions)
                    .putFloat(this.quality)
                    .putFloat(this.size);
            }
        }
    }
}
