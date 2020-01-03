/*
 * Copyright (C) 2017 MarkusWME RatzzFatzz
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgf.flymod.gui;

import at.pcgf.flymod.FlyModConfig;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class SettingsModMenu implements ModMenuApi {
    @Override
    public String getModId() {
        return "flymod";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(FlyModConfig.class, screen).get();
    }
}
