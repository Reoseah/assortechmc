package spacefactory.screen;

import spacefactory.SpaceFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import spacefactory.block.entity.CompressorBlockEntity;

public class CompressorScreenHandler extends CraftingMachineScreenHandler {
    public CompressorScreenHandler(int syncId, CompressorBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.ScreenHandlerTypes.COMPRESSOR, syncId, be, player);
    }

    public CompressorScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.COMPRESSOR, syncId, user);
    }
}
