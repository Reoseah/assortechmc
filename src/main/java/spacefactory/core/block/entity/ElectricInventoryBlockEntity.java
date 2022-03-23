package spacefactory.core.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import spacefactory.api.EnergyTier;

public abstract class ElectricInventoryBlockEntity extends InventoryBlockEntity {
	protected int energy = 0;

	protected ElectricInventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected abstract EnergyTier getEnergyTier();

	public int getEnergy() {
		return this.energy;
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

	public static void tick(World world, BlockPos pos, BlockState state, ElectricInventoryBlockEntity be) {
	}
}
