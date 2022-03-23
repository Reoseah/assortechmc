package spacefactory.core.screen.property;

import net.minecraft.screen.Property;

import java.util.function.IntConsumer;

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
