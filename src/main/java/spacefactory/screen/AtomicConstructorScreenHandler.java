package spacefactory.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.block.entity.AtomicConstructorBlockEntity;
import spacefactory.core.screen.ContainerScreenHandler;
import spacefactory.core.screen.property.ReadProperty;
import spacefactory.core.screen.property.WriteProperty;
import spacefactory.core.screen.slot.OutputSlot;

public class AtomicConstructorScreenHandler extends ContainerScreenHandler {
	protected boolean active;
	protected int energy, capacity;
	protected int progress, recipeDuration;

	private AtomicConstructorScreenHandler(int syncId, Inventory inventory, PlayerInventory user) {
		super(SpaceFactory.ScreenHandlerTypes.ATOMIC_RECONSTRUCTOR, syncId, inventory);

		this.addQuickTransferSlot(stack -> true, new Slot(inventory, AtomicConstructorBlockEntity.SLOT_INPUT_1, 53, 24));
		this.addQuickTransferSlot(stack -> true, new Slot(inventory, AtomicConstructorBlockEntity.SLOT_INPUT_2, 53, 46));
		this.addQuickTransferSlot(EU::isElectricItem, 0, new Slot(inventory, AtomicConstructorBlockEntity.SLOT_BATTERY, 8, 53));
		this.addSlot(new OutputSlot(inventory, AtomicConstructorBlockEntity.SLOT_OUTPUT, 116, 35));
		this.addPlayerSlots(user);
	}

	public AtomicConstructorScreenHandler(int syncId, AtomicConstructorBlockEntity be, PlayerEntity player) {
		this(syncId, be, player.getInventory());

		this.addProperty(new ReadProperty(() -> be.isActive() ? 1 : 0));
		this.addProperty(new ReadProperty(be::getEnergy));
		this.addProperty(new ReadProperty(be::getCapacity));
		this.addProperty(new ReadProperty(be::getProgress));
		this.addProperty(new ReadProperty(be::getRecipeDuration));
	}

	public AtomicConstructorScreenHandler(int syncId, PlayerInventory user) {
		this(syncId, new SimpleInventory(4), user);

		this.addProperty(new WriteProperty(value -> this.active = value == 1));
		this.addProperty(new WriteProperty(value -> this.energy = value));
		this.addProperty(new WriteProperty(value -> this.capacity = value));
		this.addProperty(new WriteProperty(value -> this.progress = value));
		this.addProperty(new WriteProperty(value -> this.recipeDuration = value));
	}

	@Environment(EnvType.CLIENT)
	public boolean isActive() {
		return this.active;
	}

	@Environment(EnvType.CLIENT)
	public int getRecipeDisplay() {
		int duration = this.recipeDuration == 0 ? 1000 : this.recipeDuration;
		return this.progress * 24 / duration;
	}

	@Environment(EnvType.CLIENT)
	public int getEnergyDisplay() {
		return this.energy * 13 / this.capacity;
	}

	@Environment(EnvType.CLIENT)
	public int getEnergy() {
		return this.energy;
	}

	@Environment(EnvType.CLIENT)
	public int getCapacity() {
		return this.capacity;
	}
}
