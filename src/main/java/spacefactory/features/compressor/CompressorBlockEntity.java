package spacefactory.features.compressor;

import spacefactory.SpaceFactory;
import spacefactory.core.block.entity.CraftingMachineBlockEntity;
import spacefactory.features.compressor.CompressorRecipe;
import spacefactory.features.compressor.CompressorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CompressorBlockEntity extends CraftingMachineBlockEntity<CompressorRecipe> {
    public CompressorBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.COMPRESSOR, pos, state);
    }

    @Override
    protected RecipeType<CompressorRecipe> getRecipeType() {
        return SpaceFactory.RecipeTypes.COMPRESSING;
    }

    @Override
    protected int getEnergyPerTick() {
        return 2;
    }

    @Override
    protected int getRecipeDuration(CompressorRecipe recipe) {
        return recipe.getDuration();
    }

    @Override
    protected boolean canAcceptRecipeOutput(@Nullable CompressorRecipe recipe) {
        if (recipe == null || this.inventory.get(0).isEmpty()) {
            return false;
        }
        return this.canAccept(2, recipe.getOutput());
    }

    @Override
    protected void craftRecipe(@Nullable CompressorRecipe recipe) {
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
        return new CompressorScreenHandler(syncId, this, player);
    }
}
