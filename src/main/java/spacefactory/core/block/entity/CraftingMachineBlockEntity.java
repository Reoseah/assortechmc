package spacefactory.core.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
import spacefactory.SpaceFactory;
import spacefactory.api.EU;

import java.util.Optional;

public abstract class CraftingMachineBlockEntity<R extends Recipe<Inventory>> extends MachineBlockEntity implements SidedInventory, EU.Receiver {
    private static final int[] TOP_SLOTS = {0};
    private static final int[] BOTTOM_SLOTS = {2, 1};
    private static final int[] SIDE_SLOTS = {1};

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected @Nullable Optional<R> cachedRecipe;
    protected int progress;
    protected boolean active = false;

    public CraftingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public int getCapacity() {
        return SpaceFactory.config.craftingMachineCapacity;
    }

    @Override
    protected int getReceiveRate() {
        return SpaceFactory.config.craftingMachineRate;
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

    public int getRecipeDuration() {
        return this.cachedRecipe != null && this.cachedRecipe.isPresent() ? this.getRecipeDuration(this.cachedRecipe.get()) : 0;
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

    public void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
        super.tick(world, pos, state, be);

        int batterySlot = this.getInventorySize() - 2; // all machines have 1 output currently, change later
        this.energy += EU.tryDischarge(this.getCapacity() - this.energy, this.getStack(batterySlot), stack -> this.setStack(batterySlot, stack));

        boolean wasActive = this.active;

        if (this.progress > 0 && !this.hasEnergyPerTick()) {
            this.progress = Math.max(this.progress - 2, 0);
        } else {
            R recipe = this.findRecipe(world);

            if (this.canAcceptRecipeOutput(recipe) && this.hasEnergyPerTick()) {
                if (!this.active) {
                    this.active = true;
                }
                this.energy -= this.getEnergyPerTick();
                this.progress += 1;
                if (this.progress >= this.getRecipeDuration(recipe)) {
                    this.craftRecipe(recipe);

                    this.progress = 0;
                }
                this.markDirty();
            } else if (this.active || this.progress > 0) {
                this.active = false;
                if (!this.canAcceptRecipeOutput(recipe)) {
                    this.progress = 0;
                }
                this.markDirty();
            }
        }
        if (wasActive != this.active) {
            world.setBlockState(this.pos, this.getCachedState().with(Properties.LIT, this.active), 3);
        }
    }

    public boolean hasEnergyPerTick() {
        return this.energy >= this.getEnergyPerTick();
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
            case 1 -> EU.isElectricItem(stack);
            default -> true;
        };
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }
}
