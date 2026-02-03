package io.wispforest.owo.braid.core;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.wispforest.owo.Owo;
import net.minecraft.class_10799;
import org.jetbrains.annotations.ApiStatus;

public class BraidRenderPipelines {
    public static final RenderPipeline TEXTURED_DEFAULT = RenderPipeline.builder(class_10799.field_56864)
        .withLocation(Owo.id("pipeline/braid_textured_default"))
        .build();

    public static final RenderPipeline TEXTURED_NEAREST = RenderPipeline.builder(class_10799.field_56864)
        .withLocation(Owo.id("pipeline/braid_textured_nearest"))
        .build();

    public static final RenderPipeline TEXTURED_BILINEAR = RenderPipeline.builder(class_10799.field_56864)
        .withLocation(Owo.id("pipeline/braid_textured_bilinear"))
        .build();

    @ApiStatus.Internal
    public static void register() {
        class_10799.method_67887(TEXTURED_DEFAULT);
        class_10799.method_67887(TEXTURED_NEAREST);
        class_10799.method_67887(TEXTURED_BILINEAR);
    }
}
