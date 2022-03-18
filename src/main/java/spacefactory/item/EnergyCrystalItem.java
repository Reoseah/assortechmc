package spacefactory.item;

import net.minecraft.item.Item;
import spacefactory.api.EnergyTier;

public class EnergyCrystalItem extends EnergyStorageItem {
    public static final int CAPACITY = 1_000_000;

    public EnergyCrystalItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public EnergyTier getEnergyTier() {
        return EnergyTier.HIGH;
    }

//    @Override
//    public long getEnergyCapacity() {
//        return CAPACITY;
//    }
}
