package spacefactory.api;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Implement on a block that to interact with a wrench.
 * <p>
 * Why not use Zundrel's Wrenchable API or other alternatives? <br>
 * <p>
 * First, it doesn't return success/failure value - some blocks might be in such
 * context that they can't change to other state, and I don't want there to be
 * visuals and sound in that case. <br>
 * <p>
 * Second, it enforces a number of interaction that for me don't make sense,
 * such as rotating a log... within a tree!
 */
public interface Wrenchable {
    /**
     * @return whether player should swing a wrench + play wrench use sound +
     * consume durability in survival
     */
    boolean useWrench(BlockState state, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player,
                      Hand hand, Vec3d hitPos);


}