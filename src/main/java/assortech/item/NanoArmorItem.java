package assortech.item;

import assortech.api.EnergyItem;
import assortech.api.EnergyTier;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleBatteryItem;

import java.util.List;
import java.util.UUID;

public class NanoArmorItem extends ArmorItem implements EnergyItem, DynamicAttributeTool {
    public static final int[] CHARGED_PROTECTION = new int[]{3, 6, 8, 3};
    private static final UUID[] MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    public static final int ENERGY_COST = 500;

    public NanoArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }

    @Override
    public EnergyTier getEnergyTier() {
        return EnergyTier.MEDIUM;
    }

    @Override
    public long getEnergyCapacity() {
        return EnergyCrystalItem.CAPACITY;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getDynamicModifiers(EquipmentSlot slot, ItemStack stack, @Nullable LivingEntity user) {
        if (slot == this.slot) {
            UUID uuid = MODIFIERS[slot.getEntitySlotId()];

            ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();

            int protection = getStoredEnergy(stack) >= ENERGY_COST ? CHARGED_PROTECTION[slot.getEntitySlotId()] : this.getProtection();

            builder.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uuid, "Armor modifier", protection, EntityAttributeModifier.Operation.ADDITION));
            builder.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(uuid, "Armor toughness", getMaterial().getToughness(), EntityAttributeModifier.Operation.ADDITION));

            return builder.build();
        }
        return this.getAttributeModifiers(slot);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(new ItemStack(this));

            ItemStack full = new ItemStack(this);
            this.setStoredEnergy(full, this.getEnergyCapacity());
            stacks.add(full);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (stack.getCount() == 1) {
            tooltip.add(new TranslatableText("container.assortech.energy_storage", this.getStoredEnergy(stack), this.getEnergyCapacity()).formatted(Formatting.GRAY));
        }
    }

    public static void handlePlayerDamage(PlayerEntity entity, DamageSource source, float amount) {
        if (amount <= 0 || source.isUnblockable()) {
            return;
        }
        float damage = amount / 4;
        if (damage < 1.0F) {
            damage = 1.0F;
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (stack.getItem() instanceof NanoArmorItem) {
                SimpleBatteryItem.setStoredEnergyUnchecked(stack, Math.max(0, SimpleBatteryItem.getStoredEnergyUnchecked(stack) - ENERGY_COST * (long) damage));
                entity.equipStack(slot, stack);
            }
        }
    }
}
