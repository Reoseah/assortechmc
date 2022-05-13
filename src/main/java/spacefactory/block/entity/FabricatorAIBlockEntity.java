package spacefactory.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.core.block.entity.MachineBlockEntity;
import spacefactory.recipe.AIFabricationRecipe;
import spacefactory.screen.FabricatorAIScreenHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FabricatorAIBlockEntity extends MachineBlockEntity implements SidedInventory {
    private final Inventory result = new SimpleInventory(1);
    public final List<InventoryChangedListener> changeListeners = new ArrayList<>();

    public FabricatorAIBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.FABRICATOR_AI, pos, state);
    }

    public Inventory getResultInventory() {
        return this.result;
    }

    @Override
    protected int getInventorySize() {
        return 10;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = super.removeStack(slot, amount);
        for (InventoryChangedListener handler : this.changeListeners) {
            handler.onInventoryChanged(this);
        }
        this.updateResult();
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);
        for (InventoryChangedListener handler : this.changeListeners) {
            handler.onInventoryChanged(this);
        }
        this.updateResult();
    }

    protected void updateResult() {
        ItemStack result = ItemStack.EMPTY;
        Optional<AIFabricationRecipe> optional = this.getWorld().getRecipeManager()
                .getFirstMatch(SpaceFactory.RecipeTypes.AI_FABRICATION, this, this.getWorld());
        if (optional.isPresent()) {
            result = optional.get().craft(this);
        }

        this.result.setStack(0, result);
    }

    @Override
    public int getCapacity() {
        return 1000;
    }

    @Override
    protected int getReceiveRate() {
        return 10;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new FabricatorAIScreenHandler.Server(syncId, this, player);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{9};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return EU.isElectricItem(stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !EU.isElectricItem(stack);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
        super.tick(world, pos, state, be);

        Optional<AIFabricationRecipe> optionalRecipe = world.getRecipeManager().getFirstMatch(SpaceFactory.RecipeTypes.AI_FABRICATION, this, world);
    }
}
