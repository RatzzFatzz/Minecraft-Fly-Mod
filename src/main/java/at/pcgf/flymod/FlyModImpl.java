/*
 * Copyright (C) 2017 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgf.flymod;

import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1.ConfigHolder;
import me.sargunvohra.mcmods.autoconfig1.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class FlyModImpl implements ModInitializer, ClientModInitializer {
    public static FabricKeyBinding flyKey = FabricKeyBinding.Builder.create(
            new Identifier("flykey"),
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.flymod"
    ).build();


    public static byte flying = -1;
    public static ConfigHolder<FlyModConfig> config;

    @Override
    public void onInitializeClient() {
        config = AutoConfig.register(FlyModConfig.class, GsonConfigSerializer::new);

        KeyBindingRegistry.INSTANCE.register(flyKey);
        ClientTickCallback.EVENT.register(e -> {
            if(flyKey.wasPressed()){
                System.out.println("b is pressed");
                flying = (byte) (flying > 0 ? 0 : 1);
            }
        });

    }

    @Override
    public void onInitialize() {
        AutoConfig.getGuiRegistry(FlyModConfig.class);

    }
}