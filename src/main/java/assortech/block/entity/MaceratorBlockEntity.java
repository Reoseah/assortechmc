package assortech.block.entity;

import assortech.Assortech;
import assortech.recipe.MaceratorRecipe;
import assortech.screen.MaceratorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public class MaceratorBlockEntity extends CraftingMachineBlockEntity<MaceratorRecipe> implements SidedInventory {
    public MaceratorBlockEntity(BlockPos pos, BlockState state) {
        super(Assortech.AtBlockEntityTypes.MACERATOR, pos, state);
    }

    @Override
    protected DefaultedList<ItemStack> createInventory() {
        return DefaultedList.ofSize(3, ItemStack.EMPTY);
    }

    @Override
    protected int getEnergyPerTick() {
        return 2;
    }

    @Override
    protected RecipeType<MaceratorRecipe> getRecipeType() {
        return Assortech.AtRecipeTypes.MACERATING;
    }

    @Override
    protected int getRecipeDuration(MaceratorRecipe recipe) {
        return recipe.getDuration();
    }

    @Override
    protected int getProgressPerTick() {
        return 1;
    }

    @Override
    protected void onCannotContinue() {
        this.progress = MathHelper.clamp(this.progress - 2, 0, this.duration);
    }

    @Override
    protected boolean canAcceptRecipeOutput(@Nullable MaceratorRecipe recipe) {
        if (recipe == null || this.inventory.get(0).isEmpty()) {
            return false;
        }
        return this.canAccept(2, recipe.getOutput());
    }

    @Override
    protected void craftRecipe(@Nullable MaceratorRecipe recipe) {
        if (this.canAcceptRecipeOutput(recipe)) {
            assert recipe != null;

            this.inventory.get(0).decrement(1);

            ItemStack slot = this.inventory.get(2);
            ItemStack output = recipe.getOutput();
            if (slot.isEmpty()) {
                this.inventory.set(2, output.copy());
            } else if (slot.getItem() == output.getItem()) {
                slot.increment(output.getCount());
            }
        }
    }


    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new MaceratorScreenHandler(syncId, this, player);
    }

    public static <R extends Recipe<Inventory>> void tick(World world, BlockPos pos, BlockState state, CraftingMachineBlockEntity<R> be) {
        EnergyStorage itemEnergy = be.getItemApi(1, EnergyStorage.ITEM);
        if (itemEnergy != null) {
            EnergyStorageUtil.move(itemEnergy, be.energyStorage, TRANSFER_LIMIT, null);
        }
        CraftingMachineBlockEntity.tick(world, pos, state, be);
    }

    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
    private static final int[] SIDE_SLOTS = new int[]{1};

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
