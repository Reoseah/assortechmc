package spacefactory.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;

public class DragonEggSiphonBlockEntity extends InventoryBlockEntity implements EU.Sender {
	public boolean generating = false, dragonEgg = false;

	public DragonEggSiphonBlockEntity(BlockPos pos, BlockState state) {
		super(SpaceFactory.BlockEntityTypes.DRAGON_EGG_SIPHON, pos, state);
	}

	@Override
	protected int getInventorySize() {
		return 0;
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return null;
	}

	public static void tick(World world, BlockPos pos, BlockState state, DragonEggSiphonBlockEntity be) {
		boolean dragonEgg = world.getBlockState(pos.up()).isOf(Blocks.DRAGON_EGG);
		if (dragonEgg) {
			if (!be.generating) {
				be.markDirty();
			}
			be.generating = true;

			int energy = SpaceFactory.Constants.DRAGON_EGG_SYPHON_OUTPUT;
			for (Direction side : Direction.values()) {
				if (energy == 0) {
					break;
				}
				energy -= EU.send(energy, world, pos.offset(side), side.getOpposite());
			}
			world.setBlockState(pos, state.with(Properties.LIT, true));
			return;
		}
		if (be.generating || dragonEgg != be.dragonEgg) {
			be.markDirty();
		}
		be.generating = false;
		be.dragonEgg = dragonEgg;
		world.setBlockState(pos, state.with(Properties.LIT, false));
	}

	@Override
	public boolean canSend(Direction side) {
		return side != Direction.UP;
	}
}
