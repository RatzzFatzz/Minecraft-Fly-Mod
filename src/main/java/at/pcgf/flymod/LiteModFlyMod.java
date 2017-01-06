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

import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.File;

@SuppressWarnings("FieldCanBeLocal,SpellCheckingInspection,UnusedAssignment,unused")
public class LiteModFlyMod implements Tickable {
    public static KeyBinding flyKey = new KeyBinding(I18n.format("key.flymod.fly"), Keyboard.KEY_B, I18n.format("key.categories.flymod"));

    public static byte flying = -1;
    public static Minecraft minecraft = Minecraft.getMinecraft();

    public static int flyDownKey;
    public static int flyUpKey;
    public static int speedKey;
    public static int backwardKey;
    public static int forwardKey;
    public static int leftKey;
    public static int rightKey;
    public static FlyModConfig config;

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getName() {
        return "Fly Mod";
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(flyKey);
        flyDownKey = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
        flyUpKey = Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode();
        speedKey = Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode();
        backwardKey = Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode();
        forwardKey = Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode();
        leftKey = Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode();
        rightKey = Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode();
        config = new FlyModConfig();
        LiteLoader.getInstance().registerExposable(config, null);
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (inGame && minecraft.currentScreen == null && Minecraft.isGuiEnabled()) {
            if (flyKey.isPressed()) {
                flying = (byte)(flying > 0 ? 0 : 1);
            }
        }
    }
}