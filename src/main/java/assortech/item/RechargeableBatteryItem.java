package assortech.item;

import net.minecraft.item.Item;

public class RechargeableBatteryItem extends EnergyStorageItem {
    public RechargeableBatteryItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public long getEnergyCapacity() {
        return 10000;
    }

    @Override
    public long getEnergyMaxInput() {
        return 32;
    }

    @Override
    public long getEnergyMaxOutput() {
        return 32;
    }
}
