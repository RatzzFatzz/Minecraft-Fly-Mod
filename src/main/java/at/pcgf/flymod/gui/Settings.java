package at.pcgf.flymod.gui;

import at.pcgf.flymod.FlyModConfig;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class Settings implements ModMenuApi {
    @Override
    public String getModId() {
        return "flymod";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(FlyModConfig.class, screen).get();
    }
}
