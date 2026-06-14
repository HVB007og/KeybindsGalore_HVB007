package net.hvb007.keybindsgalore.configmanager;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.minecraft.client.gui.screens.Screen;

public class ConfigScreenFactory implements IConfigScreenFactory {
    @Override
    public Screen createScreen(ModContainer modContainer, Screen parent) {
        return ConfigScreen.create(parent);
    }
}
