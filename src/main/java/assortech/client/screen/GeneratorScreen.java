package assortech.client.screen;

import assortech.Assortech;
import assortech.block.entity.GeneratorBlockEntity;
import assortech.block.entity.ProcessingMachineBlockEntity;
import assortech.screen.GeneratorScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class GeneratorScreen extends HandledScreen<GeneratorScreenHandler> {
    private static final Identifier TEXTURE = Assortech.id("textures/gui/generator.png");

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

        int energy = this.handler.getEnergyDisplay();
        this.drawTexture(matrices, leftX + 78, topY + 21, 176, 31, energy, 10);

        if (this.isPointWithinBounds(78, 21, 20, 10, mouseX, mouseY)) {
            this.renderTooltip(matrices, new TranslatableText("container.assortech.energy", this.handler.getEnergy(), GeneratorBlockEntity.CAPACITY).formatted(Formatting.ITALIC, Formatting.GRAY), mouseX, mouseY);
        }
    }
}
