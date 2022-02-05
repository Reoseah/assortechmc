package spacefactory.item;

import spacefactory.api.EnergyTier;

public class EnergyCrystalItem extends EnergyStorageItem {
    public static final int CAPACITY = 100000;

    public EnergyCrystalItem(Settings settings) {
        super(settings);
    }

    @Override
    public long getEnergyCapacity() {
        return CAPACITY;
    }

    @Override
    public EnergyTier getEnergyTier() {
        return EnergyTier.MEDIUM;
    }
}
