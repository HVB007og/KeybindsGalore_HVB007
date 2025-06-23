/*
 * This class is modified from the PSI mod created by Vazkii
 * Psi Source Code: https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 *
 * HVB007: IDK What Part This credit refers to, if you want to know contact https://github.com/CaelTheColher as he is the maker of this mod
 * I am just updating it to 1.20.x
 */
package me.av306.keybindsgaloreplus;

import com.mojang.blaze3d.systems.RenderSystem;

import static me.av306.keybindsgaloreplus.KeybindsGalorePlus.customDataManager;

import me.av306.keybindsgaloreplus.mixin.KeyBindingAccessor;
import me.av306.keybindsgaloreplus.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
//import net.minecraft.client.render.BufferRenderer;
//import net.minecraft.client.render.VertexFormat;
//import net.minecraft.client.render.GameRenderer;
//import net.minecraft.client.util.math.MatrixStack;
//import com.mojang.blaze3d.systems.RenderSystem;
//// Core rendering imports
//import net.minecraft.client.render.BufferBuilder;
//import net.minecraft.client.render.Tessellator;
//import net.minecraft.client.render.VertexFormats;
//import net.minecraft.client.render.GameRenderer;
//import net.minecraft.client.gui.DrawContext;
//import com.mojang.blaze3d.systems.RenderSystem;
// Alternative rendering approach (if BufferRenderer import fails)
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.joml.Matrix4f;


import java.util.ArrayList;
import java.util.Objects;
// Updated VertexFormat import - moved to blaze3d package
import com.mojang.blaze3d.vertex.VertexFormat;

public class KeybindSelectorScreen extends Screen
{
    // Instance variables
    private int ticksInScreen = 0;
    private int selectedSectorIndex = -1;
    private boolean mouseDown = false;

    private final InputUtil.Key conflictedKey;

    private final MinecraftClient mc;

    private int centreX = 0, centreY = 0;

    private float maxRadius = 0;
    private float maxExpandedRadius = 0;
    private float cancelZoneRadius = 0;

    private boolean isFirstFrame = true;

    /** This is probably not going to change while the screen is open, so maybe this optimisation helps? */
    private final ArrayList<KeyBinding> conflicts = new ArrayList<>();

    /*public KeybindSelectorScreen()
    {
        super( NarratorManager.EMPTY );
        this.mc = MinecraftClient.getInstance();

        // Debug -- print all fields
        for ( var f : this.getClass().getFields() )
        {
            try
            {
                KeybindsGalorePlus.LOGGER.info( "{}: {}", f.getName(), f.get( this ) );
            }
            catch ( IllegalAccessException e )
            {
                KeybindsGalorePlus.LOGGER.warn( e.getMessage() );
            }
        }
    }*/

    public KeybindSelectorScreen( InputUtil.Key key )
    {
        //this();
        super( NarratorManager.EMPTY );
        this.mc = MinecraftClient.getInstance();

        this.conflictedKey = key;

        this.conflicts.addAll( KeybindManager.getConflicts( key ) );
    }

    @Override
    public void render( DrawContext context, int mouseX, int mouseY, float delta )
    {
        // ===== Version dependent =====
        //super.render( context, mouseX, mouseY, delta );
        this.renderBackground( context, mouseX, mouseY, delta );
        //this.renderBackground( context );

        // Pixel coords of screen centre
        // Only set these on the first frame
        // Side effect: If window is resized when the screen is open, the menu won't update
        if ( this.isFirstFrame )
        {
            // Set centre of screen
            this.centreX = this.width / 2;
            this.centreY = this.height / 2;

            this.maxRadius = Math.min( (this.centreX * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN, (this.centreY * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN );
            this.maxExpandedRadius = this.maxRadius * Configurations.EXPANSION_FACTOR_WHEN_SELECTED;
            this.cancelZoneRadius = maxRadius * Configurations.CANCEL_ZONE_SCALE;

            this.isFirstFrame = false;
        }

        // Angle of mouse, in radians from +X-axis, centred on the origin
        double mouseAngle = mouseAngle( this.centreX, this.centreY, mouseX, mouseY );

        float mouseDistanceFromCentre = MathHelper.sqrt( (mouseX - this.centreX) * (mouseX - this.centreX) +
                        (mouseY - this.centreY) * (mouseY - this.centreY) );

        // Determines how many sectors to make for the pie menu
        int numberOfSectors = this.conflicts.size();

        // Calculate the angle occupied by each sector
        float sectorAngle = (MathHelper.TAU) / numberOfSectors;

        // Get the exact sector index that is selected
        this.selectedSectorIndex = (int) (mouseAngle / sectorAngle);

        // Deselect slot if mouse is within cancel zone
        if ( mouseDistanceFromCentre <= this.cancelZoneRadius )
            this.selectedSectorIndex = -1;
        
        this.renderPieMenu( context, delta, numberOfSectors, sectorAngle );
        this.renderLabelTexts( context, delta, numberOfSectors, sectorAngle );
    }


    // ==================== Rendering methods ====================


    private void renderPieMenu(DrawContext context, float delta, int numberOfSectors, float sectorAngle) {
        // 1. Initialize buffer for triangles
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(RenderLayer.getGui().getDrawMode(),
                VertexFormats.POSITION_COLOR);

        // 2. Obtain transformation matrix from DrawContext
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        // 3. Compute per-sector vertex count
        float startAngle = 0f;
        int vertices = Math.max(1, Configurations.CIRCLE_VERTICES / numberOfSectors);

        // 4. Build each sector
        for (int i = 0; i < numberOfSectors; i++) {
            float innerR = this.cancelZoneRadius;
            float outerR = calculateRadius(delta, numberOfSectors, i);
            int baseColor = Configurations.PIE_MENU_COLOR;
            int innerColor = baseColor, outerColor = baseColor;

            // 4.a Custom color override
            if (customDataManager.hasCustomData) {
                try {
                    outerColor = customDataManager.customData
                            .get(conflicts.get(i).getTranslationKey()).sectorColor;
                } catch (Exception ignored) {}
            }

            // 4.b Alternate shading & selection
            if (i % 2 == 0) innerColor = outerColor + Configurations.PIE_MENU_COLOR_LIGHTEN_FACTOR;
            if (selectedSectorIndex == i) {
                innerR *= Configurations.EXPANSION_FACTOR_WHEN_SELECTED;
                outerColor = mouseDown
                        ? Configurations.PIE_MENU_HIGHLIGHT_COLOR
                        : Configurations.PIE_MENU_SELECT_COLOR;
            }
            if (!Configurations.SECTOR_GRADATION) innerColor = outerColor;

            // 4.c Draw the sector
            drawSector(buf, startAngle, sectorAngle, vertices, innerR, outerR, innerColor, outerColor);
            startAngle += sectorAngle;
        }

        // 5. Finalize and render
        BuiltBuffer built = buf.end();
        RenderLayer.getGui().draw(built);
    }




    private void drawSector( BufferBuilder buf, float startAngle, float sectorAngle, int vertices, float innerRadius, float outerRadius,
                             int innerColor, int outerColor )
    {
        for ( var i = 0; i <= vertices; i++ )
        {
            float angle = startAngle + ((float) i / vertices) * sectorAngle;

            // ===== Version dependent =====
            // Inner vertex
            // FIXME: is the compiler smart enough to optimise the trigo?
            buf.vertex( this.centreX + MathHelper.cos( angle ) * innerRadius, this.centreY + MathHelper.sin( angle ) * innerRadius, 0 );
            buf.color( innerColor >> 16 & 0xFF, innerColor >> 8 & 0xFF, innerColor & 0xFF, Configurations.PIE_MENU_ALPHA );

            // Outer vertex
            buf.vertex( this.centreX + MathHelper.cos( angle ) * outerRadius, this.centreY + MathHelper.sin( angle ) * outerRadius, 0 );
            buf.color( outerColor >> 16 & 0xFF, outerColor >> 8 & 0xFF, outerColor & 0xFF, Configurations.PIE_MENU_ALPHA );
        }
    }

    private float calculateRadius( float delta, int numberOfSectors, int sectorIndex )
    {
        float radius = Configurations.ANIMATE_PIE_MENU ?
                Math.max( 0f, Math.min( (this.ticksInScreen + delta - sectorIndex * 6f / numberOfSectors) * 40f, this.maxRadius ) ) :
                this.maxRadius;

        // Expand the sector if selected
        if ( this.selectedSectorIndex == sectorIndex ) radius *= Configurations.EXPANSION_FACTOR_WHEN_SELECTED;

        return radius;
    }

    private void renderLabelTexts(
            DrawContext context,
            float delta,
            int numberOfSectors, float sectorAngle
    )
    {
        for ( var sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++ )
        {
            float radius = calculateRadius( delta, numberOfSectors, sectorIndex );
            
            float angle = (sectorIndex + 0.5f) * sectorAngle;

            // Position in the middle of the arc
            float xPos = this.centreX + MathHelper.cos( angle ) * radius;
            float yPos = this.centreY + MathHelper.sin( angle ) * radius;

            KeyBinding action = this.conflicts.get( sectorIndex );

            // The biggest nagging bug for me
            // Tells you which control category the action goes in
            // TODO: configurable

            String id = action.getTranslationKey();
            String actionName = Text.translatable( action.getCategory() ).getString() + ": " + Text.translatable( action.getTranslationKey() ).getString();

            // Read custom data for this keybind, only if present
            if ( customDataManager.hasCustomData )
            {
                try
                {
                    if ( customDataManager.customData.get( id ).hideCategory )
                        actionName = Text.translatable( action.getTranslationKey() ).getString();
                }
                catch ( NullPointerException npe )
                {
                    //KeybindsGalorePlus.debugLog( "No hideCategory setting for {}", id );
                }

                try
                {
                    // Assigning `null` doesn't throw an NPE, so we wrap with this to throw one
                    actionName = Objects.requireNonNull( customDataManager.customData.get( id ).displayName );
                }
                catch ( NullPointerException npe )
                {
                    //KeybindsGalorePlus.debugLog( "No custom name for {}", id );
                }
            }

            int textWidth = this.textRenderer.getWidth( actionName );

            // Which side of the screen are we on?
            if ( xPos > this.centreX ) // Right side
            {
                xPos -= Configurations.LABEL_TEXT_INSET;

                // Check text going off-screen
                if ( this.width - xPos < textWidth )
                    xPos -= textWidth - this.width + xPos;
            }
            else // Left side
            {

                xPos -= textWidth - Configurations.LABEL_TEXT_INSET;

                // Check text going off-screen
                if ( xPos < 0 ) xPos = Configurations.LABEL_TEXT_INSET;
            }

            // Move the text closer to the centre of the circle
            yPos -= Configurations.LABEL_TEXT_INSET;

            actionName = (this.selectedSectorIndex == sectorIndex ? Formatting.UNDERLINE : Formatting.RESET) + actionName;

            context.drawText( this.textRenderer, actionName, (int) xPos, (int) yPos, 0xFFFFFF, Configurations.LABEL_TEXT_SHADOW );
        }
    }


    // ==================== Others // ====================

    // Returns the angle of the line bounded by the given coordinates and the mouse position from the vertical axis
    // This is why we study trigo, guys
    private static double mouseAngle( int x, int y, int mx, int my )
    {
        return (MathHelper.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    private void closePieMenu()
    {
        this.mc.setScreen( null );

        // Activate the selected binding
        if ( this.selectedSectorIndex != -1 )
        {
            KeyBinding selectedKeyBinding = this.conflicts.get( this.selectedSectorIndex );

            KeybindsGalorePlus.debugLog( "Activated {} from pie menu", selectedKeyBinding.getTranslationKey() );

            ((KeyBindingAccessor) selectedKeyBinding).setPressed( true );
            ((KeyBindingAccessor) selectedKeyBinding).setTimesPressed( 1 );
            //((KeyBindingAccessor) bind).invokeSetPressed( true );

            // Attack workaround (very hacky)
            // Abusable??? (FIXME)
            if ( selectedKeyBinding.equals( this.mc.options.attackKey ) && Configurations.ENABLE_ATTACK_WORKAROUND )
            {
                KeybindsGalorePlus.debugLog( "\tAttack workaround enabled" );
                ((MinecraftClientAccessor) this.mc).setAttackCooldown( 0 );
            }
        }
        else
        {
            KeybindsGalorePlus.debugLog( "Pie menu closed with no selection" );
        }
    }


    // ==================== Overrides // ====================

    @Override
    public void tick()
    {
        // There's literally nothing there. Avoid the jump instructions.
        // super.tick();
        this.ticksInScreen++;
    }

    // These two callbacks work the same as handling it in tick(), plus we get differentiated mouse/keyboard handling
    // Previously, InputUtil.isKeyPressed would throw a GL error when called for a mouse code (0, 1, 2) and return a meaningless value

    @Override
    public boolean keyReleased( int keyCode, int scanCode, int modifiers )
    {
        if ( keyCode == this.conflictedKey.getCode() ) this.closePieMenu();

        return super.keyReleased( keyCode, scanCode, modifiers );
    }

    @Override
    public boolean mouseReleased( double mouseX, double mouseY, int button )
    {
        //this.mouseDown = false;

        if ( button == this.conflictedKey.getCode() )
        {
            // Close menu and activate selection normally – click-hold not applicable
            this.closePieMenu();
        }
        else
        {
            // Click-hold selected binding

            this.mc.setScreen( null );
            KeyBinding.unpressAll(); // This stops the other actions from triggering. Not sure why they do in the first place, though.

            if ( this.selectedSectorIndex != -1 )
            {
                KeyBinding binding = this.conflicts.get( this.selectedSectorIndex );

                // Clicked on a sector; add its binding to the click-hold map
                //KeybindsGalorePlus.debugLog( "Activated sector {} (key {}) (click-hold) via pie menu", this.selectedSectorIndex, this.conflictedKey.getCategory() );
                KeybindsGalorePlus.debugLog( "Pie menu closed with click-hold" );
                KeybindManager.clickHoldKeys.put(
                        this.conflictedKey.getCode(),
                        binding
                );

                // Key events are generated repeatedly for keyboard keys held down, but not for mouse buttons,
                // so we have to make one manually
                if ( this.conflictedKey.getCode() <= GLFW.GLFW_MOUSE_BUTTON_LAST )
                    binding.setPressed( true );
            }
            else
            {
                KeybindsGalorePlus.debugLog( "Pie menu closed via click-hold with no selection" );
                // No sector clicked; add null to the click-hold map to signal a cancel
                KeybindManager.clickHoldKeys.put( this.conflictedKey.getCode(), null );
            }
        }

        return super.mouseReleased( mouseX, mouseY, button );
    }

    @Override
    public boolean mouseClicked( double mouseX, double mouseY, int button )
    {
        this.mouseDown = true;

        return super.mouseClicked( mouseX, mouseY, button );
    }

    @Override
    // Don't pause the game when this screen is open
    // actually why not
    public boolean shouldPause() { return false; }


    //* >=1.20.2
    @Override
    public void renderBackground( DrawContext context, int mouseX, int mouseY, float delta )
    {
        // Remove the darkened background if needed
        // This can help performance, as with all post-processing
        if ( Configurations.DARKENED_BACKGROUND ) super.renderBackground( context, mouseX, mouseY, delta ); //* >=1.20.2
    }

    //* <1.20.2
    // Annoyingly, we can have the method in >1.20.2 but not the super call :(
    //@Override
    // public void renderBackground( DrawContext context ) //* <1.20.2
    // {
    //     //* // ===== Version dependent =====
    //     // Remove the darkened background if needed
    //     // This can help performance, as with all post-processing
    //     if ( Configurations.DARKENED_BACKGROUND ) super.renderBackground( context ); //* <1.20.2
    // }
}
