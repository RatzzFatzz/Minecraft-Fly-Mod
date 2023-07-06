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
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;

import static at.pcgf.flymod.FlyModImpl.flyingState;
import static at.pcgf.flymod.FlyingState.*;
import static org.joml.Math.cos;
import static org.joml.Math.sin;

@SuppressWarnings("unused")
@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerEntityMixin extends PlayerEntity {

    public PlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Override
    public void move(MovementType type, Vec3d vec3d) {
        MinecraftClient client = MinecraftClient.getInstance();
        toggleFlying();

        if (getAbilities().flying && isActiveForCurrentGamemode() && isActiveForCurrentServer()) {
            boolean backwards = client.options.backKey.isPressed();
            boolean forwards = client.options.forwardKey.isPressed();
            boolean left = client.options.leftKey.isPressed();
            boolean right = client.options.rightKey.isPressed();

            Vec3d vec = mouseControlMovement(vec3d, backwards, forwards, left, right);
            fadeMovement(backwards || forwards || left || right);
            vec = verticalMovement(vec);
            vec = applyFlyMultiplier(vec);

            setSneaking(false);
            setSprinting(false);
            super.move(type, vec);

        } else if (!getAbilities().flying && isActiveForCurrentGamemode() && isActiveForCurrentServer()) {
            Vec3d vec = vec3d;
            if (client.options.sprintKey.isPressed() || client.options.getSprintToggled().getValue()) {
                if (!FlyModConfigManager.getConfig().overrideExhaustion || isMultiplayer()) {
                    setSprinting(true);
                    // -0.3 to account for the boost by setSprinting(true)
                    vec = applyRunMultiplier(vec, FlyModConfigManager.getConfig().runSpeedMultiplier - 0.3F);
                } else {
                    setSprinting(false);
                    vec = applyRunMultiplier(vec, FlyModConfigManager.getConfig().runSpeedMultiplier);
                }
                setSwimming(isSubmergedInWater);
            }
            super.move(type, vec);
        } else {
            super.move(type, vec3d);
        }
    }

    private void toggleFlying() {
        if (!isActiveForCurrentGamemode() || !isActiveForCurrentServer()) {
            return;
        }

        if (flyingState == FLYING) {
            getAbilities().flying = true;
        } else if (flyingState == NEUTRAL) {
            flyingState = NOT_FLYING;
            getAbilities().flying = false;
        } else if (flyingState == NOT_FLYING && getAbilities().flying) {
            flyingState = FLYING;
        }

        sendAbilitiesUpdate();
    }

    private boolean isActiveForCurrentGamemode() {
        return !(FlyModConfigManager.getConfig().onlyForCreative && !getAbilities().creativeMode);
    }

    private boolean isActiveForCurrentServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        return (client.getServer() != null && client.getServer().isSingleplayer() && FlyModConfigManager.getConfig().activeInSingleplayer)
                || (client.getServer() == null && getAbilities().allowFlying);
//                || (client.getServer() == null && FlyModConfigManager.getConfig().activeInMultiplayer && FlyModConfigManager.getConfig().isFlyingAllowedInMultiplayer);
    }

    private boolean isMultiplayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        return !(client.getServer() != null && client.getServer().isSingleplayer()) && client.getServer() == null;
    }

    private Vec3d mouseControlMovement(final Vec3d vec3d, boolean backwards, boolean forwards, boolean left, boolean right) {
        if (FlyModConfigManager.getConfig().mouseControl) {
            float pitch = prevPitch;
            float yaw = prevYaw;
            final Vector4f directionsVector = new Vector4f(
                    (backwards ? 1 : 0) - (forwards ? 1 : 0),
                    0,
                    (left ? 1 : 0) - (right ? 1 : 0),
                    1);
            directionsVector.normalize();
            float length = (float) Math.sqrt((vec3d.getX() * vec3d.getX()) + (vec3d.getZ() * vec3d.getZ()));
            final Vector4f movementVector = multiply4dVector(directionsVector, length);

            // roll yaw pitch degree
            movementVector.rotate(quaternionOf(0, -(yaw - 90), pitch));

            float resultX = movementVector.x() / movementVector.w();
            float resultY = movementVector.y() / movementVector.w();
            float resultZ = movementVector.z() / movementVector.w();
            return new Vec3d(
                    Double.isNaN(resultX) ? 0 : resultX,
                    Double.isNaN(resultY) ? 0 : resultY,
                    Double.isNaN(resultZ) ? 0 : resultZ
            );
        }
        return vec3d;
    }

    private Vector4f multiply4dVector(final Vector4f vector, float length) {
        return new Vector4f(
                vector.x() * length,
                vector.y() * length,
                vector.z() * length,
                vector.w() * length
        );
    }

    /**
     * This is basically the implementation of the minecraft math constructor of Quaternion.
     * Quaternions were moved in 1.20 and do no longer offer this directly.
     */
    private Quaternionf quaternionOf(float x, float y, float z) {
        x *= 0.017453292F * 0.5F;
        y *= 0.017453292F * 0.5F;
        z *= 0.017453292F * 0.5F;

        float f = sin(x);
        float g = cos(x);
        float h = sin(y);
        float i = cos(y);
        float j = sin(z);
        float k = cos(z);
        return new Quaternionf(f * i * k + g * h * j,
                g * h * k - f * i * j,
                f * h * k + g * i * j,
                g * i * k - f * h * j);
    }

    private void fadeMovement(boolean isMoving) {
        if (FlyModConfigManager.getConfig().mouseControl) {
            return;
        }
        if (!isMoving && !FlyModConfigManager.getConfig().fadeMovement) {
            setVelocityClient(0.0, 0.0, 0.0);
        }
    }

    private Vec3d verticalMovement(final Vec3d moveVector) {
        double y = moveVector.getY();
        double flyUpDownBlocks = !isMultiplayer() ? FlyModConfigManager.getConfig().flyUpDownBlocks : 1;
        if (MinecraftClient.getInstance().options.sneakKey.isPressed() && MinecraftClient.getInstance().options.jumpKey.isPressed()) {
            y += 0;
        } else if (MinecraftClient.getInstance().options.sneakKey.isPressed()) {
            y -= flyUpDownBlocks;
        } else if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
            y += flyUpDownBlocks;
        }
        return new Vec3d(moveVector.getX(), y, moveVector.getZ());
    }

    private Vec3d applyFlyMultiplier(double x, double y, double z) {
        boolean speedEnabled = MinecraftClient.getInstance().options.sprintKey.isPressed();
        float multiplier = speedEnabled ? FlyModConfigManager.getConfig().flySpeedMultiplier : 1.0f;
        float upDownMultiplier = FlyModConfigManager.getConfig().multiplyUpDown && speedEnabled ? multiplier : 1.0f;
        x *= multiplier;
        y *= upDownMultiplier;
        z *= multiplier;
        return new Vec3d(x, y, z);
    }

    private Vec3d applyFlyMultiplier(final Vec3d moveVector) {
        if (isMultiplayer()) return moveVector;
        return applyFlyMultiplier(moveVector.getX(), moveVector.getY(), moveVector.getZ());
    }

    private Vec3d applyRunMultiplier(final Vec3d moveVector, float multiplier) {
        if (isMultiplayer()) return moveVector;
        return new Vec3d(moveVector.getX() * multiplier, moveVector.getY(), moveVector.getZ() * multiplier);
    }
}
