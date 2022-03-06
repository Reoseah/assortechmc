package spacefactory.screen;

import spacefactory.SpaceFactory;
import spacefactory.block.entity.MaceratorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class PulverizerScreenHandler extends CraftingMachineScreenHandler {
    public PulverizerScreenHandler(int syncId, MaceratorBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.SFScreenHandlerTypes.PULVERIZER, syncId, be, player);
    }

    public PulverizerScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.SFScreenHandlerTypes.PULVERIZER, syncId, user);
    }
}
