package assortech.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import team.reborn.energy.api.base.SimpleBatteryItem;

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
