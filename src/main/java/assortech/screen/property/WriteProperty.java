package assortech.screen.property;

import java.util.function.IntConsumer;

import net.minecraft.screen.Property;

public class WriteProperty extends Property {
	protected final IntConsumer consumer;

	public WriteProperty(IntConsumer consumer) {
		this.consumer = consumer;
	}

	@Override
	public void set(int value) {
		this.consumer.accept(value);
	}

	@Override
	public int get() {
		return 0;
	}
}
