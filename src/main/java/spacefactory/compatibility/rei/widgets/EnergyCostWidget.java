package spacefactory.compatibility.rei.widgets;

import spacefactory.compatibility.rei.SpaceFactoryPlugin;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.BurningFire;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.List;

public class EnergyCostWidget extends BurningFire {
    private Rectangle bounds;
    private double animationDuration = -1.0D;

    public EnergyCostWidget(Rectangle bounds) {
        this.bounds = new Rectangle(bounds);
    }

    public double getAnimationDuration() {
        return this.animationDuration;
    }

    public void setAnimationDuration(double animationDurationMS) {
        this.animationDuration = animationDurationMS;
        if (this.animationDuration <= 0.0D) {
            this.animationDuration = -1.0D;
        }
    }

    public Rectangle getBounds() {
        return this.bounds;
    }

    public List<? extends Element> children() {
        return Collections.emptyList();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1);
        RenderSystem.setShaderTexture(0, SpaceFactoryPlugin.WIDGETS);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.blendFunc(770, 771);
        if (this.getAnimationDuration() > 0.0D) {
            int height = 14 - MathHelper.ceil((double) System.currentTimeMillis() / (this.animationDuration / 14.0D) % 14.0D);
            this.drawTexture(matrices, this.getX(), this.getY(), 0, 0, 14, 14 - height);
            this.drawTexture(matrices, this.getX(), this.getY() + 14 - height, 16, 14 - height, 14, height);
        } else {
            this.drawTexture(matrices, this.getX(), this.getY(), 0, 0, 14, 14);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
