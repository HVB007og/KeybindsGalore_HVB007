package io.wispforest.owo.ui.component;

import io.wispforest.owo.mixin.ui.access.EditBoxAccessor;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import io.wispforest.owo.util.Observable;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_342;

public class TextBoxComponent extends class_342 {

    protected final Observable<Boolean> showsBackground = Observable.of(((EditBoxAccessor) this).owo$bordered());

    protected final Observable<String> textValue = Observable.of("");
    protected final EventStream<OnChanged> changedEvents = OnChanged.newStream();

    protected TextBoxComponent(Sizing horizontalSizing) {
        super(class_310.method_1551().field_1772, 0, 0, 0, 0, class_2561.method_43473());

        this.textValue.observe(this.changedEvents.sink()::onChanged);
        this.sizing(horizontalSizing, Sizing.content());

        this.showsBackground.observe(a -> this.widgetWrapper().notifyParentIfMounted());
    }

    /**
     * @deprecated Subscribe to {@link #onChanged()} instead
     */
    @Override
    @Deprecated(forRemoval = true)
    public void method_1863(Consumer<String> changedListener) {
        super.method_1863(changedListener);
    }

    @Override
    public void drawFocusHighlight(OwoUIGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {
        // noop, since TextFieldWidget already does this
    }

    @Override
    public boolean method_25404(class_11908 input) {
        boolean result = super.method_25404(input);

        if (input.method_74236()) {
            this.method_1867("    ");
            return true;
        } else {
            return result;
        }
    }

    @Override
    public void updateX(int x) {
        super.updateX(x);
        ((EditBoxAccessor) this).owo$updateTextPosition();
    }

    @Override
    public void updateY(int y) {
        super.updateY(y);
        ((EditBoxAccessor) this).owo$updateTextPosition();
    }

    @Override
    public void method_1858(boolean drawsBackground) {
        super.method_1858(drawsBackground);
        this.showsBackground.set(drawsBackground);
    }

    public EventSource<OnChanged> onChanged() {
        return changedEvents.source();
    }

    public TextBoxComponent text(String text) {
        this.method_1852(text);
        this.method_1870(false);
        return this;
    }

    @Override
    public void parseProperties(UIModel spec, Element element, Map<String, Element> children) {
        super.parseProperties(spec, element, children);
        UIParsing.apply(children, "show-background", UIParsing::parseBool, this::method_1858);
        UIParsing.apply(children, "max-length", UIParsing::parseUnsignedInt, this::method_1880);
        UIParsing.apply(children, "text", e -> e.getTextContent().strip(), this::text);
    }

    protected CursorStyle owo$preferredCursorStyle() {
        return CursorStyle.TEXT;
    }

    public interface OnChanged {
        void onChanged(String value);

        static EventStream<OnChanged> newStream() {
            return new EventStream<>(subscribers -> value -> {
                for (var subscriber : subscribers) {
                    subscriber.onChanged(value);
                }
            });
        }
    }
}
