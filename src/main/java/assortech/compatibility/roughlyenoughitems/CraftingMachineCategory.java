package assortech.compatibility.roughlyenoughitems;

import assortech.compatibility.roughlyenoughitems.widgets.EnergyCostWidget;
import assortech.compatibility.roughlyenoughitems.widgets.MachineArrowWidget;
import com.google.common.collect.Lists;
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

import java.util.Collections;
import java.util.List;

public class CraftingMachineCategory implements DisplayCategory<CraftingMachineDisplay> {
    private final CategoryIdentifier<? extends CraftingMachineDisplay> id;
    private final EntryStack<?> logo;
    private final String name;

    public CraftingMachineCategory(CategoryIdentifier<? extends CraftingMachineDisplay> id, EntryStack<?> logo, String name) {
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
    public CategoryIdentifier<? extends CraftingMachineDisplay> getCategoryIdentifier() {
        return this.id;
    }

    public int getDisplayHeight() {
        return 49;
    }

    public DisplayRenderer getDisplayRenderer(CraftingMachineDisplay display) {
        return SimpleDisplayRenderer.from(Collections.singletonList(display.getInputEntries().get(0)), display.getOutputEntries());
    }

    public List<Widget> setupDisplay(CraftingMachineDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.y + 10);
        double duration = display.getDuration();
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 9)));
        widgets.add(new EnergyCostWidget(new Rectangle(startPoint.x + 1, startPoint.y + 20, 14, 14)).animationDurationMS(10000.0D));
        widgets.add(new MachineArrowWidget(new Rectangle(startPoint.x + 24, startPoint.y + 8, 24, 17), this.getArrowType()).animationDurationTicks(duration));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 9)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 1)).entries(display.getInputEntries().get(0)).markInput());
        return widgets;
    }

    private MachineArrowWidget.Type getArrowType() {
        if (this.id == AssortechREI.MACERATING) {
            return MachineArrowWidget.Type.MACERATING;
        }
        if (this.id == AssortechREI.COMPRESSING) {
            return MachineArrowWidget.Type.COMPRESSING;
        }
        if (this.id == AssortechREI.EXTRACTING) {
            return MachineArrowWidget.Type.EXTRACTING;
        }
        return MachineArrowWidget.Type.DEFAULT;
    }
}