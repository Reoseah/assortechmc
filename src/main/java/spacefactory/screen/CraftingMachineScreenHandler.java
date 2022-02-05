package spacefactory.screen;

import spacefactory.block.entity.CraftingMachineBlockEntity;
import spacefactory.screen.property.ReadProperty;
import spacefactory.screen.property.WriteProperty;
import spacefactory.screen.slot.GenericOutputSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import team.reborn.energy.api.EnergyStorageUtil;

public abstract class CraftingMachineScreenHandler extends AtScreenHandler {
    protected boolean active;
    protected int energy;
    protected int progress, recipeDuration;

    private CraftingMachineScreenHandler(ScreenHandlerType<?> type, int syncId, Inventory inventory, PlayerInventory user) {
        super(type, syncId, inventory);

        this.addQuickTransferSlot(stack -> true, new Slot(inventory, 0, 53, 17));
        this.addQuickTransferSlot(EnergyStorageUtil::isEnergyStorage, 0, new Slot(inventory, 1, 53, 53));
        this.addSlot(new GenericOutputSlot(inventory, 2, 116, 35));
        this.addPlayerSlots(user);
    }

    protected CraftingMachineScreenHandler(ScreenHandlerType<?> type, int syncId, CraftingMachineBlockEntity<?> be, PlayerEntity player) {
        this(type, syncId, be, player.getInventory());

        this.addProperty(new ReadProperty(() -> be.isActive() ? 1 : 0));
        this.addProperty(new ReadProperty(be::getEnergy));
        this.addProperty(new ReadProperty(be::getProgress));
        this.addProperty(new ReadProperty(be::getRecipeDuration));
    }

    @Environment(EnvType.CLIENT)
    protected CraftingMachineScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory user) {
        this(type, syncId, new SimpleInventory(3), user);

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
        int duration = this.recipeDuration == 0 ? 400 : this.recipeDuration;
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
