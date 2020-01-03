package at.pcgf.flymod.gui;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class SettingsModMenu implements ModMenuApi {
    @Override
    public String getModId() {
        return "flymod";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return FlyModConfig::createConfigScreen;
    }
}
