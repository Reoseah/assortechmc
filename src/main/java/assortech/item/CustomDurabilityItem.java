package assortech.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/**
 * An item that provides custom durability bar.
 * <p>
 * It's also the only way to prevent Unbreaking/Mending enchantments (for example on a
 * battery).
 */
public interface CustomDurabilityItem {
    @Environment(EnvType.CLIENT)
    double getDurabilityBarProgress(ItemStack stack);

    @Environment(EnvType.CLIENT)
    boolean hasDurabilityBar(ItemStack stack);

    @Environment(EnvType.CLIENT)
    default int getDurabilityBarColor(ItemStack stack) {
        return MathHelper.hsvToRgb(Math.max(0.0F, 1 - (float) getDurabilityBarProgress(stack)) / 3.0F, 1.0F, 1.0F);
    }
}