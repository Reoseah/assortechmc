package spacefactory.screen.client;

import spacefactory.SpaceFactory;
import spacefactory.block.entity.BatteryBlockEntity;
import spacefactory.screen.BatteryBoxScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class BatteryBoxScreen extends HandledScreen<BatteryBoxScreenHandler> {
    private static final Identifier TEXTURE = SpaceFactory.id("textures/gui/energy_storage.png");

    public BatteryBoxScreen(BatteryBoxScreenHandler handler, PlayerInventory inventory, Text title) {
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

        int energy = this.handler.getEnergyDisplay();
        this.drawTexture(matrices, leftX + 105, topY + 39, 176, 31, energy, 10);

        if (this.isPointWithinBounds(105, 39, 20, 10, mouseX, mouseY)) {
            this.renderTooltip(matrices, new TranslatableText("container.spacefactory.energy_storage", this.handler.getEnergy(), BatteryBlockEntity.CAPACITY).formatted(Formatting.GRAY), mouseX, mouseY);
        }
    }
}
