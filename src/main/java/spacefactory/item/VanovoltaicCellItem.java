package spacefactory.item;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import spacefactory.SpaceFactory;
import team.reborn.energy.api.EnergyStorage;

public class VanovoltaicCellItem extends Item implements EnergyStorage {
    public VanovoltaicCellItem(Settings settings) {
        super(settings);
        EnergyStorage.ITEM.registerSelf(this);
    }
    // TODO charging items in inventory at 10 EU/tick
    // TODO instability if more than one in inventory, like Extra Utilities' Unstable Ingots?

    @Override
    public boolean supportsInsertion() {
        return false;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return Math.min(maxAmount, SpaceFactory.Constants.VANOVOLTAIC_CELL_GENERATION);
    }

    @Override
    public long getAmount() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }
}
