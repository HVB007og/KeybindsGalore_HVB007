package io.wispforest.owo.braid.framework.proxy;

import java.time.Duration;
import net.minecraft.class_310;

public interface ProxyHost {

    class_310 client();

    void scheduleAnimationCallback(AnimationCallback callback);

    long scheduleDelayedCallback(Duration delay, Runnable callback);

    void cancelDelayedCallback(long id);

    void schedulePostLayoutCallback(Runnable callback);

    interface AnimationCallback {
        void run(Duration delta);
    }
}
