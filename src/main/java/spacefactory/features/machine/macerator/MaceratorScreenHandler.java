package spacefactory.features.machine.macerator;

import spacefactory.SpaceFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import spacefactory.features.machine.CraftingMachineScreenHandler;

public class MaceratorScreenHandler extends CraftingMachineScreenHandler {
    public MaceratorScreenHandler(int syncId, MaceratorBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.ScreenHandlerTypes.MACERATOR, syncId, be, player);
    }

    public MaceratorScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.MACERATOR, syncId, user);
    }
}
