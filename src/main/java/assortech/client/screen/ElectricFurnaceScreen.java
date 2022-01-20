package assortech.client.screen;

import assortech.block.entity.GeneratorBlockEntity;
import assortech.block.entity.ProcessingMachineBlockEntity;
import assortech.screen.ElectricFurnaceScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ElectricFurnaceScreen extends HandledScreen<ElectricFurnaceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("assortech:textures/gui/electric_furnace.png");

    public ElectricFurnaceScreen(ElectricFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
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
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int leftX = (this.width - this.backgroundWidth) / 2;
        int topY = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, leftX, topY, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.handler.isActive()) {
            this.drawTexture(matrices, leftX + 54, topY + 36, 176, 0, 14, 14);
        }

        int recipe = this.handler.getRecipeDisplay();
        this.drawTexture(matrices, leftX + 79, topY + 34, 176, 14, recipe + 1, 16);

        int energy = this.handler.getEnergyDisplay();
        this.drawTexture(matrices, leftX + 51, topY + 56, 176, 31, energy, 10);
        if (this.isPointWithinBounds(51, 56, 20, 10, mouseX, mouseY)) {
            this.renderTooltip(matrices, new TranslatableText("container.assortech.energy", this.handler.getEnergy(), ProcessingMachineBlockEntity.CAPACITY).formatted(Formatting.ITALIC, Formatting.GRAY), mouseX, mouseY);
        }
    }
}
