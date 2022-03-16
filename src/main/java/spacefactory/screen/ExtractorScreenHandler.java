package spacefactory.screen;

import spacefactory.SpaceFactory;
import spacefactory.block.entity.ExtractorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class ExtractorScreenHandler extends CraftingMachineScreenHandler {
    public ExtractorScreenHandler(int syncId, ExtractorBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.ScreenHandlerTypes.EXTRACTOR, syncId, be, player);
    }

    public ExtractorScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.EXTRACTOR, syncId, user);
    }
}
