package io.wispforest.owo.ui.parsing;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.util.CommandOpenedScreen;
import io.wispforest.owo.ui.util.UISounds;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_437;

public class ConfigureHotReloadScreen extends BaseUIModelScreen<FlowLayout> implements CommandOpenedScreen {

    private final @Nullable class_437 parent;

    private final class_2960 modelId;
    private @Nullable Path reloadLocation;

    private LabelComponent fileNameLabel;

    public ConfigureHotReloadScreen(class_2960 modelId, @Nullable class_437 parent) {
        super(FlowLayout.class, DataSource.asset(Owo.id("configure_hot_reload")));
        this.parent = parent;

        this.modelId = modelId;
        this.reloadLocation = UIModelLoader.getHotReloadPath(this.modelId);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.childById(LabelComponent.class, "ui-model-label").text(class_2561.method_43469("text.owo.configure_hot_reload.model", this.modelId));
        this.fileNameLabel = rootComponent.childById(LabelComponent.class, "file-name-label");
        this.updateFileNameLabel();

        rootComponent.childById(ButtonComponent.class, "choose-button").onPress(button -> {
            CompletableFuture.runAsync(() -> {
                var newPath = TinyFileDialogs.tinyfd_openFileDialog("Choose UI Model source", null, null, null, false);
                if (newPath != null) this.reloadLocation = Path.of(newPath);
            }, class_156.method_18349()).whenComplete((unused, throwable) -> {
                this.updateFileNameLabel();
            });
        });

        rootComponent.childById(ButtonComponent.class, "save-button").onPress(button -> {
            UIModelLoader.setHotReloadPath(this.modelId, this.reloadLocation);
            this.method_25419();
        });

        rootComponent.childById(LabelComponent.class, "close-label").mouseDown().subscribe((click, doubled) -> {
            UISounds.playInteractionSound();
            this.method_25419();
            return true;
        });
    }

    @Override
    public void method_25419() {
        this.field_22787.method_1507(this.parent);
    }

    private void updateFileNameLabel() {
        this.fileNameLabel.text(class_2561.method_43469(
                "text.owo.configure_hot_reload.reload_from",
                this.reloadLocation == null ? class_2561.method_43471("text.owo.configure_hot_reload.reload_from.unset") : this.reloadLocation
        ));
    }
}
