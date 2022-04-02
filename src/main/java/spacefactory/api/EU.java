package spacefactory.api;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Contains core API for using SpaceFactory EU energy.
 */
public abstract class EU {
    /**
     * Implement on a block that needs to interact with EU electricity
     * and override methods if you need to customize behavior.
     * <p>
     * The default behavior is try to cast block entity to {@link EU.Receiver} or {@link EU.Sender}.
     */
    public interface ElectricBlock {
        default @Nullable EU.Sender getEnergySender(BlockState state, WorldAccess world, BlockPos pos) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof EU.Sender sender) {
                return sender;
            }
            return null;
        }


        default @Nullable EU.Receiver getEnergyReceiver(BlockState state, WorldAccess world, BlockPos pos) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof EU.Receiver receiver) {
                return receiver;
            }
            return null;
        }
    }

    /**
     * Something that will send energy using {@link EU#trySend} or {@link EU#findReceiver}.
     */
    public interface Sender {
        /**
         * Returns whether this object can send energy to the given side,
         * for example, solar panels might not send energy up, batteries might only send from one side.
         * <p>
         * Currently, used by cables when deciding whether to connect to a block.
         */
        default boolean canSendEnergy(Direction side) {
            return true;
        }
    }

    public interface Receiver {
        /**
         * Returns whether this object can receive energy from the given side,
         * for example, a battery might only receive energy from some sides.
         * <p>
         * Currently, used by cables when deciding whether to connect to a block.
         */
        default boolean canReceiveEnergy(Direction side) {
            return true;
        }

        /**
         * Receives energy from the given side, returns what was accepted.
         */
        int receiveEnergy(int energy, Direction side);
    }

    /**
     * Sends energy to the block at the given position if possible.
     *
     * @return consumed energy, i.e. what was accepted by the receiver
     */
    public static int trySend(int energy, WorldAccess world, BlockPos pos, Direction side) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof EU.ElectricBlock block) {
            EU.Receiver receiver = block.getEnergyReceiver(state, world, pos);
            if (receiver != null) {
                return receiver.receiveEnergy(energy, side);
            }
        }
        return 0;
    }

    public static EU.Receiver findReceiver(WorldAccess world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof EU.ElectricBlock block) {
            return block.getEnergyReceiver(state, world, pos);
        }
        return null;
    }

    public static boolean canInteract(WorldAccess world, BlockPos pos, Direction side) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof EU.ElectricBlock block) {
            EU.Receiver receiver = block.getEnergyReceiver(state, world, pos);
            if (receiver != null && receiver.canReceiveEnergy(side)) {
                return true;
            }
            EU.Sender sender = block.getEnergySender(state, world, pos);
            return sender != null && sender.canSendEnergy(side);
        }
        return false;
    }

    /**
     * Implement on items that need to interface with EU energy.
     */
    public interface ElectricItem {
        int getEnergy(ItemStack stack);

        ItemStack withEnergy(ItemStack stack, int amount);

        int getCapacity(ItemStack stack);

        int getTransferLimit(ItemStack stack);

        default boolean canCharge(ItemStack stack) {
            return false;
        }

        default int charge(ItemStack stack, int amount, Consumer<ItemStack> stackSaver) {
            if (!this.canCharge(stack) || stack.getCount() > 1) {
                return 0;
            }
            int change = Math.min(Math.min(this.getCapacity(stack) - this.getEnergy(stack), amount), this.getTransferLimit(stack));
            stackSaver.accept(this.withEnergy(stack, this.getEnergy(stack) + change));
            return change;
        }

        default boolean canDischarge(ItemStack stack) {
            return false;
        }

        default int discharge(ItemStack stack, int max, Consumer<ItemStack> stackSaver) {
            if (!this.canDischarge(stack) || stack.getCount() > 1) {
                return 0;
            }
            int change = Math.min(Math.min(this.getEnergy(stack), max), this.getTransferLimit(stack));
            stackSaver.accept(this.withEnergy(stack, this.getEnergy(stack) - change));
            return change;
        }

    }

    public static boolean isElectricItem(ItemStack stack) {
        return stack.getItem() instanceof ElectricItem;
    }

    public static int tryCharge(int energy, ItemStack stack, Consumer<ItemStack> stackSaver) {
        if (stack.getItem() instanceof ElectricItem electricItem) {
            return electricItem.charge(stack, energy, stackSaver);
        }
        return 0;
    }

    public static int tryDischarge(int max, ItemStack stack, Consumer<ItemStack> stackSaver) {
        if (stack.getItem() instanceof ElectricItem electricItem) {
            return electricItem.discharge(stack, max, stackSaver);
        }
        return 0;
    }

    /**
     * Implement this interface on your item class to make it function as a simple non-rechargeable battery.
     */
    public interface SimpleDischargeableItem extends EU.ElectricItem, FabricItem {
        String ENERGY_KEY = "energy";

        @Override
        default int getEnergy(ItemStack stack) {
            return stack.hasNbt() ? stack.getNbt().getInt(ENERGY_KEY) : 0;
        }

        default ItemStack withEnergy(ItemStack stack, int energy) {
            if (energy == 0) {
                stack.removeSubNbt(ENERGY_KEY);
                return stack;
            } else {
                stack.getOrCreateNbt().putInt(ENERGY_KEY, energy);
                return stack;
            }
        }

        @Override
        default boolean canDischarge(ItemStack stack) {
            return true;
        }

        @Override
        default boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
            return ItemStack.areItemsEqual(oldStack, newStack);
        }

        @Override
        default boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
            return ItemStack.areItemsEqual(oldStack, newStack);
        }
    }

    /**
     * Implement this interface on your item class to make it function as a simple rechargeable battery.
     */
    public interface SimpleRechargeableItem extends SimpleDischargeableItem {
        @Override
        default boolean canCharge(ItemStack stack) {
            return true;
        }
    }

    private EU() {
        throw new IllegalStateException();
    }
}
