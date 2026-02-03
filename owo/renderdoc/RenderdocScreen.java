package io.wispforest.owo.renderdoc;

import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.CheckboxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.CommandOpenedScreen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import net.minecraft.class_11908;
import net.minecraft.class_124;
import net.minecraft.class_2561;

public class RenderdocScreen extends BaseOwoScreen<FlowLayout> implements CommandOpenedScreen {

    private int ticks = 0;
    private boolean setCaptureKey = false;
    private @Nullable RenderDoc.Key scheduledKey = null;

    private ButtonComponent captureKeyButton = null;
    private LabelComponent captureLabel = null;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, UIContainers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.surface(Surface.VANILLA_TRANSLUCENT);

        var overlayState = RenderDoc.getOverlayOptions();
        rootComponent.child(
                        UIContainers.verticalFlow(Sizing.content(), Sizing.content())
                                .child(UIComponents.label(class_2561.method_43470("RenderDoc Controls")).shadow(true).margins(Insets.top(5).withBottom(10)))
                                .child(
                                        UIContainers.grid(Sizing.content(), Sizing.content(), 2, 2)
                                                .child(overlayControl(class_2561.method_30163("Enabled"), overlayState, RenderDoc.OverlayOption.ENABLED), 0, 0)
                                                .child(overlayControl(class_2561.method_30163("Capture List"), overlayState, RenderDoc.OverlayOption.CAPTURE_LIST), 0, 1)
                                                .child(overlayControl(class_2561.method_30163("Frame Rate"), overlayState, RenderDoc.OverlayOption.FRAME_RATE), 1, 0)
                                                .child(overlayControl(class_2561.method_30163("Frame Number"), overlayState, RenderDoc.OverlayOption.FRAME_NUMBER), 1, 1)
                                )
                                .child(
                                        UIComponents.box(Sizing.fixed(175), Sizing.fixed(1))
                                                .color(Color.ofFormatting(class_124.field_1063))
                                                .fill(true)
                                                .margins(Insets.vertical(5))
                                )
                                .child(
                                        UIContainers.grid(Sizing.content(), Sizing.content(), 2, 2)
                                                .child(UIComponents.button(
                                                        class_2561.method_30163("Launch UI"),
                                                        (ButtonComponent button) -> RenderDoc.launchReplayUI(true)
                                                ).horizontalSizing(Sizing.fixed(90)).margins(Insets.of(2)), 0, 0)
                                                .child((this.captureKeyButton = UIComponents.button(
                                                        class_2561.method_30163("Capture Hotkey"),
                                                        (ButtonComponent button) -> {
                                                            button.field_22763 = false;
                                                            button.method_25355(class_2561.method_30163("Press..."));

                                                            this.setCaptureKey = true;
                                                        }
                                                )).horizontalSizing(Sizing.fixed(90)).margins(Insets.of(2)), 1, 0)
                                                .child(UIComponents.button(
                                                        class_2561.method_30163("Capture Frame"),
                                                        (ButtonComponent button) -> RenderDoc.triggerCapture()
                                                ).horizontalSizing(Sizing.fixed(90)).margins(Insets.of(2)), 0, 1)
                                                .child(this.captureLabel = UIComponents.label(
                                                        this.createCapturesText()
                                                ), 1, 1)
                                                .verticalAlignment(VerticalAlignment.CENTER).horizontalAlignment(HorizontalAlignment.CENTER)
                                )
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .padding(Insets.of(5))
                                .surface(Surface.flat(0x77000000).and(Surface.outline(0x77000000)))
                )
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER);
    }

    @Override
    public void method_25393() {
        super.method_25393();
        if (++this.ticks % 10 != 0) return;

        if (this.scheduledKey != null) {
            RenderDoc.setCaptureKeys(this.scheduledKey);
            this.scheduledKey = null;
        }

        this.captureLabel.text(this.createCapturesText());
    }

    @Override
    public boolean method_25404(class_11908 input) {
        if (this.setCaptureKey) {
            this.captureKeyButton.field_22763 = true;
            this.captureKeyButton.method_25355(class_2561.method_30163("Capture Hotkey"));

            this.setCaptureKey = false;

            var key = RenderDoc.Key.fromGLFW(input.comp_4795());
            if (key != null) {
                this.ticks = 0;
                this.scheduledKey = key;

                return true;
            }
        }
        return super.method_25404(input);
    }

    private class_2561 createCapturesText() {
        return TextOps.withColor("Captures: §" + RenderDoc.getNumCaptures(), TextOps.color(class_124.field_1068), 0x00D7FF);
    }

    private static CheckboxComponent overlayControl(class_2561 name, EnumSet<RenderDoc.OverlayOption> state, RenderDoc.OverlayOption option) {
        var checkbox = UIComponents.checkbox(name);
        checkbox.margins(Insets.of(3)).horizontalSizing(Sizing.fixed(100));
        checkbox.checked(state.contains(option));
        checkbox.onChanged(enabled -> {
            if (enabled) {
                RenderDoc.enableOverlayOptions(option);
            } else {
                RenderDoc.disableOverlayOptions(option);
            }
        });
        return checkbox;
    }
}
