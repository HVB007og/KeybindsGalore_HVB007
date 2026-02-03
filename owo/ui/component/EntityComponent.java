package io.wispforest.owo.ui.component;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.Owo;
import io.wispforest.owo.mixin.ui.access.EntityRendererAccessor;
import io.wispforest.owo.ui.base.BaseUIComponent;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.renderstate.EntityElementRenderState;
import net.minecraft.class_10185;
import net.minecraft.class_1068;
import net.minecraft.class_11352;
import net.minecraft.class_11653;
import net.minecraft.class_11909;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_1664;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_2598;
import net.minecraft.class_310;
import net.minecraft.class_3730;
import net.minecraft.class_4597;
import net.minecraft.class_634;
import net.minecraft.class_640;
import net.minecraft.class_746;
import net.minecraft.class_7833;
import net.minecraft.class_7923;
import net.minecraft.class_7965;
import net.minecraft.class_7975;
import net.minecraft.class_8030;
import net.minecraft.class_8675;
import net.minecraft.class_8685;
import net.minecraft.class_8942;
import net.minecraft.class_898;
import net.minecraft.class_9064;
import net.minecraft.class_9782;
import net.minecraft.world.entity.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

public class EntityComponent<E extends class_1297> extends BaseUIComponent {

    protected final class_898 manager;
    protected final class_4597.class_4598 entityBuffers;
    protected final E entity;

    protected float mouseRotation = 0;
    protected float scale = 1;
    protected boolean lookAtCursor = false;
    protected boolean allowMouseRotation = false;
    protected boolean scaleToFit = false;
    protected boolean showNametag = false;
    protected Consumer<Matrix4f> transform = matrixStack -> {};

    protected EntityComponent(Sizing sizing, E entity) {
        final var client = class_310.method_1551();
        this.manager = client.method_1561();
        this.entityBuffers = client.method_22940().method_23000();

        this.entity = entity;

        this.sizing(sizing);
    }

    @SuppressWarnings("DataFlowIssue")
    protected EntityComponent(Sizing sizing, class_1299<E> type, @Nullable class_2487 nbt) {
        final var client = class_310.method_1551();
        this.manager = client.method_1561();
        this.entityBuffers = client.method_22940().method_23000();

        this.entity = type.method_5883(client.field_1687, class_3730.field_16466);
        if (nbt != null) entity.method_5651(class_11352.method_71417(new class_8942.class_11340(Owo.LOGGER), client.field_1687.method_30349(), nbt));
        entity.method_30634(client.field_1724.method_23317(), client.field_1724.method_23318(), client.field_1724.method_23321());

        this.sizing(sizing);
    }

    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        var matrix = new Matrix4f();
        matrix.scale(75 * this.scale * this.width / 64f, -75 * this.scale * this.height / 64f, -75 * this.scale);

        matrix.translate(0, entity.method_17682() / 2f, 0);

        this.transform.accept(matrix);

        if (this.lookAtCursor) {
            float xRotation = (float) Math.toDegrees(Math.atan((mouseY - this.y - this.height / 2f) / 40f));
            float yRotation = (float) Math.toDegrees(Math.atan((mouseX - this.x - this.width / 2f) / 40f));

            if (this.entity instanceof class_1309 living) {
                living.field_6259 = -yRotation;
            }

            this.entity.field_5982 = -yRotation;
            this.entity.field_6004 = xRotation * .65f;

            // We make sure the xRotation never becomes 0, as the lighting otherwise becomes very unhappy
            if (xRotation == 0) xRotation = .1f;
            matrix.rotate(class_7833.field_40714.rotationDegrees(xRotation * .15f));
            matrix.rotate(class_7833.field_40716.rotationDegrees(yRotation * .15f));
        } else {
            matrix.rotate(class_7833.field_40714.rotationDegrees(35));
            matrix.rotate(class_7833.field_40716.rotationDegrees(-45 + this.mouseRotation));
        }

        var entityState = this.manager.method_72977(this.entity, partialTicks);
        var renderer = this.manager.method_3953(this.entity);

        if (showNametag) {
            entityState.field_53337 = ((EntityRendererAccessor) renderer).owo$getNameTag(entity);
            entityState.field_53338 = entity.method_56072().method_55675(class_9064.field_47745, 0, entity.method_61415(partialTicks));
        } else {
            entityState.field_53337 = null;
            entityState.field_53338 = null;
        }

        graphics.field_59826.method_70922(new EntityElementRenderState(
            entityState,
            matrix,
            new class_8030(this.x, this.y, this.width, this.height),
            graphics.field_44659.method_70863()
        ));
    }

    @Override
    public boolean onMouseDrag(class_11909 click, double deltaX, double deltaY) {
        if (this.allowMouseRotation && click.method_74245() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.mouseRotation += deltaX;

            super.onMouseDrag(click, deltaX, deltaY);
            return true;
        } else {
            return super.onMouseDrag(click, deltaX, deltaY);
        }
    }

    public E entity() {
        return this.entity;
    }

    public EntityComponent<E> allowMouseRotation(boolean allowMouseRotation) {
        this.allowMouseRotation = allowMouseRotation;
        return this;
    }

    public boolean allowMouseRotation() {
        return this.allowMouseRotation;
    }

    public EntityComponent<E> lookAtCursor(boolean lookAtCursor) {
        this.lookAtCursor = lookAtCursor;
        return this;
    }

    public boolean lookAtCursor() {
        return this.lookAtCursor;
    }

    public EntityComponent<E> scale(float scale) {
        this.scale = scale;
        return this;
    }

    public float scale() {
        return this.scale;
    }

    public EntityComponent<E> scaleToFit(boolean scaleToFit) {
        this.scaleToFit = scaleToFit;

        if (scaleToFit) {
            float xScale = .5f / entity.method_17681();
            float yScale = .5f / entity.method_17682();

            this.scale(Math.min(xScale, yScale));
        }

        return this;
    }

    public boolean scaleToFit() {
        return this.scaleToFit;
    }

    public EntityComponent<E> transform(Consumer<Matrix4f> transform) {
        this.transform = transform;
        return this;
    }

    public Consumer<Matrix4f> transform() {
        return transform;
    }

    public EntityComponent<E> showNametag(boolean showNametag) {
        this.showNametag = showNametag;
        return this;
    }

    public boolean showNametag() {
        return showNametag;
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.MOUSE_CLICK;
    }

    public static RenderablePlayerEntity createRenderablePlayer(GameProfile profile) {
        return new RenderablePlayerEntity(profile);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "scale", UIParsing::parseFloat, this::scale);
        UIParsing.apply(children, "look-at-cursor", UIParsing::parseBool, this::lookAtCursor);
        UIParsing.apply(children, "mouse-rotation", UIParsing::parseBool, this::allowMouseRotation);
        UIParsing.apply(children, "scale-to-fit", UIParsing::parseBool, this::scaleToFit);
    }

    public static EntityComponent<?> parse(Element element) {
        UIParsing.expectAttributes(element, "type");
        var entityId = UIParsing.parseIdentifier(element.getAttributeNode("type"));
        var entityType = class_7923.field_41177.method_17966(entityId).orElseThrow(() -> new UIModelParsingException("Unknown entity type " + entityId));

        class_2487 nbt = null;
        if (element.hasAttribute("nbt")) {
            try {
                nbt = class_2522.method_67315(element.getAttribute("nbt"));
            } catch (CommandSyntaxException cse) {
                throw new UIModelParsingException("Invalid NBT compound", cse);
            }
        }

        return new EntityComponent<>(Sizing.content(), entityType, nbt);
    }

    public static class RenderablePlayerEntity extends class_746 {

        protected class_8685 skinTextures;

        protected RenderablePlayerEntity(GameProfile profile) {
            super(class_310.method_1551(),
                class_310.method_1551().field_1687,
                new class_634(class_310.method_1551(),
                    new net.minecraft.class_2535(class_2598.field_11942),
                    new class_8675(
                        new class_11653(0),
                        profile, new class_7975(class_7965.field_41434, false, Duration.ZERO, ""),
                        class_310.method_1551().field_1687.method_30349().method_40316(),
                        class_310.method_1551().field_1687.method_45162(),
                        "Wisp Forest Enterprises", null, null, Map.of(), null, Map.of(), class_9782.field_51977, Map.of(),
                        true
                    )),
                null, null, class_10185.field_54098, false
            );

            this.skinTextures = class_1068.method_52854(profile);
            class_156.method_18349().execute(() -> {
                var completeProfile = class_310.method_1551().method_73361().comp_4624().method_73290(profile.id()).orElse(profile);

                this.skinTextures = class_1068.method_52854(completeProfile);
                this.field_3937.method_1582().method_52863(completeProfile).thenAccept(textures -> {
                    textures.ifPresent($ -> this.skinTextures = $);
                });
            });
        }

        @Override
        public class_8685 method_52814() {
            return this.skinTextures;
        }

        @Override
        public boolean method_74091(class_1664 part) {
            return true;
        }

        @Nullable
        @Override
        protected class_640 method_3123() {
            return null;
        }
    }
}
