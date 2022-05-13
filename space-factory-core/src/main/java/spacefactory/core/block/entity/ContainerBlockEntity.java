package spacefactory.core.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

/**
 * A simple block entity with typical implementation of {@link Inventory} and {@link Nameable}.
 */
public abstract class ContainerBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory, Nameable {
	protected final DefaultedList<ItemStack> inventory;
	protected @Nullable Text customName;

	protected ContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.inventory = DefaultedList.ofSize(this.getInventorySize(), ItemStack.EMPTY);
	}

	protected abstract int getInventorySize();

	public DefaultedList<ItemStack> getInventory() {
		return this.inventory;
	}

	/**
	 * Returns whether the stack can be fully inserted into the specified slot.
	 */
	protected boolean canAccept(int slot, ItemStack offer) {
		ItemStack stackInSlot = this.getStack(slot);
		if (stackInSlot.isEmpty() || offer.isEmpty()) {
			return true;
		}
		return ItemStack.canCombine(stackInSlot, offer)
				&& stackInSlot.getCount() + offer.getCount() <= stackInSlot.getMaxCount();
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.inventory.clear();
		Inventories.readNbt(nbt, this.inventory);
		if (nbt.contains("CustomName", 8)) {
			this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
		}
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, this.inventory);
		if (this.customName != null) {
			nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
		}
	}

	@Override
	public int size() {
		return this.inventory.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : this.inventory) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.inventory.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return Inventories.splitStack(this.inventory, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(this.inventory, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.inventory.set(slot, stack);
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.world.getBlockEntity(this.pos) == this
				&& player.squaredDistanceTo(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64;
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	@Override
	public Text getName() {
		if (this.customName != null) {
			return this.customName;
		}
		// vanilla uses "container.minecraft.name" for container names,
		// which just duplicates "block.minecraft.name"
		// not sure why is that needed
		return new TranslatableText(Util.createTranslationKey("block", Registry.BLOCK_ENTITY_TYPE.getId(this.getType())));
	}

	@Override
	public Text getDisplayName() {
		return this.getName();
	}

	@Override
	@Nullable
	public Text getCustomName() {
		return this.customName;
	}
}
