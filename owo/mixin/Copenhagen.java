package io.wispforest.owo.mixin;

import io.wispforest.owo.util.Maldenhagen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2826;
import net.minecraft.class_3122;
import net.minecraft.class_3124;
import net.minecraft.class_5281;
import net.minecraft.class_5819;
import net.minecraft.class_5867;

// welcome to maldenhagen, it moved
// it originally lived in things, but it was malding too hard there
// see Maldenhagen for how this is used
@Mixin(class_3122.class)
public class Copenhagen {

    // this map contains the seethe'd orr blocks. its quite important
    @Unique private final ThreadLocal<Map<class_2338, class_2680>> COPING = ThreadLocal.withInitial(HashMap::new);

    // this target method is just so damn complex that not even mixin can correctly guess the injector signature.
    // i just kinda gave up and deleted some of them until it worked. very epic
    //
    // oh also the method caches all the spots that gleaming ore was placed at, so we can later update them for it to glow.
    // of course that needs to be done later, because mojang decided it should. the actual reason is that ChunkSectionCache
    // locks its chunk sections.
    //
    // now you would think this throws an error when you then try to modify those sections. but no.
    // it just silently deadlocks the entire game
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "doPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void malding(class_5281 world, class_5819 random, class_3124 config, double startX, double endX, double startZ, double endZ,
                         double startY, double endY, int p_x, int p_y, int p_z, int p_horizontalSize, int p_verticalSize, CallbackInfoReturnable<Boolean> cir,
                         int i, BitSet bitSet, class_2338.class_2339 mutable, int j, double[] ds, class_5867 chunkSectionCache, int m, double d, double e,
                         double g, double h, int n, int o, int p, int q, int r, int s, int t, double u, int v, double w, int aa, double x, int ab, class_2826 chunkSection,
                         int ad, int ae, int af, class_2680 blockState, Iterator<class_3124.class_5876> var57, class_3124.class_5876 target) {

        if (!Maldenhagen.isOnCopium(target.field_29069.method_26204())) return;
        COPING.get().put(new class_2338(t, v, aa), target.field_29069);
    }

    // now in here we read all the gleaming ore spots from our cache and actually cause a block update so that the
    // lighting calculations happen. all of this just so that some dumb orr block can glow.
    @Inject(method = "doPlace", at = @At("TAIL"))
    private void coping(class_5281 world, net.minecraft.class_5819 random, class_3124 config, double startX, double endX,
                        double startZ, double endZ, double startY, double endY, int x, int y, int z, int horizontalSize,
                        int verticalSize, CallbackInfoReturnable<Boolean> cir) {

        COPING.get().forEach((blockPos, state) -> {
            world.method_8652(blockPos, state, class_2248.field_31036);
        });
        COPING.get().clear();
    }

}
