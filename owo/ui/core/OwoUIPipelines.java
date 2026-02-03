package io.wispforest.owo.ui.core;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import org.jetbrains.annotations.ApiStatus;

public final class OwoUIPipelines {

    public static final RenderPipeline.Snippet HSV_SNIPPET = RenderPipeline.builder(class_10799.field_60125)
        .withVertexShader(class_2960.method_60656("core/gui"))
        .withFragmentShader(class_2960.method_60655("owo", "core/spectrum"))
        .withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382)
        .withBlend(BlendFunction.TRANSLUCENT)
        .buildSnippet();

    public static final RenderPipeline GUI_HSV = RenderPipeline.builder(HSV_SNIPPET)
        .withLocation(class_2960.method_60655("owo", "pipeline/gui_hsv"))
        .build();

    public static final RenderPipeline GUI_BLUR = RenderPipeline.builder(class_10799.field_60125)
        .withLocation(class_2960.method_60655("owo", "pipeline/gui_blur"))
        .withVertexFormat(class_290.field_1592, VertexFormat.class_5596.field_27382)
        .withVertexShader(class_2960.method_60655("owo", "core/blur"))
        .withFragmentShader(class_2960.method_60655("owo", "core/blur"))
        .withSampler("InputSampler")
        .withUniform("BlurSettings", class_10789.field_60031)
        .build();

    public static final RenderPipeline GUI_TRIANGLE_FAN = RenderPipeline.builder(class_10799.field_56863)
        .withLocation(class_2960.method_60655("owo", "pipeline/gui_triangle_fan"))
        .withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27381)
        .build();

    public static final RenderPipeline GUI_TRIANGLE_STRIP = RenderPipeline.builder(class_10799.field_56863)
        .withLocation(class_2960.method_60655("owo", "pipeline/gui_triangle_strip"))
        .withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27380)
        .build();

    public static final RenderPipeline GUI_TEXTURED_NO_BLEND = RenderPipeline.builder(class_10799.field_56864)
        .withLocation(class_2960.method_60655("owo", "pipeline/gui_textured"))
        .withoutBlend()
        .build();

    @ApiStatus.Internal
    public static void register() {
        class_10799.method_67887(GUI_HSV);
        class_10799.method_67887(GUI_BLUR);
        class_10799.method_67887(GUI_TRIANGLE_FAN);
        class_10799.method_67887(GUI_TRIANGLE_STRIP);
        class_10799.method_67887(GUI_TEXTURED_NO_BLEND);
    }
}
