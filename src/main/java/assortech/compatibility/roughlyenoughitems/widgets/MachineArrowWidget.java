package assortech.compatibility.roughlyenoughitems.widgets;

import assortech.compatibility.roughlyenoughitems.AssortechREI;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Arrow;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.List;

public class MachineArrowWidget extends Arrow {
    private final Rectangle bounds;
    private final MachineArrowWidget.Type type;
    private double animationDuration = -1.0D;

    public MachineArrowWidget(Rectangle bounds, Type type) {
        this.bounds = new Rectangle(bounds);
        this.type = type;
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
        RenderSystem.setShaderTexture(0, AssortechREI.WIDGETS);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.blendFunc(770, 771);
        if (this.getAnimationDuration() > 0.0D) {
            int width = MathHelper.ceil((double) System.currentTimeMillis() / (this.animationDuration / 24.0D) % 24.0D);
            this.drawTexture(matrices, this.getX() + width, this.getY(), width, this.type.uOffest, 24 - width, 17);
            this.drawTexture(matrices, this.getX(), this.getY(), 24, this.type.uOffest, width, 17);
        } else {
            this.drawTexture(matrices, this.getX(), this.getY(), 0, this.type.uOffest, 24, 17);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public enum Type {
        DEFAULT(16),
        MACERATING(16 + 17),
        COMPRESSING(16 + 17 * 2),
        EXTRACTING(16 + 17 * 3),
        MOLECULAR_ASSEMBLY(16 + 17 * 4);

        public final int uOffest;

        Type(int uOffest) {
            this.uOffest = uOffest;
        }
    }
}
