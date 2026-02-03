package io.wispforest.owo.mixin.text.stapi;

import io.wispforest.owo.text.TextLanguage;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.language.SystemDelegatedLanguage;

@Pseudo
@Mixin(SystemDelegatedLanguage.class)
public abstract class SystemDelegatedLanguageFixin implements TextLanguage {
    @Final
    @Shadow private class_2477 vanilla;

    @Shadow
    protected abstract ServerLanguage getSystemLanguage();

    @Override
    public class_2561 getText(String key) {
        if (!(vanilla instanceof TextLanguage lang) || this.getSystemLanguage().serverTranslations().contains(key)) {
            return null;
        }

        return lang.getText(key);
    }
}
