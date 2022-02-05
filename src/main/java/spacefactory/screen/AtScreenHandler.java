package spacefactory.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Basically implements {@link #transferSlot} using map instead of having to hardcode it for every different machine.
 */
public abstract class AtScreenHandler extends ScreenHandler {
    protected final Inventory inventory;
    protected final List<Pair<Predicate<ItemStack>, Slot>> quickTransferMap = new ArrayList<>();
    protected int playerSlotsStart = -1;

    protected AtScreenHandler(ScreenHandlerType<?> type, int syncId, Inventory inventory) {
        super(type, syncId);
        this.inventory = inventory;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    /**
     * Adds typical 4 rows of player's slots to this screen handler.
     */
    protected void addPlayerSlots(PlayerInventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    protected Slot addQuickTransferSlot(Predicate<ItemStack> filter, Slot slot) {
        return this.addQuickTransferSlot(filter, this.quickTransferMap.size(), slot);
    }

    protected Slot addQuickTransferSlot(Predicate<ItemStack> filter, int index, Slot slot) {
        this.addSlot(slot);
        this.quickTransferMap.add(index, Pair.of(filter, slot));
        return slot;
    }

    @Override
    protected Slot addSlot(Slot slot) {
        super.addSlot(slot);
        if (this.playerSlotsStart < 0 && slot.inventory instanceof PlayerInventory) {
            this.playerSlotsStart = slot.id;
        }
        return slot;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        ItemStack stack = slot.getStack();
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack previous = stack.copy();
        if (index < this.playerSlotsStart) {
            if (!this.insertItem(stack, this.playerSlotsStart, this.playerSlotsStart + 36, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickTransfer(stack, previous);
        } else {
            for (Pair<Predicate<ItemStack>, Slot> entry : this.quickTransferMap) {
                if (entry.getKey().test(stack)
                        && (this.slots.get(entry.getValue().id).getStack().isItemEqual(stack)
                        || this.slots.get(entry.getValue().id).getStack().isEmpty())) {
                    if (!this.insertItem(stack, entry.getValue().id, entry.getValue().id + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            if (index < this.playerSlotsStart + 27) {
                if (!this.insertItem(stack, this.playerSlotsStart + 27, this.playerSlotsStart + 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(stack, this.playerSlotsStart, this.playerSlotsStart + 27, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        if (stack.getCount() == previous.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTakeItem(player, stack);
        return previous;
    }
}
