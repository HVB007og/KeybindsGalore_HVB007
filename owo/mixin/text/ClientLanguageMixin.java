package io.wispforest.owo.mixin.text;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.wispforest.owo.text.LanguageAccess;
import io.wispforest.owo.text.TextLanguage;
import io.wispforest.owo.util.KawaiiUtil;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.class_1078;
import net.minecraft.class_2561;
import net.minecraft.class_3300;

@Debug(export = true)
@Mixin(class_1078.class)
public class ClientLanguageMixin implements TextLanguage {

    @Mutable
    @Shadow
    @Final
    private Map<String, String> storage;

    private final Map<String, class_2561> owo$textMap = new HashMap<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void kawaii(Map<String, String> translations, boolean rightToLeft, CallbackInfo ci) {
        if (!Objects.equals(System.getProperty("owo.uwu"), "yes please")) return;

        var builder = ImmutableMap.<String, String>builder();
        translations.forEach((s, s2) -> builder.put(s, KawaiiUtil.uwuify(s2)));
        this.storage = builder.build();
    }

    @WrapMethod(method = "loadFrom")
    private static class_1078 setupAndSetText(class_3300 resourceManager, List<String> list, boolean bl, Operation<class_1078> original) {
        var buildingMap = new HashMap<String, class_2561>();
        LanguageAccess.textConsumer.set(buildingMap::put);
        var lang = original.call(resourceManager, list, bl);
        LanguageAccess.textConsumer.remove();
        var map = ((ClientLanguageMixin) (Object) lang).owo$textMap;
        map.clear();
        map.putAll(buildingMap);
        return lang;
    }

    @Inject(method = "has", at = @At("HEAD"), cancellable = true)
    private void hasTranslation(String key, CallbackInfoReturnable<Boolean> cir) {
        if (this.owo$textMap.containsKey(key)) cir.setReturnValue(true);
    }

    @Inject(method = "getOrDefault", at = @At("HEAD"), cancellable = true)
    private void get(String key, String fallback, CallbackInfoReturnable<String> cir) {
        if (this.owo$textMap.containsKey(key)) cir.setReturnValue(this.owo$textMap.get(key).getString());
    }

    @Override
    public class_2561 getText(String key) {
        return this.owo$textMap.get(key);
    }
}
