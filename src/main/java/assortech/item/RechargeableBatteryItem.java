package assortech.item;

import assortech.api.EnergyTier;
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
    public EnergyTier getEnergyTier() {
        return EnergyTier.LOW;
    }
}
