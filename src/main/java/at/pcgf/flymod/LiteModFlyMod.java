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

import at.pcgf.flymod.gui.FlyModSettings;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.client.util.InputUtil.KeyCode;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.input.Keyboard;


import java.io.File;

@SuppressWarnings("FieldCanBeLocal,SpellCheckingInspection,UnusedAssignment,unused")
public class LiteModFlyMod implements JoinGameListener, Tickable {
    public static FabricKeyBinding flyKey = FabricKeyBinding.Builder.create(
            new Identifier("flyKey"),
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.flymod"
    ).build();
    public static FabricKeyBinding settingsKey = FabricKeyBinding.Builder.create(
            new Identifier("settingsKey"),
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.flymod"
    ).build();

    public static byte flying = -1;
    public static MinecraftClient minecraft = MinecraftClient.getInstance();

    private static boolean backwardKeyPushed = false;
    private static boolean forwardKeyPushed = false;
    private static boolean leftKeyPushed = false;
    private static boolean rightKeyPushed = false;

    public static KeyBinding flyDownKey;
    public static KeyBinding flyUpKey;
    public static KeyBinding speedKey;
    public static KeyBinding backwardKey;
    public static KeyBinding forwardKey;
    public static KeyBinding leftKey;
    public static KeyBinding rightKey;
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
        KeyBindingRegistry.INSTANCE.register(flyKey);
        KeyBindingRegistry.INSTANCE.register(settingsKey);
        flyDownKey = MinecraftClient.getInstance().options.keySneak;
        flyUpKey = MinecraftClient.getInstance().options.keyJump;
        speedKey = MinecraftClient.getInstance().options.keySprint;
        backwardKey = MinecraftClient.getInstance().options.keyBack;
        forwardKey = MinecraftClient.getInstance().options.keyForward;
        leftKey = MinecraftClient.getInstance().options.keyLeft;
        rightKey = MinecraftClient.getInstance().options.keyRight;
        config = new FlyModConfig();
        LiteLoader.getInstance().registerExposable(config, null);
    }

    @Override
    public void onJoinGame(INetHandler netHandler, SPacketJoinGame joinGamePacket, ServerData serverData,
                           RealmsServer realmsServer) {
        flying = -1;
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (inGame && minecraft.currentScreen == null) {
            if (flyKey.isPressed()) {
                flying = (byte)(flying > 0 ? 0 : 1);
            } else if (settingsKey.isPressed()) {
                minecraft.displayGuiScreen(new FlyModSettings());
            }
        }
    }
}