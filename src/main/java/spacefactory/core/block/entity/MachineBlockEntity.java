package spacefactory.core.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import spacefactory.api.EU;

public abstract class MachineBlockEntity extends InventoryBlockEntity implements EU.Receiver, BlockEntityTicker<BlockEntity> {
	protected int energy = 0;
	protected int current = 0;

	protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public int getEnergy() {
		return this.energy;
	}

	protected abstract int getCapacity();

	protected abstract int getReceiveRate();

	@Override
	public void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
		this.current = 0;
	}

	@Override
	public int receiveEnergy(int energy, Direction side) {
		int limit = this.getReceiveRate() - this.current;
		int accepted = this.addEnergy(Math.min(energy, limit));
		this.current += accepted;
		return accepted;
	}

	protected int addEnergy(int amount) {
		int added = Math.min(amount, this.getCapacity() - this.energy);
		this.energy += added;
		if (added > 0) {
			this.markDirty();
		}
		return added;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.energy = nbt.getInt("Energy");
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("Energy", this.energy);
	}
}
