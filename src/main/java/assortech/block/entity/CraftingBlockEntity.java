package assortech.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Block entity with recipe handling code based of vanilla furnace with some caching optimizations.
 * <p>
 * Important! Call #resetCachedRecipe when recipe inputs change, e.g. for item stacks:
 * <pre>
 * private static final int SLOT_INPUT = 0;
 *
 * public void setStack(int slot, ItemStack stack) {
 *     ItemStack previous = this.inventory.get(slot);
 *     boolean needsRecipeUpdate = stack.isEmpty() || !stack.isItemEqualIgnoreDamage(previous) || !ItemStack.areNbtEqual(stack, previous);
 *
 *     this.inventory.set(slot, stack);
 *
 *     if (needsRecipeUpdate && slot == SLOT_INPUT) {
 *         this.resetCachedRecipe(this.world);
 *     }
 * }
 * </pre>
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class CraftingBlockEntity<R extends Recipe<Inventory>> extends InventoryBlockEntity {
    protected @Nullable Optional<R> cachedRecipe;
    // progress on current recipe, such as ticks passed or energy consumed, i.e. `cookTime` in furnace
    protected int progress;
    // current recipe duration/required energy/whatever progress is measured in, i.e. `cookTimeTotal` in furnace
    protected int progressTarget;

    protected CraftingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract RecipeType<R> getRecipeType();

    protected abstract int getProgressTarget(R recipe);

    protected abstract int getProgressPerTick();

    protected abstract boolean canAcceptRecipeOutput(@Nullable R recipe);

    protected abstract void craftRecipe(@Nullable R recipe);

    // called when recipe progress was increased,
    // machines could consume energy here
    protected abstract void onProgressTick();

    // there was progress on recipe, but now both isActive and canActivate are false
    protected abstract void onCannotProgress();

    // e.g. `isBurning` in furnace, which is `fuelTime > 0`
    // machines can just track activity is as a boolean field
    // `onActivityChange` will be called whenever this has changed
    protected abstract boolean isActive();

    // e.g. has fuel to consume in furnace
    // for machines it's whether it has enough energy to do one tick of operation
    protected abstract boolean canActivate();

    // e.g. consume fuel for furnace,
    // IC2 style machines could consume redstone here
    // otherwise normal machines do nothing here
    protected abstract void tryActivate();

    // apply changes like setting lit state on furnace
    protected abstract void onActivityChange(boolean active);


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.progress = nbt.getShort("Progress");
        this.progressTarget = nbt.getShort("Target");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("Progress", (short) this.progress);
        nbt.putShort("Target", (short) this.progressTarget);
    }

    public static <R extends Recipe<Inventory>> void tick(World world, BlockPos pos, BlockState state, CraftingBlockEntity<R> be) {
        boolean dirty = false;
        boolean wasActive = be.isActive();

        if (be.progress > 0 && !be.isActive() && !be.canActivate()) {
            be.onCannotProgress();
        } else {
            R recipe = be.findRecipe(world);
            boolean canAcceptOutput = be.canAcceptRecipeOutput(recipe);

            if (!be.isActive() && be.canActivate() && canAcceptOutput) {
                be.tryActivate();
                be.progressTarget = be.getProgressTarget(recipe);
            }

            if (be.isActive() && canAcceptOutput) {
                be.onProgressTick();
                be.progress += be.getProgressPerTick();
                if (be.progress >= be.progressTarget) {
                    be.craftRecipe(recipe);

                    be.progress = 0;
                    be.progressTarget = be.getProgressTarget(be.findRecipe(world));
                }
            } else {
                be.progress = 0;
            }
        }
        if (wasActive != be.isActive()) {
            be.onActivityChange(be.isActive());
        }
    }


    @Nullable
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
    protected void resetCachedRecipe(World world) {
        this.cachedRecipe = null;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getProgressTarget() {
        return this.progressTarget;
    }
}
