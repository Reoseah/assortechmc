package assortech.screen.client;

import assortech.screen.ElectricFurnaceScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ElectricFurnaceScreen extends CraftingMachineScreen<ElectricFurnaceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("assortech:textures/gui/electric_furnace.png");

    public ElectricFurnaceScreen(ElectricFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected Identifier getTextureId() {
        return TEXTURE;
    }
}
