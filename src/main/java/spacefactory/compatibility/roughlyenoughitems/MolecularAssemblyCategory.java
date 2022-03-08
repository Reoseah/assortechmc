package spacefactory.compatibility.roughlyenoughitems;

import com.google.common.collect.ImmutableList;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.DisplayRenderer;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.SimpleDisplayRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import spacefactory.compatibility.roughlyenoughitems.widgets.EnergyCostWidget;
import spacefactory.compatibility.roughlyenoughitems.widgets.MachineArrowWidget;

import java.util.Collections;
import java.util.List;

public class MolecularAssemblyCategory implements DisplayCategory<MolecularAssemblyDisplay> {
    private final CategoryIdentifier<? extends MolecularAssemblyDisplay> id;
    private final EntryStack<?> logo;
    private final String name;

    public MolecularAssemblyCategory(CategoryIdentifier<? extends MolecularAssemblyDisplay> id, EntryStack<?> logo, String name) {
        this.id = id;
        this.logo = logo;
        this.name = name;
    }

    @Override
    public Renderer getIcon() {
        return this.logo;
    }

    @Override
    public Text getTitle() {
        return new TranslatableText(this.name);
    }

    @Override
    public CategoryIdentifier<? extends MolecularAssemblyDisplay> getCategoryIdentifier() {
        return this.id;
    }

    public int getDisplayHeight() {
        return 67;
    }

    public DisplayRenderer getDisplayRenderer(MolecularAssemblyDisplay display) {
        return SimpleDisplayRenderer.from(Collections.singletonList(display.getInputEntries().get(0)), display.getOutputEntries());
    }

    public List<Widget> setupDisplay(MolecularAssemblyDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.y + 10);

        Widget base = Widgets.createRecipeBase(bounds);
        Widget resultBackground = Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 19));
        Widget energy = new EnergyCostWidget(new Rectangle(startPoint.x + 1, startPoint.y + 39, 14, 14)).animationDurationMS(10000.0D);
        Widget arrow = new MachineArrowWidget(new Rectangle(startPoint.x + 24, startPoint.y + 18, 24, 17), MachineArrowWidget.Type.MOLECULAR_ASSEMBLY).cost(this.getEuPerTick(), this.getEuTotal(display)).animationDurationTicks(display.getDuration());
        Widget output = Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 19)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput();
        Widget input1 = Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 1)).entries(display.getInputEntries().get(0)).markInput();
        Widget input2 = Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 19)).entries(display.getInputEntries().get(1)).markInput();

        return ImmutableList.of(base, resultBackground, energy, arrow, output, input1, input2);
    }

    private int getEuPerTick() {
        return 10;
    }

    private int getEuTotal(MolecularAssemblyDisplay display) {
        return this.getEuPerTick() * display.getDuration();
    }
}