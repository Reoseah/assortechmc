package spacefactory.features.generator;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import spacefactory.SpaceFactory;
import spacefactory.features.generator.GeneratorScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GeneratorScreen extends HandledScreen<GeneratorScreenHandler> {
    private static final Identifier TEXTURE = SpaceFactory.id("textures/gui/generator.png");

    public GeneratorScreen(GeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        int leftX = (this.width - this.backgroundWidth) / 2;
        int topY = (this.height - this.backgroundHeight) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.drawTexture(matrices, leftX, topY, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.handler.isBurning()) {
            int fuel = this.handler.getFuelDisplay();
            this.drawTexture(matrices, leftX + 81, topY + 49 - fuel, 176, 12 - fuel, 14, fuel + 1);
        }
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {
        super.drawMouseoverTooltip(matrices, x, y);
        if (this.isPointWithinBounds(81, 34, 14, 14, x, y)) {
            this.renderTooltip(matrices, new TranslatableText("container.spacefactory.fuel_ticks", this.handler.getFuelLeft()).formatted(Formatting.GRAY), x, y);
        }
    }
}
