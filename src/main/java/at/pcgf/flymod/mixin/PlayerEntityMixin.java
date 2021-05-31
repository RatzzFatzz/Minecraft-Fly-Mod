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

import at.pcgf.flymod.gui.FlyModConfigManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import static at.pcgf.flymod.FlyModImpl.flyingState;
import static at.pcgf.flymod.FlyingState.*;

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
            boolean backwards = MinecraftClient.getInstance().options.keyBack.isPressed();
            boolean forwards = MinecraftClient.getInstance().options.keyForward.isPressed();
            boolean left = MinecraftClient.getInstance().options.keyLeft.isPressed();
            boolean right = MinecraftClient.getInstance().options.keyRight.isPressed();

            Vec3d vec = mouseControlMovement(vec3d, backwards, forwards, left, right);
            fadeMovement(backwards || forwards || left || right);
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
        if (flyingState == FLYING) {
            abilities.flying = true;
        } else if (flyingState == NEUTRAL) {
            flyingState = NOT_FLYING;
            abilities.flying = false;
        } else if (flyingState == NOT_FLYING && abilities.flying) {
            flyingState = FLYING;
        }
        sendAbilitiesUpdate();
    }

    private Vec3d mouseControlMovement(Vec3d vec3d, boolean backwards, boolean forwards, boolean left, boolean right) {
        if (FlyModConfigManager.getConfig().mouseControl) {
            float pitch = prevPitch;
            float yaw = prevYaw;
            Vector4f directionsVector = new Vector4f(
                    (backwards ? 1 : 0) - (forwards ? 1 : 0),
                    0,
                    (left ? 1 : 0) - (right ? 1 : 0),
                    1);
            directionsVector.normalize();
            float length = (float) Math.sqrt((vec3d.getX() * vec3d.getX()) + (vec3d.getZ() * vec3d.getZ()));
            Vector4f movementVector = multiply4dVector(directionsVector, length);
            // roll yaw pitch degree
            movementVector.rotate(new Quaternion(0, -(yaw - 90), pitch, true));

            float resultX = movementVector.getX() / movementVector.getW();
            float resultY = movementVector.getY() / movementVector.getW();
            float resultZ = movementVector.getZ() / movementVector.getW();
            return new Vec3d(
                    Double.isNaN(resultX) ? 0 : resultX,
                    Double.isNaN(resultY) ? 0 : resultY,
                    Double.isNaN(resultZ) ? 0 : resultZ
            );
        }
        return vec3d;
    }

    private Vector4f multiply4dVector(Vector4f vector, float length) {
        return new Vector4f(
                vector.getX() * length,
                vector.getY() * length,
                vector.getZ() * length,
                vector.getW() * length
        );
    }

    private void fadeMovement(boolean isMoving) {
        if (FlyModConfigManager.getConfig().mouseControl) {
            return;
        }
        if (!isMoving && !FlyModConfigManager.getConfig().fadeMovement) {
            setVelocityClient(0.0, 0.0, 0.0);
        }
    }

    private Vec3d verticalMovement(Vec3d vec3d) {
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