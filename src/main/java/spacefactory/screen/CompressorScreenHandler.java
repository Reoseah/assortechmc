package spacefactory.screen;

import spacefactory.SpaceFactory;
import spacefactory.block.entity.CompressorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class CompressorScreenHandler extends CraftingMachineScreenHandler {
    public CompressorScreenHandler(int syncId, CompressorBlockEntity be, PlayerEntity player) {
        super(SpaceFactory.SFScreenHandlerTypes.COMPRESSOR, syncId, be, player);
    }

    public CompressorScreenHandler(int syncId, PlayerInventory user) {
        super(SpaceFactory.SFScreenHandlerTypes.COMPRESSOR, syncId, user);
    }
}
