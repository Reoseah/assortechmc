package spacefactory.api;

import net.minecraft.util.math.Direction;

public class LimitedEUReceiver implements EU.Receiver {
	protected final EU.Receiver parent;
	protected final int max;
	protected int total;

	public LimitedEUReceiver(EU.Receiver parent, int max) {
		this.parent = parent;
		this.max = max;
	}

	@Override
	public boolean canReceiveEnergy(Direction side) {
		return this.parent.canReceiveEnergy(side);
	}

	@Override
	public int receiveEnergy(int energy, Direction side) {
		int limited = Math.min(this.max - this.total, energy);
		return this.parent.receiveEnergy(limited, side);
	}

	public void resetLimit() {
		this.total = 0;
	}
}
