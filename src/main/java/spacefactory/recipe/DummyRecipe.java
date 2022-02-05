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

public class DummyRecipe implements Recipe<Inventory> {
    public static final DummyRecipe INSTANCE = new DummyRecipe();

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
        return new Identifier("spacefactory:dummy");
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SpaceFactory.SFRecipeSerializers.DUMMY;
    }

    @Override
    public RecipeType<?> getType() {
        return SpaceFactory.SFRecipeTypes.DUMMY;
    }

    public static class Serializer implements RecipeSerializer<DummyRecipe> {
        @Override
        public DummyRecipe read(Identifier id, JsonObject json) {
            return DummyRecipe.INSTANCE;
        }

        @Override
        public DummyRecipe read(Identifier id, PacketByteBuf buf) {
            return DummyRecipe.INSTANCE;
        }

        @Override
        public void write(PacketByteBuf buf, DummyRecipe recipe) {

        }
    }
}
