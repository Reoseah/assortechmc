package spacefactory.features.machine.fabricator_ai;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import spacefactory.SpaceFactory;

import java.util.Map;

public class AIFabricationRecipe implements Recipe<Inventory> {
    protected final int width;
    protected final int height;
    protected final DefaultedList<Ingredient> input;
    protected final ItemStack output;
    protected final Identifier id;
    protected final String group;

    public AIFabricationRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> input, ItemStack output) {
        this.id = id;
        this.group = group;
        this.width = width;
        this.height = height;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SpaceFactory.RecipeSerializers.AI_FABRICATION;
    }

    @Override
    public RecipeType<?> getType() {
        return SpaceFactory.RecipeTypes.AI_FABRICATION;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.input;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        for (int x = 0; x <= 3 - this.width; ++x) {
            for (int y = 0; y <= 3 - this.height; ++y) {
                if (this.matchesPattern(inventory, x, y, true)) {
                    return true;
                }
                if (!this.matchesPattern(inventory, x, y, false)) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    private boolean matchesPattern(Inventory inventory, int offsetX, int offsetY, boolean flipped) {
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                int row = x - offsetX;
                int column = y - offsetY;
                Ingredient ingredient = Ingredient.EMPTY;
                if (row >= 0 && column >= 0 && row < this.width && column < this.height) {
                    ingredient = flipped ? this.input.get(this.width - row - 1 + column * this.width) : this.input.get(row + column * this.width);
                }
                if (ingredient.test(inventory.getStack(x + y * 3))) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return this.getOutput().copy();
    }

    public static class Serializer implements RecipeSerializer<AIFabricationRecipe> {
        @Override
        public AIFabricationRecipe read(Identifier identifier, JsonObject json) {
            String group = JsonHelper.getString(json, "group", "");
            Map<String, Ingredient> map = ShapedRecipe.readSymbols(JsonHelper.getObject(json, "key"));
            String[] pattern = ShapedRecipe.removePadding(ShapedRecipe.getPattern(JsonHelper.getArray(json, "pattern")));
            int width = pattern[0].length();
            int height = pattern.length;
            DefaultedList<Ingredient> ingredients = ShapedRecipe.createPatternMatrix(pattern, map, width, height);
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
            return new AIFabricationRecipe(identifier, group, width, height, ingredients, output);
        }

        @Override
        public AIFabricationRecipe read(Identifier identifier, PacketByteBuf buffer) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            String group = buffer.readString();
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(width * height, Ingredient.EMPTY);
            for (int k = 0; k < ingredients.size(); ++k) {
                ingredients.set(k, Ingredient.fromPacket(buffer));
            }
            ItemStack output = buffer.readItemStack();
            return new AIFabricationRecipe(identifier, group, width, height, ingredients, output);
        }

        @Override
        public void write(PacketByteBuf buffer, AIFabricationRecipe recipe) {
            buffer.writeVarInt(recipe.width);
            buffer.writeVarInt(recipe.height);
            buffer.writeString(recipe.group);
            for (Ingredient ingredient : recipe.input) {
                ingredient.write(buffer);
            }
            buffer.writeItemStack(recipe.output);
        }
    }
}
