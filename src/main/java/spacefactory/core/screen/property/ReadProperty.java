package spacefactory.core.screen.property;

import net.minecraft.screen.Property;

import java.util.function.IntSupplier;

/**
 * Read-only {@link Property} that can be initialized with lambda-function.
 * <p>
 * Properties are used only to sync screen handler data to client
 * and this logic is naturally closer to screen handler than block entity.
 * So, by high cohesion principle, instead of doing it Mojank way with property delegates,
 * property logic should be moved to screen handler.
 * {@link ReadProperty} on server paired with matching {@link WriteProperty} on client allow to do it in a readable way:
 * <pre>
 *     // on server
 *     this.addProperty(new ReadProperty(() -> be.getEnergy()));
 *     // on client
 *     this.addProperty(new WriteProperty(value -> this.energy = value));
 * </pre>
 */
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
