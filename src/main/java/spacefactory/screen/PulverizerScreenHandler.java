package spacefactory.screen;

import spacefactory.SpaceFactory;
import spacefactory.block.entity.PulverizerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class PulverizerScreenHandler extends CraftingMachineScreenHandler {
    public PulverizerScreenHandler(int syncId, PulverizerBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.ScreenHandlerTypes.PULVERIZER, syncId, be, player);
    }

    public PulverizerScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.PULVERIZER, syncId, user);
    }
}
