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

package at.pcgf.flymod.mixin;

import at.pcgf.flymod.LiteModFlyMod;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("unused")
@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends EntityPlayer {
    public MixinAbstractClientPlayer(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
        boolean speedEnabled = Keyboard.isKeyDown(LiteModFlyMod.speedKey);
        if (LiteModFlyMod.flying > 0) {
            boolean backwards = Keyboard.isKeyDown(LiteModFlyMod.backwardKey);
            boolean forwards = Keyboard.isKeyDown(LiteModFlyMod.forwardKey);
            boolean left = Keyboard.isKeyDown(LiteModFlyMod.leftKey);
            boolean right = Keyboard.isKeyDown(LiteModFlyMod.rightKey);
            y = 0.0;
            if (Keyboard.isKeyDown(LiteModFlyMod.flyDownKey)) {
                y -= 0.2f;
            }
            if (Keyboard.isKeyDown(LiteModFlyMod.flyUpKey)) {
                y += 0.2f;
            }
            if (LiteModFlyMod.config.mouseControl && y == 0) {
                float yaw = rotationYaw;
                if (forwards) {
                    if (right) {
                        yaw += 45.0f;
                    } else if (left) {
                        yaw += 315.0f;
                    }
                } else if (backwards) {
                    if (right) {
                        yaw += 135.0f;
                    } else if (left) {
                        yaw += 225.0f;
                    } else {
                        yaw += 180.0f;
                    }
                } else if (right) {
                    yaw += 90.0f;
                } else if (left) {
                    yaw += 270.0f;
                }
                if (yaw > 180.0f) {
                    yaw -= 360.0f;
                }
                Vec3d e = Vec3d.fromPitchYaw(rotationPitch, yaw).normalize();
                double length = Math.sqrt((x * x) + (z * z));
                x = e.xCoord * length;
                y = e.yCoord * length;
                z = e.zCoord * length;
            }
            if (!(backwards || forwards || left || right)) {
                motionX = 0.0;
                motionZ = 0.0;
            }
            fallDistance = 0.0f;
            motionY = 0.0;
            setSneaking(false);
            capabilities.isFlying = true;
            sendPlayerAbilities();
            float multiplier = speedEnabled ? 1.0f * LiteModFlyMod.config.flySpeedMultiplier : 1.0f;
            x *= multiplier;
            y *= multiplier;
            z *= multiplier;
            super.move(type, x, y, z);
        } else if (LiteModFlyMod.flying == 0) {
            LiteModFlyMod.flying = -1;
            fallDistance = 0.0f;
            if (!capabilities.isCreativeMode) {
                capabilities.isFlying = false;
                sendPlayerAbilities();
            }
        } else if (LiteModFlyMod.flying < 0 && onGround && speedEnabled) {
            x *= LiteModFlyMod.config.runSpeedMultiplier;
            z *= LiteModFlyMod.config.runSpeedMultiplier;
            super.move(type, x, y, z);
        } else {
            super.move(type, x, y, z);
        }
    }
}