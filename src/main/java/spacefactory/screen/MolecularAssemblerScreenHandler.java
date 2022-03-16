package spacefactory.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;
import spacefactory.SpaceFactory;
import spacefactory.block.entity.CraftingMachineBlockEntity;
import spacefactory.block.entity.MolecularAssemblerBlockEntity;
import spacefactory.screen.property.ReadProperty;
import spacefactory.screen.property.WriteProperty;
import spacefactory.screen.slot.GenericOutputSlot;
import team.reborn.energy.api.EnergyStorageUtil;

public class MolecularAssemblerScreenHandler extends AtScreenHandler {
    protected boolean active;
    protected int energy;
    protected int progress, recipeDuration;

    private MolecularAssemblerScreenHandler(int syncId, Inventory inventory, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.MOLECULAR_ASSEMBLER, syncId, inventory);

        this.addQuickTransferSlot(stack -> true, new Slot(inventory, MolecularAssemblerBlockEntity.SLOT_INPUT_1, 53, 24));
        this.addQuickTransferSlot(stack -> true, new Slot(inventory, MolecularAssemblerBlockEntity.SLOT_INPUT_2, 53, 46));
        this.addQuickTransferSlot(EnergyStorageUtil::isEnergyStorage, 0, new Slot(inventory, MolecularAssemblerBlockEntity.SLOT_BATTERY, 8, 53));
        this.addSlot(new GenericOutputSlot(inventory, MolecularAssemblerBlockEntity.SLOT_OUTPUT, 116, 35));
        this.addPlayerSlots(user);
    }

    public MolecularAssemblerScreenHandler(int syncId, MolecularAssemblerBlockEntity be, PlayerEntity player) {
        this(syncId, be, player.getInventory());

        this.addProperty(new ReadProperty(() -> be.isActive() ? 1 : 0));
        this.addProperty(new ReadProperty(be::getEnergy));
        this.addProperty(new ReadProperty(be::getProgress));
        this.addProperty(new ReadProperty(be::getRecipeDuration));
    }

    @Environment(EnvType.CLIENT)
    public MolecularAssemblerScreenHandler(int syncId, PlayerInventory user) {
        this(syncId, new SimpleInventory(4), user);

        this.addProperty(new WriteProperty(value -> this.active = value == 1));
        this.addProperty(new WriteProperty(value -> this.energy = value));
        this.addProperty(new WriteProperty(value -> this.progress = value));
        this.addProperty(new WriteProperty(value -> this.recipeDuration = value));
    }

    @Environment(EnvType.CLIENT)
    public boolean isActive() {
        return this.active;
    }

    @Environment(EnvType.CLIENT)
    public int getRecipeDisplay() {
        int duration = this.recipeDuration == 0 ? 1000 : this.recipeDuration;
        return this.progress * 24 / duration;
    }

    @Environment(EnvType.CLIENT)
    public int getEnergyDisplay() {
        return this.energy * 13 / CraftingMachineBlockEntity.CAPACITY;
    }

    @Environment(EnvType.CLIENT)
    public int getEnergy() {
        return this.energy;
    }
}
