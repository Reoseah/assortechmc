package spacefactory.features.pulverizer;

import spacefactory.SpaceFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import spacefactory.core.screen.CraftingMachineScreenHandler;

public class PulverizerScreenHandler extends CraftingMachineScreenHandler {
    public PulverizerScreenHandler(int syncId, PulverizerBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.ScreenHandlerTypes.PULVERIZER, syncId, be, player);
    }

    public PulverizerScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.PULVERIZER, syncId, user);
    }
}
