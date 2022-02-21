package spacefactory.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import spacefactory.SpaceFactory;

public class WireCuttersItem extends Item {
    public WireCuttersItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        if (player != null && world.canPlayerModifyAt(player, pos)) {
            BlockState state = world.getBlockState(pos);
            if (state.isOf(SpaceFactory.SFBlocks.COPPER_WIRE)) {
                for (ItemStack stack : player.getInventory().main) {
                    if (SpaceFactory.SFItems.RUBBERS.contains(stack.getItem())) {
                        if (world.setBlockState(pos, SpaceFactory.SFBlocks.COPPER_CABLE.getStateWithProperties(state))) {
                            stack.decrement(1);
                            context.getStack().damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
                        }
                        return ActionResult.SUCCESS;
                    }
                }
                return ActionResult.PASS;
            } else if (state.isOf(SpaceFactory.SFBlocks.COPPER_CABLE)) {
                if (world.setBlockState(pos, SpaceFactory.SFBlocks.COPPER_WIRE.getStateWithProperties(state))) {
                    player.getInventory().insertStack(new ItemStack(SpaceFactory.SFItems.RUBBER));
                    context.getStack().damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
                }
                return ActionResult.SUCCESS;
            }
        }
        return super.useOnBlock(context);
    }
}
