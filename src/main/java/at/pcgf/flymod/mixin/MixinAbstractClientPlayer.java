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
            y = 0.0;
            if (Keyboard.isKeyDown(LiteModFlyMod.flyDownKey)) {
                y -= 0.2f;
            }
            if (Keyboard.isKeyDown(LiteModFlyMod.flyUpKey)) {
                y += 0.2f;
            }
            double multiplier = speedEnabled ? 1.0 * LiteModFlyMod.config.flySpeedMultiplier : 1.0;
            float verticalFly = 0.0f;
            float pitch = Math.abs((float)(0.005f * multiplier) * rotationPitch);
            if (LiteModFlyMod.config.mouseControl) {
                if (moveForward > 0.01f) {
                    if (rotationPitch > 0) {
                        verticalFly -= pitch;
                    } else if (rotationPitch < 0) {
                        verticalFly += pitch;
                    }
                } else if (moveForward < -0.01f) {
                    if (rotationPitch > 0) {
                        verticalFly += pitch;
                    } else if (rotationPitch < 0) {
                        verticalFly -= pitch;
                    }
                }
            }
            x *= multiplier;
            y *= multiplier;
            z *= multiplier;
            fallDistance = 0.0f;
            motionY = 0.0;
            if (LiteModFlyMod.config.mouseControl && y == 0) {
                y = verticalFly;
            }
            if (!(Keyboard.isKeyDown(LiteModFlyMod.backwardKey) || Keyboard.isKeyDown(LiteModFlyMod.forwardKey) || Keyboard.isKeyDown(LiteModFlyMod.leftKey) || Keyboard.isKeyDown(LiteModFlyMod.rightKey))) {
                motionX = 0.0;
                motionZ = 0.0;
            }
            setSneaking(false);
            capabilities.isFlying = true;
            sendPlayerAbilities();
            super.move(type, x, y, z);
        } else if (LiteModFlyMod.flying == 0 && !onGround) {
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
            LiteModFlyMod.flying = -1;
            super.move(type, x, y, z);
        }
    }
}