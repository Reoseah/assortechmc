package spacefactory.screen.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import spacefactory.screen.CraftingMachineScreenHandler;

@Environment(EnvType.CLIENT)
public abstract class CraftingMachineScreen<T extends CraftingMachineScreenHandler> extends HandledScreen<T> {
    public CraftingMachineScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    protected abstract Identifier getTextureId();

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
        RenderSystem.setShaderTexture(0, this.getTextureId());

        int leftX = (this.width - this.backgroundWidth) / 2;
        int topY = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, leftX, topY, 0, 0, this.backgroundWidth, this.backgroundHeight);

        int recipe = this.handler.getRecipeDisplay();
        this.drawTexture(matrices, leftX + 79, topY + 34, 176, 14, recipe + 1, 16);

        int energy = this.handler.getEnergyDisplay();
        if (this.handler.getEnergy() > 0) {
            this.drawTexture(matrices, leftX + 54, topY + 48 - energy, 176, 12 - energy, 14, energy + 1);
        }
    }

    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {
        if (this.isPointWithinBounds(54, 36, 14, 14, x, y)) {
            this.renderTooltip(matrices, new TranslatableText("tooltip.spacefactory.energy_per_tick_and_limit", this.handler.getEnergy(), this.handler.getCapacity()).formatted(Formatting.GRAY), x, y);
        }
        super.drawMouseoverTooltip(matrices, x, y);
    }
}
