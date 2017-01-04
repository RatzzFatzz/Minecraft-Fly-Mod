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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.PlayerMoveListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderEventBroker;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import com.mumfrey.liteloader.util.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import org.lwjgl.input.Keyboard;

import java.io.File;

@SuppressWarnings("SpellCheckingInspection,UnusedAssignment,unused")
@ExposableOptions(strategy = ConfigStrategy.Versioned, filename = "flymod.json")
public class LiteModFlyMod implements Tickable, PlayerMoveListener {
    private static KeyBinding flyKey = new KeyBinding("key.flymod.fly", Keyboard.KEY_B, "key.categories.flymod");

    private static byte flying = -1;

    private static int flyDownKey;
    private static int flyUpKey;
    private static int speedKey;
    private static int backwardKey;
    private static int forwardKey;

    @Expose
    @SerializedName("mouseControl")
    private boolean mouseControl = true;

    @Expose
    @SerializedName("flySpeedMultiplier")
    private int flySpeedMultiplier = 3;

    @Expose
    @SerializedName("runSpeedMultiplier")
    private int runSpeedMultiplier = 2;

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getName() {
        return "Fly Mod";
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
    }

    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(flyKey);
        flyDownKey = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
        flyUpKey = Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode();
        speedKey = Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode();
        backwardKey = Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode();
        forwardKey = Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode();
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (inGame && minecraft.currentScreen == null && Minecraft.isGuiEnabled()) {
            if (flyKey.isPressed()) {
                flying = (byte)(flying > 0 ? 0 : 1);
            }
        }
    }

    @Override
    public boolean onPlayerMove(EntityPlayerMP playerMP, Position from, Position to, LiteLoaderEventBroker.ReturnValue<Position> newPos) {
        boolean speedEnabled = Keyboard.isKeyDown(speedKey);
        double dx = to.xCoord - from.xCoord;
        double dy = to.yCoord - from.yCoord;
        double dz = to.zCoord - from.zCoord;
        KeyBinding.updateKeyBindState();
        if (flying > 0) {
            dy = 0.0;
            if (Keyboard.isKeyDown(flyDownKey)) {
                dy -= 0.2f;
            }
            if (Keyboard.isKeyDown(flyUpKey)) {
                dy += 0.2f;
            }
            double multiplier = speedEnabled ? 1.0 * flySpeedMultiplier : 1.0;
            dx *= multiplier;
            dy *= multiplier;
            dz *= multiplier;
            float pitch = Math.abs((float)(0.005f * multiplier) * playerMP.rotationPitch);
            if (mouseControl) {
                if (Keyboard.isKeyDown(forwardKey)) {
                    if (playerMP.rotationPitch > 0) {
                        dy -= pitch;
                    } else if (playerMP.rotationPitch < 0) {
                        dy += pitch;
                    }
                } else if (Keyboard.isKeyDown(backwardKey)) {
                    if (playerMP.rotationPitch > 0) {
                        dy += pitch;
                    } else if (playerMP.rotationPitch < 0) {
                        dy -= pitch;
                    }
                }
            }
            playerMP.fallDistance = 0.0f;
            playerMP.motionY = 0.0;
            playerMP.setSneaking(false);
            playerMP.onGround = true;
            playerMP.capabilities.isFlying = true;
            playerMP.sendPlayerAbilities();
            newPos = new LiteLoaderEventBroker.ReturnValue<Position>(new Position(from.xCoord + dx, from.yCoord + dy, from.zCoord + dz));
        } else if (flying == 0 && !playerMP.onGround) {
            playerMP.fallDistance = 0.0f;
            playerMP.onGround = true;
            if (!playerMP.capabilities.isCreativeMode) {
                playerMP.capabilities.isFlying = false;
                playerMP.sendPlayerAbilities();
            }
        } else if (flying < 0 && playerMP.onGround && speedEnabled) {
            dx *= runSpeedMultiplier;
            dz *= runSpeedMultiplier;
            newPos = new LiteLoaderEventBroker.ReturnValue<Position>(new Position(from.xCoord + dx, from.yCoord + dy, from.zCoord + dz));
        } else {
            flying = -1;
        }
        return true;
    }
}