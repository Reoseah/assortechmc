package assortech.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.Optional;

public abstract class CraftingMachineBlockEntity<R extends Recipe<Inventory>> extends InventoryBlockEntity {
    public static final int CAPACITY = 400;
    public static final int TRANSFER_LIMIT = 32;

    protected SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(CAPACITY, TRANSFER_LIMIT, 0) {
        @Override
        protected void onFinalCommit() {
            CraftingMachineBlockEntity.this.markDirty();
        }
    };
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected @Nullable Optional<R> cachedRecipe;
    protected int progress;
    protected int duration;
    protected boolean active = false;

    public CraftingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract RecipeType<R> getRecipeType();

    protected abstract int getRecipeDuration(R recipe);

    protected abstract int getProgressPerTick();

    protected abstract int getEnergyPerTick();

    protected abstract boolean canAcceptRecipeOutput(R recipe);

    protected abstract void craftRecipe(R recipe);

    protected abstract void onCannotContinue();

    public static <R extends Recipe<Inventory>> void tick(World world, BlockPos pos, BlockState state, CraftingMachineBlockEntity<R> be) {
        boolean wasActive = be.active;

        if (be.progress > 0 && !be.hasEnergy()) {
            be.onCannotContinue();
        } else {
            R recipe = be.findRecipe(world);

            if (be.canAcceptRecipeOutput(recipe) && be.hasEnergy()) {
                if (!be.active) {
                    be.active = true;
                    be.duration = be.getRecipeDuration(recipe);
                }
                be.energyStorage.amount -= be.getEnergyPerTick();
                be.progress += be.getProgressPerTick();
                if (be.progress >= be.duration) {
                    be.craftRecipe(recipe);

                    be.progress = 0;
                    be.duration = be.getRecipeDuration(be.findRecipe(world));
                }
                be.markDirty();
            } else if (be.active || be.progress > 0) {
                be.active = false;
                if (!be.canAcceptRecipeOutput(recipe)) {
                    be.progress = 0;
                }
                be.markDirty();
            }
        }
        if (wasActive != be.active) {
            be.world.setBlockState(be.pos, be.getCachedState().with(Properties.LIT, be.active), 3);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.progress = nbt.getShort("Progress");
        this.duration = nbt.getShort("Target");
        this.energyStorage.amount = nbt.getLong("Energy");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("Progress", (short) this.progress);
        nbt.putShort("Target", (short) this.duration);
        nbt.putLong("Energy", this.energyStorage.amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack previous = this.inventory.get(slot);
        boolean needsRecipeUpdate = stack.isEmpty() || !stack.isItemEqualIgnoreDamage(previous) || !ItemStack.areNbtEqual(stack, previous);

        this.inventory.set(slot, stack);

        if (needsRecipeUpdate && slot == 0) {
            this.resetCachedRecipe();
        }
    }

    public EnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    public int getEnergy() {
        return (int) this.energyStorage.amount;
    }

    public boolean hasEnergy() {
        return this.energyStorage.amount >= this.getEnergyPerTick();
    }


    @SuppressWarnings("OptionalAssignedToNull")
    protected R findRecipe(World world) {
        if (world == null) {
            return null;
        }
        if (this.cachedRecipe == null) {
            this.cachedRecipe = world.getRecipeManager().getFirstMatch(this.getRecipeType(), this, world);
        }
        return this.cachedRecipe == null ? null : this.cachedRecipe.orElse(null);
    }

    // call this if any entries change
    @SuppressWarnings("OptionalAssignedToNull")
    protected void resetCachedRecipe() {
        this.cachedRecipe = null;
        this.progress = 0;
        this.duration = 0;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getRecipeDuration() {
        return this.duration;
    }

    public boolean isActive() {
        return this.active;
    }
}
