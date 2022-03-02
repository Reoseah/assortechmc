package spacefactory.compatibility.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import spacefactory.screen.client.config.SpaceFactoryConfigScreen;

@Environment(EnvType.CLIENT)
public class SpaceFactoryModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SpaceFactoryConfigScreen::new;
    }
}
