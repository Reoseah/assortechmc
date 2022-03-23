package spacefactory.features.pulverizer;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.core.block.entity.CraftingMachineBlockEntity;
import spacefactory.features.pulverizer.PulverizerRecipe;
import spacefactory.features.pulverizer.PulverizerScreenHandler;

public class PulverizerBlockEntity extends CraftingMachineBlockEntity<PulverizerRecipe> {
    public PulverizerBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.PULVERIZER, pos, state);
    }

    @Override
    protected RecipeType<PulverizerRecipe> getRecipeType() {
        return SpaceFactory.RecipeTypes.PULVERIZING;
    }

    @Override
    protected int getEnergyPerTick() {
        return 2;
    }

    @Override
    protected int getRecipeDuration(PulverizerRecipe recipe) {
        return recipe.getDuration();
    }

    @Override
    protected boolean canAcceptRecipeOutput(@Nullable PulverizerRecipe recipe) {
        if (recipe == null || this.inventory.get(0).isEmpty()) {
            return false;
        }
        return this.canAccept(2, recipe.getOutput());
    }

    @Override
    protected void craftRecipe(@Nullable PulverizerRecipe recipe) {
        if (this.canAcceptRecipeOutput(recipe)) {
            assert recipe != null;

            this.inventory.get(0).decrement(recipe.getIngredientCount());

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
        return new PulverizerScreenHandler(syncId, this, player);
    }
}
