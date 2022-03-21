package spacefactory.api;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class EU {
	public interface Sender {
		default boolean canSend(Direction side) {
			return true;
		}
	}

	public interface Receiver {
		default boolean canReceive(Direction side) {
			return true;
		}

		int receive(int energy, Direction side);
	}

	public interface ElectricItem {
		boolean canCharge(ItemStack stack);
	}

	/** @return consumed energy, i.e. what was accepted by some receiver */
	public static int send(int energy, WorldAccess world, BlockPos pos, Direction side) {
		BlockEntity entity = world.getBlockEntity(pos);
		if (entity instanceof EU.Receiver receiver) {
			return receiver.receive(energy, side);
		}
		return 0;
	}

	public static boolean canSend(WorldAccess world, BlockPos pos, Direction side) {
		BlockEntity entity = world.getBlockEntity(pos);
		if (entity instanceof EU.Receiver receiver) {
			return receiver.canReceive(side);
		}
		return false;
	}

	public static EU.Receiver getReceiver(WorldAccess world, BlockPos pos) {
		BlockEntity entity = world.getBlockEntity(pos);
		if (entity instanceof EU.Receiver receiver) {
			return receiver;
		}
		return null;
	}

	public static boolean isElectricBlock(WorldAccess world, BlockPos pos, Direction side) {
		BlockEntity entity = world.getBlockEntity(pos);
		if (entity instanceof EU.Receiver receiver) {
			return receiver.canReceive(side);
		}
		if (entity instanceof EU.Sender sender) {
			return sender.canSend(side);
		}
		return false;
	}

	public static boolean isElectricItem(ItemStack stack) {
		return stack.getItem() instanceof ElectricItem;
	}
}
