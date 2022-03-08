package spacefactory.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.recipe.MolecularAssemblerRecipe;
import spacefactory.screen.MolecularAssemblerScreenHandler;
import team.reborn.energy.api.EnergyStorageUtil;

public class MolecularAssemblerBlockEntity extends CraftingMachineBlockEntity<MolecularAssemblerRecipe> {
    public static final int SLOT_INPUT_1 = 0;
    public static final int SLOT_INPUT_2 = 1;
    public static final int SLOT_BATTERY = 2;
    public static final int SLOT_OUTPUT = 3;

    private static final int[] TOP_SLOTS = {SLOT_INPUT_1, SLOT_INPUT_2};
    private static final int[] BOTTOM_SLOTS = {SLOT_OUTPUT, SLOT_BATTERY};
    private static final int[] SIDE_SLOTS = {SLOT_BATTERY};

    public MolecularAssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.SFBlockEntityTypes.MOLECULAR_ASSEMBLER, pos, state);
    }

    @Override
    protected int getInventorySize() {
        return 4;
    }

    @Override
    protected RecipeType<MolecularAssemblerRecipe> getRecipeType() {
        return SpaceFactory.SFRecipeTypes.MOLECULAR_ASSEMBLY;
    }

    @Override
    protected int getEnergyPerTick() {
        return 10;
    }

    protected boolean canAcceptRecipeOutput(@Nullable MolecularAssemblerRecipe recipe) {
        if (recipe == null
                || this.inventory.get(SLOT_INPUT_1).isEmpty()
                || this.inventory.get(SLOT_INPUT_2).isEmpty()) {
            return false;
        }
        return this.canAccept(SLOT_OUTPUT, recipe.getOutput());
    }

    protected void craftRecipe(@Nullable MolecularAssemblerRecipe recipe) {
        if (this.canAcceptRecipeOutput(recipe)) {
            assert recipe != null;

            ItemStack input1 = this.inventory.get(SLOT_INPUT_1);
            ItemStack input2 = this.inventory.get(SLOT_INPUT_2);

            ItemStack slot = this.inventory.get(SLOT_OUTPUT);
            ItemStack output = recipe.getOutput();

            input1.decrement(recipe.input1.test(input1) ? recipe.input1.count : recipe.input2.count);
            input2.decrement(recipe.input2.test(input2) ? recipe.input2.count : recipe.input1.count);

            if (slot.isEmpty()) {
                this.inventory.set(SLOT_OUTPUT, output.copy());
            } else if (slot.getItem() == output.getItem()) {
                slot.increment(output.getCount());
            }
        }
    }

    @Override
    protected int getRecipeDuration(MolecularAssemblerRecipe recipe) {
        return recipe.getDuration();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new MolecularAssemblerScreenHandler(syncId, this, player);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack previous = this.inventory.get(slot);
        boolean needsRecipeUpdate = stack.isEmpty() || !stack.isItemEqualIgnoreDamage(previous) || !ItemStack.areNbtEqual(stack, previous);

        this.inventory.set(slot, stack);

        if (needsRecipeUpdate && (slot == SLOT_INPUT_1 || slot == SLOT_INPUT_2)) {
            this.resetCachedRecipe();
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack offer) {
        return switch (slot) {
            case SLOT_OUTPUT -> false;
            case SLOT_BATTERY -> EnergyStorageUtil.isEnergyStorage(offer);
            default -> true;
        };
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return switch (side) {
            case UP -> TOP_SLOTS;
            case DOWN -> BOTTOM_SLOTS;
            default -> SIDE_SLOTS;
        };
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        if (side == Direction.DOWN && slot == SLOT_BATTERY) {
            return !EnergyStorageUtil.isEnergyStorage(stack);
        }
        return true;
    }
}
