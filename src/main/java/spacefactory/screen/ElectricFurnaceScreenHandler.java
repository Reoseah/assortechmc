package spacefactory.screen;

import spacefactory.SpaceFactory;
import spacefactory.block.entity.ElectricFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class ElectricFurnaceScreenHandler extends CraftingMachineScreenHandler {
    public ElectricFurnaceScreenHandler(int syncId, ElectricFurnaceBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.ScreenHandlerTypes.ELECTRIC_FURNACE, syncId, be, player);
    }

    public ElectricFurnaceScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.ELECTRIC_FURNACE, syncId, user);
    }
}
