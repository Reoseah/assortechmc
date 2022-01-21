package assortech.screen;

import assortech.Assortech;
import assortech.block.entity.ElectricFurnaceBlockEntity;
import assortech.block.entity.CraftingMachineBlockEntity;
import assortech.screen.property.ReadProperty;
import assortech.screen.property.WriteProperty;
import assortech.screen.slot.GenericOutputSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;
import team.reborn.energy.api.EnergyStorageUtil;

public class ElectricFurnaceScreenHandler extends CraftingMachineScreenHandler {
    public ElectricFurnaceScreenHandler(int syncId, ElectricFurnaceBlockEntity be, PlayerEntity player) {
        super(Assortech.AtScreenHandlerTypes.ELECTRIC_FURNACE, syncId, be, player);
    }

    @Environment(EnvType.CLIENT)
    public ElectricFurnaceScreenHandler(int syncId, PlayerInventory user) {
        super(Assortech.AtScreenHandlerTypes.ELECTRIC_FURNACE, syncId, user);
    }
}
