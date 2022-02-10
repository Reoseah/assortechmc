package spacefactory.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Shadow
    private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes;

    @Shadow
    protected abstract <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllOfType(RecipeType<T> type);

    @Inject(at = @At("RETURN"), method = "apply")
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        this.recipes = this.recipes.entrySet().stream().map(entry -> Pair.of(entry.getKey(), new ImmutableMap.Builder<Identifier, Recipe<?>>().putAll(sortRecipes(entry.getValue())))).collect(ImmutableMap.toImmutableMap(Pair::getKey, pair -> pair.getValue().build()));
    }

    @Inject(at = @At("RETURN"), method = "getAllMatches", cancellable = true)
    public <C extends Inventory, T extends Recipe<C>> void getAllMatches(RecipeType<T> type, C inventory, World world, CallbackInfoReturnable<List<T>> callback) {
        callback.setReturnValue(callback.getReturnValue().stream().sorted(createRecipeComparator()).collect(Collectors.toList()));
    }

    private static Map<Identifier, Recipe<?>> sortRecipes(Map<Identifier, Recipe<?>> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(createRecipeComparator()))
                .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static <T extends Recipe<?>> Comparator<T> createRecipeComparator() {
        // first vanilla
        return Comparator.comparing((T recipe) -> !recipe.getId().getNamespace().equals("minecraft"))
                // then our recipes, thus they will override any conflicts
                // like TechReborn Iron Furnace
                .thenComparing((T recipe) -> !recipe.getId().getNamespace().equals("spacefactory"));
    }
}
