package spacefactory.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class ConditionalRecipeSerializer implements RecipeSerializer<Recipe<?>> {
    @Override
    public Recipe<?> read(Identifier id, JsonObject json) {
        if (JsonHelper.hasArray(json, "required_items")) {
            for (JsonElement requiredItem : JsonHelper.getArray(json, "required_items")) {
                String name = JsonHelper.asString(requiredItem, "required item");
                Optional<Item> item = Registry.ITEM.getOrEmpty(new Identifier(name));
                if (item.isEmpty()) {
                    return EmptyRecipe.INSTANCE;
                }
            }
        }

        if (JsonHelper.hasArray(json, "required_tags")) {
            for (JsonElement requiredItem : JsonHelper.getArray(json, "required_tags")) {
                String name = JsonHelper.asString(requiredItem, "required tag");
                Identifier tagId = new Identifier(name);
                if (!Registry.ITEM.containsTag(TagKey.of(Registry.ITEM_KEY, tagId))) {
                    return EmptyRecipe.INSTANCE;
                }
            }
        }
        JsonObject recipeJson = JsonHelper.getObject(json, "recipe");
        Identifier recipeType = new Identifier(JsonHelper.getString(recipeJson, "type"));
        RecipeSerializer<?> serializer = Registry.RECIPE_SERIALIZER.get(recipeType);
        if (serializer == null) {
            throw new JsonSyntaxException("Unknown nested recipe type: " + recipeType);
        }
        return serializer.read(id, recipeJson);
    }

    @Override
    public Recipe<?> read(Identifier id, PacketByteBuf buf) {
        return EmptyRecipe.INSTANCE;
    }

    @Override
    public void write(PacketByteBuf buf, Recipe<?> recipe) {

    }
}
