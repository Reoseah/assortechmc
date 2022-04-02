package spacefactory.features.compressor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import spacefactory.core.screen.client.CraftingMachineScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CompressorScreen extends CraftingMachineScreen<CompressorScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("spacefactory:textures/gui/compressor.png");

    public CompressorScreen(CompressorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected Identifier getTextureId() {
        return TEXTURE;
    }
}
