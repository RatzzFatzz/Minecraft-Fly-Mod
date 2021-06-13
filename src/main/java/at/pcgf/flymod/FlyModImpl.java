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

package at.pcgf.flymod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static at.pcgf.flymod.FlyingState.*;

public class FlyModImpl implements ClientModInitializer {
    public static FlyingState flyingState = NOT_FLYING;
    public static final String MOD_ID = "flymod";
    private static final KeyBinding flyKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.flymod.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.flymod.keybinding"
    ));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(flyKey.wasPressed()) {
                flyingState = flyingState == FLYING ? NEUTRAL : FLYING;
            }
        });
    }
}