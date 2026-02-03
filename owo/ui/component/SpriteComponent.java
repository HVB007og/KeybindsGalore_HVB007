package io.wispforest.owo.ui.component;

import io.wispforest.owo.ui.base.BaseUIComponent;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.OwoUIPipelines;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.SpriteUtilInvoker;
import org.w3c.dom.Element;

import java.util.Map;
import net.minecraft.class_1058;
import net.minecraft.class_10799;
import net.minecraft.class_4730;

public class SpriteComponent extends BaseUIComponent {

    protected final class_1058 sprite;
    protected boolean blend = false;

    protected SpriteComponent(class_1058 sprite) {
        this.sprite = sprite;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return this.sprite.method_45851().method_45807();
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return this.sprite.method_45851().method_45815();
    }

    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        SpriteUtilInvoker.markSpriteActive(this.sprite);
        graphics.method_52709(this.blend ? class_10799.field_56883 : OwoUIPipelines.GUI_TEXTURED_NO_BLEND, this.sprite, this.x, this.y, this.width, this.height);
    }

    public SpriteComponent blend(boolean blend) {
        this.blend = blend;
        return this;
    }

    public boolean blend() {
        return this.blend;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "blend", UIParsing::parseBool, this::blend);
    }

    public static SpriteComponent parse(Element element) {
        UIParsing.expectAttributes(element, "atlas", "sprite");

        var atlas = UIParsing.parseIdentifier(element.getAttributeNode("atlas"));
        var sprite = UIParsing.parseIdentifier(element.getAttributeNode("sprite"));

        return UIComponents.sprite(new class_4730(atlas, sprite));
    }
}
