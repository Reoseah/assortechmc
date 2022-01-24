package assortech.screen.client;

import assortech.screen.ExtractorScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ExtractorScreen extends CraftingMachineScreen<ExtractorScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("assortech:textures/gui/extractor.png");

    public ExtractorScreen(ExtractorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected Identifier getTextureId() {
        return TEXTURE;
    }
}
