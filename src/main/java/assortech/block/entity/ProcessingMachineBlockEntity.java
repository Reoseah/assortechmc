package assortech.block.entity;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public abstract class ProcessingMachineBlockEntity<R extends Recipe<Inventory>> extends AtRecipeBlockEntity<R> {
    public static final int CAPACITY = 400;
    public static final int TRANSFER_LIMIT = 32;

    protected SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(CAPACITY, TRANSFER_LIMIT, 0) {
        @Override
        protected void onFinalCommit() {
            ProcessingMachineBlockEntity.this.markDirty();
        }
    };
    protected boolean active = false;

    public ProcessingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract int getEnergyConsumption();

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.energyStorage.amount = nbt.getLong("Energy");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("Energy", this.energyStorage.amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack previous = this.inventory.get(slot);
        boolean needsRecipeUpdate = stack.isEmpty() || !stack.isItemEqualIgnoreDamage(previous) || !ItemStack.areNbtEqual(stack, previous);

        this.inventory.set(slot, stack);

        if (needsRecipeUpdate && slot == 0) {
            this.resetCachedRecipe(this.world);
        }
    }

    public EnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    public int getEnergy() {
        return (int) this.energyStorage.amount;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    protected boolean canActivate() {
        return this.energyStorage.amount >= this.getEnergyConsumption();
    }

    @Override
    protected void tryActivate() {
        if (this.energyStorage.amount >= this.getEnergyConsumption()) {
            this.active = true;
            this.markDirty();
        }
    }

    @Override
    protected void onActivityChange(boolean active) {
        this.active = active;
        this.markDirty();
        world.setBlockState(pos, this.getCachedState().with(Properties.LIT, active), 3);
    }

    @Override
    protected void onProgressTick() {
        this.energyStorage.amount -= this.getEnergyConsumption();
        if (this.energyStorage.amount < this.getEnergyConsumption()) {
            this.active = false;
        }
        this.markDirty();
    }


}
