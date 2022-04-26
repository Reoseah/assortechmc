package spacefactory.features.machine.electric_furnace;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import spacefactory.features.machine.CraftingMachineScreen;

@Environment(EnvType.CLIENT)
public class ElectricFurnaceScreen extends CraftingMachineScreen<ElectricFurnaceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("spacefactory:textures/gui/electric_furnace.png");

    public ElectricFurnaceScreen(ElectricFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected Identifier getTextureId() {
        return TEXTURE;
    }
}