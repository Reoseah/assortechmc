package spacefactory.features.machine.electric_furnace;

import spacefactory.SpaceFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import spacefactory.features.machine.CraftingMachineScreenHandler;
import spacefactory.features.machine.electric_furnace.ElectricFurnaceBlockEntity;

public class ElectricFurnaceScreenHandler extends CraftingMachineScreenHandler {
    public ElectricFurnaceScreenHandler(int syncId, ElectricFurnaceBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.ScreenHandlerTypes.ELECTRIC_FURNACE, syncId, be, player);
    }

    public ElectricFurnaceScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.ELECTRIC_FURNACE, syncId, user);
    }
}
