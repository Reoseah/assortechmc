package spacefactory.block.entity;

import spacefactory.SpaceFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import spacefactory.screen.ElectricFurnaceScreenHandler;

public class ElectricFurnaceBlockEntity extends CraftingMachineBlockEntity<SmeltingRecipe> {
    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.ELECTRIC_FURNACE, pos, state);
    }

    @Override
    protected RecipeType<SmeltingRecipe> getRecipeType() {
        return RecipeType.SMELTING;
    }

    @Override
    protected int getEnergyPerTick() {
        return SpaceFactory.config.electricFurnaceConsumption;
    }

    @Override
    protected int getRecipeDuration(SmeltingRecipe recipe) {
        return recipe.getCookTime() * 130 / 200;
    }

    @Override
    protected boolean canAcceptRecipeOutput(@Nullable SmeltingRecipe recipe) {
        if (recipe == null || this.inventory.get(0).isEmpty()) {
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
}
