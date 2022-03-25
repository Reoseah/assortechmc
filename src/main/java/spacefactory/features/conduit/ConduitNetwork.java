package spacefactory.features.conduit;

import com.google.common.collect.MapMaker;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;

import java.util.*;

public class ConduitNetwork {
	private static final Map<World, ConduitNetwork> INSTANCES = new MapMaker().weakKeys().makeMap();

	public static ConduitNetwork of(World world) {
		if (!INSTANCES.containsKey(world)) {
			INSTANCES.put(world, new ConduitNetwork(world));
		}
		return INSTANCES.get(world);
	}

	protected final World world;
	/**
	 * Cable paths from a cable position to all connected consumers, sorted by length.
	 */
	private final Map<BlockPos, List<ConduitPath>> cache = new HashMap<>();

	private ConduitNetwork(World world) {
		this.world = world;
	}

	/**
	 * Tries to send specified amount of energy through cables, returns amount that was sent.
	 */
	public long send(BlockPos pos, int amount, TransactionContext transaction) {
        int left = amount;

		if (!this.cache.containsKey(pos)) {
			this.cache.put(pos, ConduitPath.buildPaths(this.world, pos, false));
		}

		if (this.cache.get(pos).size() == 0) {
			return 0;
		}

        int average = -Math.floorDiv(-amount, this.cache.get(pos).size()); // effectively "ceilDiv"
        int excess = 0;

		for (Iterator<ConduitPath> it = this.cache.get(pos).iterator(); it.hasNext(); ) {
			ConduitPath path = it.next();
			EU.Receiver endpoint = EU.findReceiver(this.world, path.end);
			if (endpoint == null || !endpoint.canReceiveEnergy(path.side)) {
				it.remove();
				if (this.cache.get(pos).isEmpty()) {
					this.cache.remove(pos);
				}
				continue;
			}
            int min = Math.min(left, average + excess);
            int inserted = endpoint.receiveEnergy(min, path.side);
			left -= inserted;
			excess = min - inserted;

			for (BlockPos cablePos : path.cables) {
				BlockEntity cablePosEntity = this.world.getBlockEntity(cablePos);
				if (cablePosEntity instanceof ConduitBlockEntity cable) {
					cable.current += inserted;
					if (cable.current > cable.getTransferLimit()) {
						world.removeBlock(cablePos, false);
						for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, cablePos)) {
							PacketByteBuf buf = PacketByteBufs.create();
							buf.writeBlockPos(cablePos);
							ServerPlayNetworking.send(player, SpaceFactory.id("burnt_cable"), buf);
						}
						break;
					}
				}
			}
			if (left == 0) {
				break;
			}
		}

		return amount - left;
	}

	public void onCableUpdate(BlockPos cable) {
		List<BlockPos> removed = new ArrayList<>();
		Deque<BlockPos> queue = new ArrayDeque<>();
		queue.add(cable);
		while (!queue.isEmpty()) {
			BlockPos pos = queue.remove();
			for (Direction direction : Direction.values()) {
				BlockPos neighbor = pos.offset(direction);
				if (this.cache.containsKey(neighbor) || ConduitPath.isCable(this.world, neighbor)) {
					this.cache.remove(neighbor);
				}
				if (ConduitPath.isCable(this.world, neighbor) && !queue.contains(neighbor) && !removed.contains(neighbor)) {
					removed.add(neighbor);
					queue.add(neighbor);
				}
			}
		}
		this.cache.remove(cable);
	}
}
