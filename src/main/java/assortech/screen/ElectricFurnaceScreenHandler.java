package assortech.screen;

import assortech.Assortech;
import assortech.block.entity.ElectricFurnaceBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class ElectricFurnaceScreenHandler extends CraftingMachineScreenHandler {
    public ElectricFurnaceScreenHandler(int syncId, ElectricFurnaceBlockEntity be, PlayerEntity player) {
        super(Assortech.AtScreenHandlerTypes.ELECTRIC_FURNACE, syncId, be, player);
    }

    @Environment(EnvType.CLIENT)
    public ElectricFurnaceScreenHandler(int syncId, PlayerInventory user) {
        super(Assortech.AtScreenHandlerTypes.ELECTRIC_FURNACE, syncId, user);
    }
}
