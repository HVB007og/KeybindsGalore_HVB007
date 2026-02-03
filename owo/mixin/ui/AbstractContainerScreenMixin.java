package io.wispforest.owo.mixin.ui;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.opengl.GlStateManager;
import io.wispforest.owo.ui.base.BaseOwoContainerScreen;
import io.wispforest.owo.util.pond.OwoSlotExtension;
import net.minecraft.class_11908;
import net.minecraft.class_1735;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_465.class)
public abstract class AbstractContainerScreenMixin extends class_437 {

    @Unique
    private static boolean inOwoScreen = false;

    protected AbstractContainerScreenMixin(class_2561 title) {
        super(title);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "render", at = @At("HEAD"))
    private void captureOwoState(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        inOwoScreen = (Object) this instanceof BaseOwoContainerScreen<?, ?>;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void resetOwoState(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        inOwoScreen = false;
    }

    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void injectSlotScissors(class_332 context, class_1735 slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (!inOwoScreen) return;

        var scissorArea = ((OwoSlotExtension) slot).owo$getScissorArea();
        if (scissorArea == null) return;

        GlStateManager._enableScissorTest();
        GlStateManager._scissorBox(scissorArea.x(), scissorArea.y(), scissorArea.width(), scissorArea.height());
    }

    @Inject(method = "renderSlot", at = @At("RETURN"))
    private void clearSlotScissors(class_332 context, class_1735 slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (!inOwoScreen) return;

        var scissorArea = ((OwoSlotExtension) slot).owo$getScissorArea();
        if (scissorArea == null) return;

        GlStateManager._disableScissorTest();
    }

    @ModifyVariable(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 0), ordinal = 2)
    private int doNoThrow(int slotId, @Local() class_1735 slot) {
        return (((Object) this instanceof BaseOwoContainerScreen<?, ?>) && slot != null) ? slot.field_7874 : slotId;
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;checkHotbarKeyPressed(Lnet/minecraft/client/input/KeyEvent;)Z"), cancellable = true)
    private void closeIt(class_11908 input, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof BaseOwoContainerScreen<?, ?>)) return;

        if (input.method_74231() && this.method_25422()) {
            this.method_25419();
            cir.setReturnValue(true);
        }
    }
}
