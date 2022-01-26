package assortech.block.entity;

import assortech.api.EnergyTier;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.Optional;

public abstract class CraftingMachineBlockEntity<R extends Recipe<Inventory>> extends ElectricInventoryBlockEntity implements SidedInventory {
    public static final int CAPACITY = 400;

    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
    private static final int[] SIDE_SLOTS = new int[]{1};

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected @Nullable Optional<R> cachedRecipe;
    protected int progress;
    protected boolean active = false;

    public CraftingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract RecipeType<R> getRecipeType();

    protected abstract int getRecipeDuration(R recipe);

    protected abstract int getEnergyPerTick();

    protected abstract boolean canAcceptRecipeOutput(R recipe);

    protected abstract void craftRecipe(R recipe);

    public int getProgress() {
        return this.progress;
    }

    public boolean isActive() {
        return this.active;
    }

    public int getRecipeDuration() {
        if (this.cachedRecipe != null && this.cachedRecipe.isPresent()) {
            this.getRecipeDuration(this.cachedRecipe.get());
        }
        return 0;
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
    }

    @Override
    protected EnergyTier getEnergyTier() {
        return EnergyTier.LOW;
    }

    @Override
    protected int getEnergyCapacity() {
        return CAPACITY;
    }

    @Override
    protected boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected boolean canExtractEnergy() {
        return false;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.progress = nbt.getShort("Progress");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("Progress", (short) this.progress);
    }

    @Override
    protected int getInventorySize() {
        return 3;
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

    public static <R extends Recipe<Inventory>> void tick(World world, BlockPos pos, BlockState state, CraftingMachineBlockEntity<R> be) {
        ElectricInventoryBlockEntity.tick(world, pos, state, be);

        EnergyStorageUtil.move(be.getItemApi(1, EnergyStorage.ITEM), be.energy, Integer.MAX_VALUE, null);

        boolean wasActive = be.active;

        if (be.progress > 0 && !be.hasEnergyPerTick()) {
            be.progress = Math.max(be.progress - 2, 0);
        } else {
            R recipe = be.findRecipe(world);

            if (be.canAcceptRecipeOutput(recipe) && be.hasEnergyPerTick()) {
                if (!be.active) {
                    be.active = true;
                }
                be.energy.amount -= be.getEnergyPerTick();
                be.progress += 1;
                if (be.progress >= be.getRecipeDuration(recipe)) {
                    be.craftRecipe(recipe);

                    be.progress = 0;
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

    public boolean hasEnergyPerTick() {
        return this.energy.amount >= this.getEnergyPerTick();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return switch (side) {
            case DOWN -> BOTTOM_SLOTS;
            case UP -> TOP_SLOTS;
            default -> SIDE_SLOTS;
        };
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return switch (slot) {
            case 2 -> false;
            case 1 -> EnergyStorageUtil.isEnergyStorage(stack);
            default -> true;
        };
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }
}
