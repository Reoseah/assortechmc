package spacefactory.features.machine.fabricator_ai;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import spacefactory.SpaceFactory;
import spacefactory.features.generator.fuel_generator.GeneratorScreenHandler;

@Environment(EnvType.CLIENT)
public class FabricatorAIScreen extends HandledScreen<FabricatorAIScreenHandler> {
    private static final Identifier TEXTURE = SpaceFactory.id("textures/gui/fabrication_ai.png");

    public FabricatorAIScreen(FabricatorAIScreenHandler handler, PlayerInventory inventory, Text title) {
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
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.drawTexture(matrices, leftX, topY, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
}
