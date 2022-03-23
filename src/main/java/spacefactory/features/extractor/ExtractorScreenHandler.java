package spacefactory.features.extractor;

import spacefactory.SpaceFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import spacefactory.core.screen.CraftingMachineScreenHandler;

public class ExtractorScreenHandler extends CraftingMachineScreenHandler {
    public ExtractorScreenHandler(int syncId, ExtractorBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.ScreenHandlerTypes.EXTRACTOR, syncId, be, player);
    }

    public ExtractorScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.EXTRACTOR, syncId, user);
    }
}
