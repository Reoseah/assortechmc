package spacefactory.mixin;

import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.List;

@Mixin(Ingredient.class)
public class IngredientMixin {
    @Shadow
    @Final
    private Ingredient.Entry[] entries;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IngredientAccessor that = (IngredientAccessor) o;

        List<Ingredient.Entry> thisEntries = Arrays.asList(this.entries);
        List<Ingredient.Entry> thatEntries = Arrays.asList(that.getEntries());

        return thisEntries.containsAll(thatEntries) && thatEntries.containsAll(thisEntries);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(entries);
    }
}
