package spacefactory.features.battery;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.core.screen.InventoryScreenHandler;
import spacefactory.core.screen.property.ReadProperty;
import spacefactory.core.screen.property.WriteProperty;

public class BatteryBoxScreenHandler extends InventoryScreenHandler {
    protected int energy;

    protected BatteryBoxScreenHandler(int syncId, Inventory inventory, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.BATTERY_BOX, syncId, inventory);

        this.addQuickTransferSlot(EU::isElectricItem, new Slot(inventory, 0, 71, 54));
        this.addQuickTransferSlot(EU::isElectricItem, new Slot(inventory, 1, 71, 18));

        this.addPlayerSlots(user);
    }

    public BatteryBoxScreenHandler(int syncId, BatteryBlockEntity be, PlayerEntity player) {
        this(syncId, be, player.getInventory());

        this.addProperty(new ReadProperty(be::getEnergy));
    }

    public BatteryBoxScreenHandler(int syncId, PlayerInventory user) {
        this(syncId, new SimpleInventory(2), user);

        this.addProperty(new WriteProperty(value -> this.energy = value));
    }

    @Environment(EnvType.CLIENT)
    public int getEnergyDisplay() {
        return this.energy * 20 / SpaceFactory.config.redstoneBatteryCapacity;
    }

    @Environment(EnvType.CLIENT)
    public int getEnergy() {
        return this.energy;
    }
}
