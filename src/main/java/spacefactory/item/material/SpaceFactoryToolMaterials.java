package spacefactory.item.material;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import spacefactory.SpaceFactory;

public enum SpaceFactoryToolMaterials implements ToolMaterial {
	BRONZE(2, 350, 5.5F, 1.5F, 16, Ingredient.fromTag(SpaceFactory.Items.BRONZE_INGOTS)),
	REFINED_IRON(2, 750, 6.5F, 2.5F, 8, Ingredient.ofItems(SpaceFactory.Items.REFINED_IRON_INGOT));

	private final int miningLevel;
	private final int itemDurability;
	private final float miningSpeed;
	private final float attackDamage;
	private final int enchantability;
	private final Ingredient repairIngredient;

	SpaceFactoryToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Ingredient repairIngredient) {
		this.miningLevel = miningLevel;
		this.itemDurability = itemDurability;
		this.miningSpeed = miningSpeed;
		this.attackDamage = attackDamage;
		this.enchantability = enchantability;
		this.repairIngredient = repairIngredient;
	}

	@Override
	public int getDurability() {
		return this.itemDurability;
	}

	@Override
	public float getMiningSpeedMultiplier() {
		return this.miningSpeed;
	}

	@Override
	public float getAttackDamage() {
		return this.attackDamage;
	}

	@Override
	public int getMiningLevel() {
		return this.miningLevel;
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient;
	}
}
