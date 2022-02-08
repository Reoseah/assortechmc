package spacefactory.recipe;

import spacefactory.SpaceFactory;
import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Empty recipe, doesn't match anything even if you try to search it.
 *
 * Allows to override any recipe like this:
 * <pre>
 * { "type": "spacefactory:empty" }
 * </pre>
 */
public class EmptyRecipe implements Recipe<Inventory> {
    public static final EmptyRecipe INSTANCE = new EmptyRecipe();

    @Override
    public boolean matches(Inventory inventory, World world) {
        return false;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public Identifier getId() {
        return new Identifier("spacefactory:empty");
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SpaceFactory.SFRecipeSerializers.EMPTY;
    }

    @Override
    public RecipeType<?> getType() {
        return SpaceFactory.SFRecipeTypes.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<EmptyRecipe> {
        @Override
        public EmptyRecipe read(Identifier id, JsonObject json) {
            return EmptyRecipe.INSTANCE;
        }

        @Override
        public EmptyRecipe read(Identifier id, PacketByteBuf buf) {
            return EmptyRecipe.INSTANCE;
        }

        @Override
        public void write(PacketByteBuf buf, EmptyRecipe recipe) {

        }
    }
}
