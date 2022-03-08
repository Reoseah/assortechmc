package spacefactory.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Ingredient with amount, for machines that take more than 1 item per operation.
 * <p>
 * It tries to read "count", then passes same json to {@link Ingredient#fromJson}.
 * If you want to use an array syntax, place it as "ingredient" field.
 * Usage:
 * <pre>
 * "input1": {
 *     "item": ...,
 *     "count": ...
 * },
 * "input2": {
 *     "tag": ...,
 *     "count": ...
 * },
 * "input3": {
 *     "ingredient": [{ ... (item, tag or an array of them) }],
 *     "count": ...
 * }
 * </pre>
 */
public class IngredientCount implements Predicate<ItemStack> {
    public static final IngredientCount EMPTY = new IngredientCount(Ingredient.EMPTY, 0);
    public final Ingredient ingredient;
    public final int count;

    public IngredientCount(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.count;
    }

    public static IngredientCount fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new JsonSyntaxException("Item entry cannot be null");
        }
        if (!jsonElement.isJsonObject()) {
            throw new JsonSyntaxException("Item entry must be a json object");
        }
        JsonObject json = jsonElement.getAsJsonObject();

        int count = JsonHelper.getInt(json, "count", 1);
        if (count <= 0 || count > 64) {
            throw new JsonSyntaxException("Item count must be between 0 and 64");
        }
        boolean hasIngredient = JsonHelper.hasElement(json, "ingredient");
        boolean hasItem = JsonHelper.hasElement(json, "item");
        boolean hasTag = JsonHelper.hasElement(json, "tag");

        if (hasIngredient && (hasItem || hasTag) || (hasItem && hasTag)) {
            throw new JsonSyntaxException("Expected either an ingredient, an item or a tag, found more than one");
        }
        Ingredient ingredient = Ingredient.fromJson(hasIngredient ? json.get("ingredient") : json);

        return new IngredientCount(ingredient, count);
    }

    public void write(PacketByteBuf buf) {
        this.ingredient.write(buf);
        buf.writeInt(this.count);
    }

    public static IngredientCount fromPacket(PacketByteBuf buf) {
        Ingredient ingredient = Ingredient.fromPacket(buf);
        int count = buf.readInt();
        return new IngredientCount(ingredient, count);
    }
}
