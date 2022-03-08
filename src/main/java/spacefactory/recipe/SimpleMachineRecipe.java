package spacefactory.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public abstract class SimpleMachineRecipe implements Recipe<Inventory> {
    protected final Identifier id;
    protected final Ingredient input;
    protected final int count;
    protected final ItemStack output;
    protected final int duration;
    protected final float experience;

    public SimpleMachineRecipe(Identifier id, Ingredient input, int count, ItemStack output, int duration, float experience) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.count = count;
        this.duration = duration;
        this.experience = experience;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        return this.input.test(inv.getStack(0)) && inv.getStack(0).getCount() >= this.count;
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return DefaultedList.copyOf(Ingredient.EMPTY, this.input);
    }

    public Ingredient getIngredient() {
        return this.input;
    }

    public float getMachineExperience() {
        return this.experience;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getIngredientCount() {
        return this.count;
    }

    public static class Serializer<T extends SimpleMachineRecipe> implements RecipeSerializer<T> {
        @FunctionalInterface
        public interface Factory<T extends SimpleMachineRecipe> {
            T create(Identifier id, Ingredient input, int count, ItemStack output, int duration, float experience);
        }

        protected final Factory<T> factory;
        protected final int defaultDuration;

        public Serializer(Factory<T> factory, int defaultDuration) {
            this.factory = factory;
            this.defaultDuration = defaultDuration;
        }

        @Override
        public T read(Identifier id, JsonObject json) {
            JsonElement ingredientJson = JsonHelper.hasArray(json, "ingredient") ? JsonHelper.getArray(json, "ingredient") : JsonHelper.getObject(json, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(ingredientJson);
            int count = 1;

            if (!JsonHelper.hasArray(json, "ingredient")) {
                count = JsonHelper.getInt(JsonHelper.getObject(json, "ingredient"), "count", 1);
            }

            ItemStack result = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
            int time = JsonHelper.getInt(json, "duration", this.defaultDuration);
            float experience = JsonHelper.getFloat(json, "experience", 0.0F);

            return this.factory.create(id, ingredient, count, result, time, experience);
        }

        @Override
        public T read(Identifier id, PacketByteBuf buf) {
            Ingredient ingredient = Ingredient.fromPacket(buf);
            int count = buf.readInt();
            ItemStack result = buf.readItemStack();
            float experience = buf.readFloat();
            int time = buf.readVarInt();

            return this.factory.create(id, ingredient, count, result, time, experience);
        }

        @Override
        public void write(PacketByteBuf buf, T recipe) {
            recipe.getIngredient().write(buf);
            buf.writeInt(recipe.getIngredientCount());
            buf.writeItemStack(recipe.getOutput());
            buf.writeFloat(recipe.getMachineExperience());
            buf.writeVarInt(recipe.getDuration());
        }
    }
}
