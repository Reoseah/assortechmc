package assortech.screen;

import assortech.Assortech;
import assortech.block.entity.BatteryBoxBlockEntity;
import assortech.screen.property.ReadProperty;
import assortech.screen.property.WriteProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;
import team.reborn.energy.api.EnergyStorageUtil;

public class BatteryBoxScreenHandler extends AtScreenHandler {
    protected int energy;

    protected BatteryBoxScreenHandler(int syncId, Inventory inventory, PlayerInventory user) {
        super(Assortech.AtScreenHandlerTypes.BATTERY_BOX, syncId, inventory);

        this.addQuickTransferSlot(EnergyStorageUtil::isEnergyStorage, new Slot(inventory, 0, 71, 54));
        this.addQuickTransferSlot(EnergyStorageUtil::isEnergyStorage, new Slot(inventory, 1, 71, 18));

        this.addPlayerSlots(user);
    }

    public BatteryBoxScreenHandler(int syncId, BatteryBoxBlockEntity be, PlayerEntity player) {
        this(syncId, be, player.getInventory());

        this.addProperty(new ReadProperty(be::getEnergy));
    }

    @Environment(EnvType.CLIENT)
    public BatteryBoxScreenHandler(int syncId, PlayerInventory user) {
        this(syncId, new SimpleInventory(2), user);

        this.addProperty(new WriteProperty(value -> this.energy = value));
    }

    @Environment(EnvType.CLIENT)
    public int getEnergyDisplay() {
        return this.energy * 20 / BatteryBoxBlockEntity.CAPACITY;
    }

    @Environment(EnvType.CLIENT)
    public int getEnergy() {
        return this.energy;
    }
}
