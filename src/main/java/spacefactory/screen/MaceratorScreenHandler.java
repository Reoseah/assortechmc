package spacefactory.screen;

import spacefactory.SpaceFactory;
import spacefactory.block.entity.MaceratorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class MaceratorScreenHandler extends CraftingMachineScreenHandler {
    public MaceratorScreenHandler(int syncId, MaceratorBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.SFScreenHandlerTypes.MACERATOR, syncId, be, player);
    }

    public MaceratorScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.SFScreenHandlerTypes.MACERATOR, syncId, user);
    }
}
