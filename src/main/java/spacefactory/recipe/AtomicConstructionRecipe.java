package spacefactory.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import spacefactory.SpaceFactory;
import spacefactory.core.recipe.IngredientCount;

public class AtomicConstructionRecipe implements Recipe<Inventory> {
    public final Identifier id;
    public final Ingredient input1;
    public final Ingredient input2;
    public final ItemStack output;
    public final float experience;
    public final int duration;

    public AtomicConstructionRecipe(Identifier id, Ingredient input1, Ingredient input2, ItemStack output, float experience, int duration) {
        this.id = id;
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.experience = experience;
        this.duration = duration;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack first = inventory.getStack(0);
        ItemStack second = inventory.getStack(1);
        return this.input1.test(first) && (this.input2.test(second) || this.input2 == Ingredient.EMPTY)
                || this.input1.test(second) && (this.input2.test(first) || this.input2 == Ingredient.EMPTY);
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return this.getOutput().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SpaceFactory.RecipeSerializers.ATOMIC_CONSTRUCTION;
    }

    @Override
    public RecipeType<?> getType() {
        return SpaceFactory.RecipeTypes.ATOMIC_CONSTRUCTION;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return DefaultedList.copyOf(Ingredient.EMPTY, this.input1, this.input2);
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    public int getDuration() {
        return this.duration;
    }

    public static class Serializer implements RecipeSerializer<AtomicConstructionRecipe> {
        protected final int defaultDuration;

        public Serializer(int defaultDuration) {
            this.defaultDuration = defaultDuration;
        }

        @Override
        public AtomicConstructionRecipe read(Identifier id, JsonObject json) {
            JsonArray ingredientsJson = JsonHelper.getArray(json, "input");
            if (ingredientsJson.size() > 2) {
                throw new JsonParseException("Molecular Assembler recipe takes at most 2 items");
            }

            Ingredient input1 = IngredientCount.fromJson(ingredientsJson.get(0));
            Ingredient input2 = ingredientsJson.size() == 1 ? Ingredient.EMPTY : IngredientCount.fromJson(ingredientsJson.get(1));
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
            float experience = JsonHelper.getFloat(json, "experience", 0.0F);
            int cost = JsonHelper.getInt(json, "duration", this.defaultDuration);
            return new AtomicConstructionRecipe(id, input1, input2, output, experience, cost);
        }

        @Override
        public AtomicConstructionRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient input1 = Ingredient.fromPacket(buf);
            Ingredient input2 = Ingredient.fromPacket(buf);
            ItemStack output = buf.readItemStack();
            float experience = buf.readFloat();
            int cost = buf.readVarInt();
            return new AtomicConstructionRecipe(id, input1, input2, output, experience, cost);
        }

        @Override
        public void write(PacketByteBuf buf, AtomicConstructionRecipe recipe) {
            recipe.input1.write(buf);
            recipe.input2.write(buf);
            buf.writeItemStack(recipe.output);
            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.duration);
        }
    }
}
