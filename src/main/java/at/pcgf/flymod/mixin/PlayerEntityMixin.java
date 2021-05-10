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

package at.pcgf.flymod.mixin;

import at.pcgf.flymod.FlyModImpl;
import at.pcgf.flymod.gui.FlyModConfigManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("unused")
@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerEntityMixin extends PlayerEntity {

    protected PlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    @Override
    public void move(MovementType type, Vec3d vec3d) {
        toggleFlying();

        if (abilities.flying) {
            Vec3d vec = mouseControlMovement(vec3d);
            vec = verticalMovement(vec);
            vec = applyFlyMultiplier(vec);

            setSneaking(false);
            setSprinting(false);
            super.move(type, vec);

        } else if (!abilities.flying) {
            Vec3d vec = vec3d;
            if (MinecraftClient.getInstance().options.keySprint.isPressed()) {
                vec = applyRunMultiplier(vec, FlyModConfigManager.getConfig().runSpeedMultiplier);
                setSprinting(false);
            }
            super.move(type, vec);
        } else {
            super.move(type, vec3d);
        }
    }

    private void toggleFlying() {
        abilities.flying = FlyModImpl.FLYING;
        sendAbilitiesUpdate();
    }

    private Vec3d mouseControlMovement(Vec3d vec3d) {
        if (FlyModConfigManager.getConfig().mouseControl) {
            boolean backwardsMovement = MinecraftClient.getInstance().options.keyBack.isPressed();
            boolean forwardsMovement = MinecraftClient.getInstance().options.keyForward.isPressed();
            boolean leftMovement = MinecraftClient.getInstance().options.keyLeft.isPressed();
            boolean rightMovement = MinecraftClient.getInstance().options.keyRight.isPressed();
            float pitch = prevPitch;
            float yaw = prevYaw;
            boolean invert = false;
            if (forwardsMovement) {
                if (rightMovement) {
                    yaw += 45.0f;
                } else if (leftMovement) {
                    yaw += 315.0f;
                }
            } else if (backwardsMovement) {
                if (rightMovement) {
                    yaw += 315.0f;
                } else if (leftMovement) {
                    yaw += 45.0f;
                }
                invert = true;
            } else if (rightMovement) {
                pitch = 0.0f;
                yaw += 90.0f;
            } else if (leftMovement) {
                pitch = 0.0f;
                yaw += 270.0f;
            }
            if (yaw > 180.0f) {
                yaw -= 360.0f;
            }
            Vec3d e = Vec3d.fromPolar(pitch, yaw).normalize();
            double length = Math.sqrt((vec3d.getX() * vec3d.getX()) + (vec3d.getZ() * vec3d.getZ()));
            if (invert) {
                length = -length;
            }

            if (!(backwardsMovement || forwardsMovement || leftMovement || rightMovement)) {
                setVelocityClient(0.0, 0.0, 0.0);
            }
            return new Vec3d(e.getX() * length, e.getY() * length, e.getZ() * length);
        }
        return vec3d;
    }

    public Vec3d verticalMovement(Vec3d vec3d) {
        double y = vec3d.getY();
        double flyUpDownBlocks = FlyModConfigManager.getConfig().flyUpDownBlocks;
        if (MinecraftClient.getInstance().options.keySneak.isPressed()) {
            y -= flyUpDownBlocks;
        } else if (MinecraftClient.getInstance().options.keyJump.isPressed()) {
            y += flyUpDownBlocks;
        }
        return new Vec3d(vec3d.getX(), y, vec3d.getZ());
    }

    private Vec3d applyFlyMultiplier(double x, double y, double z) {
        boolean speedEnabled = MinecraftClient.getInstance().options.keySprint.isPressed();
        float multiplier = speedEnabled ? FlyModConfigManager.getConfig().flySpeedMultiplier : 1.0f;
        float upDownMultiplier = FlyModConfigManager.getConfig().multiplyUpDown && speedEnabled ? multiplier : 1.0f;
        x *= multiplier;
        y *= upDownMultiplier;
        z *= multiplier;
        return new Vec3d(x, y, z);
    }

    private Vec3d applyFlyMultiplier(Vec3d vec3d) {
        return applyFlyMultiplier(vec3d.getX(), vec3d.getY(), vec3d.getZ());
    }

    private Vec3d applyRunMultiplier(Vec3d vec, float multiplier) {
        return new Vec3d(vec.getX() * multiplier, vec.getY(), vec.getZ() * multiplier);
    }
}