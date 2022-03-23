package spacefactory.core.screen.property;

import net.minecraft.screen.Property;

import java.util.function.IntSupplier;

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
