package spacefactory.core.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;


public class IngredientCount {
    public static Ingredient fromJson(JsonElement jsonElement) {
        Ingredient ingredient = Ingredient.fromJson(jsonElement);

        int count = JsonHelper.getInt(jsonElement.getAsJsonObject(), "count", 1);
        if (count != 1) {
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                stack.setCount(count);
            }
        }

        return ingredient;
    }
}
