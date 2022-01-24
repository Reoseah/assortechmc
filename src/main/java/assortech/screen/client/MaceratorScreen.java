package assortech.screen.client;

import assortech.screen.MaceratorScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MaceratorScreen extends CraftingMachineScreen<MaceratorScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("assortech:textures/gui/macerator.png");

    public MaceratorScreen(MaceratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected Identifier getTextureId() {
        return TEXTURE;
    }
}
