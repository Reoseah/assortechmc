package spacefactory.api;

import net.minecraft.util.math.Direction;

/**
 * Wrapper over parent EU.Receiver that restricts maximum energy received by it.
 * <p>
 * Together with {@link #resetLimit()} you can restrict maximum energy received per tick.
 */
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
		int limit = this.max - this.total;
		int accepted = this.parent.receiveEnergy(Math.min(energy, limit), side);
		this.total += accepted;
		return accepted;
	}

	public void resetLimit() {
		this.total = 0;
	}
}
