package io.wispforest.owo.braid.util.kdl;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.endec.Endec;
import io.wispforest.owo.Owo;
import io.wispforest.owo.braid.core.AppState;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.object.EntityWidget;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import net.minecraft.class_11352;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_151;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_2960;
import net.minecraft.class_3730;
import net.minecraft.class_7923;
import net.minecraft.class_8942;

public class KdlEntityWidget extends StatefulWidget {

    public final double scale;
    public final EntitySpec spec;

    public final EntityWidget.DisplayMode mode;
    public final boolean scaleToFit;
    public final boolean showNametag;

    public KdlEntityWidget(double scale, EntitySpec spec, EntityWidget.DisplayMode mode, boolean scaleToFit, boolean showNametag) {
        this.scale = scale;
        this.spec = spec;
        this.mode = mode;
        this.scaleToFit = scaleToFit;
        this.showNametag = showNametag;
    }

    @Override
    public WidgetState<KdlEntityWidget> createState() {
        return new State();
    }

    public static class State extends WidgetState<KdlEntityWidget> {

        private class_1297 entity;

        @Override
        public void init() {
            this.recreateEntity();
        }

        @Override
        public void didUpdateWidget(KdlEntityWidget oldWidget) {
            if (!this.widget().spec.equals(oldWidget.spec)) {
                this.recreateEntity();
            }
        }

        private void recreateEntity() {
            var level = AppState.of(this.context()).client().field_1687;

            var entity = this.widget().spec.type.method_5883(level, class_3730.field_52444);
            if (this.widget().spec.nbt != null) {
                entity.method_5651(class_11352.method_71417(new class_8942.class_11340(Owo.LOGGER), level.method_30349(), this.widget().spec.nbt));
            }

            this.setState(() -> {
                this.entity = entity;
            });
        }

        @Override
        public Widget build(BuildContext context) {
            return new EntityWidget(
                this.widget().scale,
                this.entity,
                widget -> widget
                    .displayMode(this.widget().mode)
                    .scaleToFit(this.widget().scaleToFit)
                    .showNametag(this.widget().showNametag)
            );
        }
    }

    public record EntitySpec(class_1299<?> type, @Nullable class_2487 nbt) {
        public static final Endec<EntitySpec> STRING_ENDEC = Endec.STRING.xmap(
            s -> {
                try {
                    class_2487 nbt = null;

                    int nbtIndex = s.indexOf('{');
                    if (nbtIndex != -1) {

                        nbt = class_2522.method_67310(new StringReader(s.substring(nbtIndex)));
                        s = s.substring(0, nbtIndex);
                    }

                    var entityType = class_7923.field_41177.method_17966(class_2960.method_60654(s)).orElseThrow();
                    return new EntitySpec(entityType, nbt);
                } catch (CommandSyntaxException | NoSuchElementException | class_151 e) {
                    throw new IllegalStateException("invalid entity: " + s, e);
                }
            },
            spec -> { throw new UnsupportedOperationException("cannot serialize an entity spec to a string"); }
        );
    }
}
