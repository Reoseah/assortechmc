package assortech.screen;

import assortech.Assortech;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class ExtractorScreenHandler extends CraftingMachineScreenHandler {
    public ExtractorScreenHandler(int syncId, ExtractorBlockEntity be, PlayerEntity player) {
        super(Assortech.AtScreenHandlerTypes.EXTRACTOR, syncId, be, player);
    }

    @Environment(EnvType.CLIENT)
    public ExtractorScreenHandler(int syncId, PlayerInventory user) {
        super(Assortech.AtScreenHandlerTypes.EXTRACTOR, syncId, user);
    }
}
