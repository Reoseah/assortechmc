package assortech.screen.property;

import java.util.function.IntSupplier;

import net.minecraft.screen.Property;

public class ReadProperty extends Property {
	protected final IntSupplier supplier;

	public ReadProperty(IntSupplier supplier) {
		this.supplier = supplier;
	}

	@Override
	public int get() {
		return this.supplier.getAsInt();
	}

	@Override
	public void set(int value) {
		throw new UnsupportedOperationException();
	}
}
