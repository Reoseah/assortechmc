package spacefactory.screen.client.config;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.util.Strings;
import spacefactory.SpaceFactory;

@Environment(EnvType.CLIENT)
public class SpaceFactoryConfigScreen extends Screen {
    protected final Screen parent;

    //    protected EntriesWidget elementList;
    protected TextFieldWidget preferredNamespacesField;
    protected ButtonWidget resetAllButton;
    protected ButtonWidget closeButton;

    public SpaceFactoryConfigScreen(Screen parent) {
        super(new TranslatableText("options.spacefactory.title"));
        this.parent = parent;
    }

    public void onClose() {
        this.client.setScreen(this.parent);
        SpaceFactory.Config.write();
    }

    @Override
    public void tick() {
        this.preferredNamespacesField.tick();
    }

    @Override
    protected void init() {
//        this.elementList = this.addSelectableChild(new EntriesWidget(this));
        this.preferredNamespacesField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 60, 200, 20, new TranslatableText("options.spacefactory.preferred_namespaces"));
        this.preferredNamespacesField.setMaxLength(1000);
        this.preferredNamespacesField.setTextPredicate(text -> text.matches("[,a-zA-Z0-9_]*"));
        this.preferredNamespacesField.setText(Strings.join(SpaceFactory.config.preferredNamespaces, ','));
        this.preferredNamespacesField.setChangedListener(text -> {
            SpaceFactory.config.preferredNamespaces = Lists.newArrayList(text.split(","));
        });
        this.addDrawableChild(this.preferredNamespacesField);
        this.resetAllButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableText("controls.spacefactory.reset"), button -> {
            SpaceFactory.config.reset();
            this.preferredNamespacesField.setText(Strings.join(SpaceFactory.config.preferredNamespaces, ','));
        }));
        this.closeButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, ScreenTexts.DONE, button -> {
            this.onClose();
        }));

        this.setInitialFocus(this.preferredNamespacesField);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
//        this.elementList.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        drawTextWithShadow(matrices, this.textRenderer, new TranslatableText("options.spacefactory.preferred_namespaces.description"), this.width / 2 - 100, 47, -6250336);

        super.render(matrices, mouseX, mouseY, delta);
    }

//    @Environment(EnvType.CLIENT)
//    public abstract static class Entry extends ElementListWidget.Entry<Entry> {
//    }
//
//    @Environment(EnvType.CLIENT)
//    public static class EntriesWidget extends ElementListWidget<SpaceFactoryConfigScreen.Entry> {
//        private final SpaceFactoryConfigScreen screen;
//
//        public EntriesWidget(SpaceFactoryConfigScreen screen) {
//            super(MinecraftClient.getInstance(), screen.width + 45, screen.height, 20, screen.height - 32, 20);
//            this.screen = screen;
//        }
//    }
}
