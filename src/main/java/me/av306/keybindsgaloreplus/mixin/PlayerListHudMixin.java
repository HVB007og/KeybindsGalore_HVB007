package me.av306.keybindsgaloreplus.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( PlayerListHud.class )
public class PlayerListHudMixin
{
    /*@Inject( at = @At( "HEAD" ), method = "render", cancellable = true )
    private void onRender( DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, @Nullable ScoreboardObjective objective, CallbackInfo ci )
    {
        throw new RuntimeException();
    }*/

    // Turns out the player list ping thing is easy to do (text might overlap for high ping values though)
    /*@Redirect(
            method = "renderLatencyIcon",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
            )
    )
    private void drawGuiTexture( DrawContext context, Identifier texture, int x, int y, int width, int height, @Local( argsOnly = true ) PlayerListEntry entry )
    {
        context.drawText( MinecraftClient.getInstance().textRenderer, String.format( "%d ms", entry.getLatency() ), x, y, 0xFFFFFFFF, false );
    }*/
}
