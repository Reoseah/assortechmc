package assortech.screen;

import assortech.Assortech;
import assortech.block.entity.MaceratorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class MaceratorScreenHandler extends CraftingMachineScreenHandler {
    public MaceratorScreenHandler(int syncId, MaceratorBlockEntity be, PlayerEntity player) {
        super(Assortech.AtScreenHandlerTypes.MACERATOR, syncId, be, player);
    }

    @Environment(EnvType.CLIENT)
    public MaceratorScreenHandler(int syncId, PlayerInventory user) {
        super(Assortech.AtScreenHandlerTypes.MACERATOR, syncId, user);
    }
}
