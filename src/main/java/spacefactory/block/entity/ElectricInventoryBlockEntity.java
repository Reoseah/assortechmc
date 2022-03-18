package spacefactory.block.entity;

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

    protected abstract int getEnergyCapacity();

    protected abstract boolean canInsertEnergy();

    protected abstract boolean canExtractEnergy();

    public int getEnergy() {
        return (int) this.energy;
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
//        be.resetLimits();
    }

//    /**
//     * Energy storage that has a limit not only on extraction/insertion per operation,
//     * but on total change between resetting. Limits are supposed to be reset every tick,
//     * thus limiting transfer per tick even if there are multiple energy sources trying to send energy.
//     */
//    public static class LimitedEnergyStorage extends SnapshotParticipant<long[]> implements EnergyStorage {
//        public final long capacity;
//        public final long maxInsert, maxExtract;
//        public long amount;
//        public long inserted, extracted;
//
//        public LimitedEnergyStorage(long capacity, long maxInsert, long maxExtract) {
//            this.capacity = capacity;
//            this.maxInsert = maxInsert;
//            this.maxExtract = maxExtract;
//        }
//
//        @Override
//        protected long[] createSnapshot() {
//            return new long[]{amount, inserted, extracted};
//        }
//
//        @Override
//        protected void readSnapshot(long[] snapshot) {
//            this.amount = snapshot[0];
//            this.inserted = snapshot[1];
//            this.extracted = snapshot[2];
//        }
//
//        @Override
//        public long insert(long maxAmount, TransactionContext transaction) {
//            long inserted = Math.min(maxInsert - this.inserted, Math.min(maxAmount, capacity - amount));
//
//            if (inserted > 0) {
//                updateSnapshots(transaction);
//                amount += inserted;
//                this.inserted += inserted;
//                return inserted;
//            }
//
//            return 0;
//        }
//
//        @Override
//        public long extract(long maxAmount, TransactionContext transaction) {
//            long extracted = Math.min(maxExtract - this.extracted, Math.min(maxAmount, amount));
//
//            if (extracted > 0) {
//                updateSnapshots(transaction);
//                amount -= extracted;
//                this.extracted += extracted;
//                return extracted;
//            }
//
//            return 0;
//        }
//
//        @Override
//        public long getAmount() {
//            return this.amount;
//        }
//
//        @Override
//        public long getCapacity() {
//            return this.capacity;
//        }
//
//        @Override
//        public boolean supportsExtraction() {
//            return maxExtract > 0;
//        }
//
//        @Override
//        public boolean supportsInsertion() {
//            return maxInsert > 0;
//        }
//
//        public void resetLimits() {
//            this.inserted = this.extracted = 0;
//        }
//    }
}
