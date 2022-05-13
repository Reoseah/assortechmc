package spacefactory.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.block.entity.FabricatorAIBlockEntity;
import spacefactory.core.screen.InventoryScreenHandler;
import spacefactory.core.screen.property.ReadProperty;
import spacefactory.core.screen.property.WriteProperty;
import spacefactory.recipe.AIFabricationRecipe;

import java.util.Optional;

public abstract class FabricatorAIScreenHandler extends InventoryScreenHandler {
    protected final PlayerEntity player;
    protected final Inventory result;

    protected FabricatorAIScreenHandler(int syncId, Inventory inventory, Inventory result, PlayerInventory user) {
        super(SpaceFactory.ScreenHandlerTypes.FABRICATOR_AI, syncId, inventory);

        this.player = user.player;
        this.result = result;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                this.addSlot(new Slot(inventory, column + row * 3, 40 + column * 18, 17 + row * 18));
            }
        }

        this.addSlot(new FabricatorAIScreenHandler.ResultSlot(player, inventory, result, 0, 134, 35));
        this.addQuickTransferSlot(EU::isElectricItem, new Slot(inventory, 8, 9, 53));
        this.addPlayerSlots(user);
    }

    public static class Server extends FabricatorAIScreenHandler implements InventoryChangedListener {
        public Server(int syncId, FabricatorAIBlockEntity be, PlayerEntity player) {
            super(syncId, be, be.getResultInventory(), player.getInventory());

            this.addProperty(new ReadProperty(be::getEnergy));
            this.addProperty(new ReadProperty(be::getCapacity));

            be.changeListeners.add(this);
        }

        @Override
        public void close(PlayerEntity player) {
            ((FabricatorAIBlockEntity) this.inventory).changeListeners.remove(this);
            super.close(player);
        }

        @Override
        public void onInventoryChanged(Inventory sender) {
            updateResult(this.syncId, this.player.world, this.player, (FabricatorAIBlockEntity) this.inventory, this.result);
        }

        protected static void updateResult(int syncId, World world, PlayerEntity player, FabricatorAIBlockEntity craftingInventory,
                                           Inventory resultInventory) {
            ItemStack result = ItemStack.EMPTY;
            Optional<AIFabricationRecipe> optional = world.getServer().getRecipeManager()
                    .getFirstMatch(SpaceFactory.RecipeTypes.AI_FABRICATION, craftingInventory, world);
            if (optional.isPresent()) {
                AIFabricationRecipe recipe = optional.get();
                result = recipe.craft(craftingInventory);
            }

            resultInventory.setStack(0, result);

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, 0, 9, result));
        }
    }

    public static class Client extends FabricatorAIScreenHandler  {
        protected int energy, capacity;

        public Client(int syncId, PlayerInventory user) {
            super(syncId, new SimpleInventory(10), new SimpleInventory(1), user);

            this.addProperty(new WriteProperty(value -> this.energy = value));
            this.addProperty(new WriteProperty(value -> this.capacity = value));
        }
    }

    public static class ResultSlot extends Slot {
        private final Inventory input;
        private final PlayerEntity player;
        private int amount;

        public ResultSlot(PlayerEntity player, Inventory input, Inventory inventory, int index,
                          int x, int y) {
            super(inventory, index, x, y);
            this.player = player;
            this.input = input;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack takeStack(int amount) {
            if (this.hasStack()) {
                this.amount += Math.min(amount, this.getStack().getCount());
            }
            return super.takeStack(amount);
        }

        @Override
        protected void onCrafted(ItemStack stack, int amount) {
            this.amount += amount;
            this.onCrafted(stack);
        }

        @Override
        protected void onTake(int amount) {
            this.amount += amount;
        }

        @Override
        protected void onCrafted(ItemStack stack) {
            if (this.amount > 0) {
                stack.onCraft(this.player.world, this.player, this.amount);
            }
            this.amount = 0;
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            this.onCrafted(stack);
            if (player.getWorld().isClient) {
                return;
            }
            FabricatorAIBlockEntity inventory = (FabricatorAIBlockEntity) this.input;

            AIFabricationRecipe recipe = player.world.getRecipeManager()
                    .getFirstMatch(SpaceFactory.RecipeTypes.AI_FABRICATION, inventory, player.world).orElseThrow();
            DefaultedList<ItemStack> remaining = recipe.getRemainder(inventory);

            for (int i = 0; i < Math.min(remaining.size(), 9); ++i) {
                ItemStack slotStack = inventory.getStack(i);
                ItemStack slotRemaining = remaining.get(i);
                if (!slotStack.isEmpty()) {
                    inventory.removeStack(i, 1);
                    slotStack = inventory.getStack(i);
                }

                if (!slotRemaining.isEmpty()) {
                    if (slotStack.isEmpty()) {
                        inventory.setStack(i, slotRemaining);
                    } else if (ItemStack.areItemsEqualIgnoreDamage(slotStack, slotRemaining)
                            && ItemStack.areNbtEqual(slotStack, slotRemaining)) {
                        slotRemaining.increment(slotStack.getCount());
                        inventory.setStack(i, slotRemaining);
                    } else if (!this.player.getInventory().insertStack(slotRemaining)) {
                        this.player.dropItem(slotRemaining, false);
                    }
                }
            }
        }
    }
}
