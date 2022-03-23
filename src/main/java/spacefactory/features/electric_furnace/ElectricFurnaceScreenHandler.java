package spacefactory.features.electric_furnace;

import spacefactory.SpaceFactory;
import spacefactory.features.electric_furnace.ElectricFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import spacefactory.core.screen.CraftingMachineScreenHandler;

public class ElectricFurnaceScreenHandler extends CraftingMachineScreenHandler {
    public ElectricFurnaceScreenHandler(int syncId, ElectricFurnaceBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.ScreenHandlerTypes.ELECTRIC_FURNACE, syncId, be, player);
    }

    public ElectricFurnaceScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.ELECTRIC_FURNACE, syncId, user);
    }
}
