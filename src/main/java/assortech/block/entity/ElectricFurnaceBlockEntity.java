package assortech.block.entity;

import assortech.Assortech;
import assortech.screen.ElectricFurnaceScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public class ElectricFurnaceBlockEntity extends CraftingMachineBlockEntity<SmeltingRecipe> implements SidedInventory {
    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(Assortech.AtBlockEntityTypes.ELECTRIC_FURNACE, pos, state);
    }

    @Override
    protected int getInventorySize() {
        return 3;
    }

    @Override
    protected int getEnergyPerTick() {
        return 3;
    }

    @Override
    protected RecipeType<SmeltingRecipe> getRecipeType() {
        return RecipeType.SMELTING;
    }

    @Override
    protected int getRecipeDuration(SmeltingRecipe recipe) {
        return recipe.getCookTime() * 130 / 200;
    }

    @Override
    protected boolean canAcceptRecipeOutput(@Nullable SmeltingRecipe recipe) {
        if (recipe == null
                || this.inventory.get(0).isEmpty()) {
            return false;
        }
        return this.canAccept(2, recipe.getOutput());
    }

    @Override
    protected void craftRecipe(@Nullable SmeltingRecipe recipe) {
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
        return new ElectricFurnaceScreenHandler(syncId, this, player);
    }

    public static <R extends Recipe<Inventory>> void tick(World world, BlockPos pos, BlockState state, CraftingMachineBlockEntity<R> be) {
        EnergyStorage itemEnergy = be.getItemApi(1, EnergyStorage.ITEM);
        if (itemEnergy != null) {
            EnergyStorageUtil.move(itemEnergy, be.energy, Integer.MAX_VALUE, null);
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
