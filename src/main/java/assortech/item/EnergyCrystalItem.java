package assortech.item;

public class EnergyCrystalItem extends EnergyStorageItem {
    public EnergyCrystalItem(Settings settings) {
        super(settings);
    }


    @Override
    public long getEnergyCapacity() {
        return 100000;
    }

    @Override
    public long getEnergyMaxInput() {
        return 128;
    }

    @Override
    public long getEnergyMaxOutput() {
        return 128;
    }
}
