package net.hvb007.keybindsgalore.mixin;

import net.hvb007.keybindsgalore.KeybindManager;
import net.hvb007.keybindsgalore.configmanager.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsScreen.class)
public abstract class KeyBindsScreenMixin extends OptionsSubScreen {
    public KeyBindsScreenMixin(Screen parent, Options gameOptions, Component title) {
        super(parent, gameOptions, title);
    }

    @Inject(at = @At("TAIL"), method = "addContents")
    private void addConfigButton(CallbackInfo ci) {
        addRenderableWidget(Button.builder(
            Component.literal("KBG Config"),
            btn -> Minecraft.getInstance().setScreen(new ConfigScreen(this))
        ).bounds(5, 5, 80, 20).build());
    }

    @Override
    public void onClose() {
        super.onClose();
        KeybindManager.findAllConflicts();
    }
}
