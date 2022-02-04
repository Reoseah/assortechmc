package assortech.screen;

import assortech.Assortech;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class CompressorScreenHandler extends CraftingMachineScreenHandler {
    public CompressorScreenHandler(int syncId, CompressorBlockEntity be, PlayerEntity player) {
        super(Assortech.AtScreenHandlerTypes.COMPRESSOR, syncId, be, player);
    }

    @Environment(EnvType.CLIENT)
    public CompressorScreenHandler(int syncId, PlayerInventory user) {
        super(Assortech.AtScreenHandlerTypes.COMPRESSOR, syncId, user);
    }
}
