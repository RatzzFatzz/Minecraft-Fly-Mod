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

import at.pcgf.flymod.FlyModConfig;
import at.pcgf.flymod.LiteModFlyMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("unused")
@Mixin(PlayerEntity.class)
public abstract class MixinAbstractClientPlayer extends LivingEntity {

    protected MixinAbstractClientPlayer(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void move(MovementType type, Vec3d vec3d) {
        double x = vec3d.getX();
        double y = vec3d.getY();
        double z = vec3d.getZ();
        boolean speedEnabled = MinecraftClient.getInstance().options.keySprint.isPressed();
        if(LiteModFlyMod.flying > 0){
            boolean backwardsMovement = MinecraftClient.getInstance().options.keyBack.isPressed();
            boolean forwardsMovement = MinecraftClient.getInstance().options.keyForward.isPressed();
            boolean leftMovement = MinecraftClient.getInstance().options.keyLeft.isPressed();
            boolean rightMovement = MinecraftClient.getInstance().options.keyRight.isPressed();
            y = 0.0;
            double flyUpDownBlocks = LiteModFlyMod.config.getConfig().multiplyUpDown && speedEnabled ?
                    LiteModFlyMod.config.getConfig().flyUpDownBlocks * LiteModFlyMod.config.getConfig().flySpeedMultiplier:
                    LiteModFlyMod.config.getConfig().flyUpDownBlocks;
            if(MinecraftClient.getInstance().options.keySneak.isPressed()){
                y -= flyUpDownBlocks;
            }else if(MinecraftClient.getInstance().options.keyJump.isPressed()){
                y += flyUpDownBlocks;
            }
            if(LiteModFlyMod.config.getConfig().mouseControl && y == 0){
                float pitch = prevPitch;
                float yaw = prevYaw;
                boolean invert = false;
                if(forwardsMovement){
                    if(rightMovement){
                        yaw += 45.0f;
                    }else if(leftMovement){
                        yaw += 315.0f;
                    }
                }else if(backwardsMovement){
                    if(rightMovement){
                        yaw += 315.0f;
                    }else if(leftMovement){
                        yaw += 45.0f;
                    }
                    invert = true;
                }else if(rightMovement){
                    pitch = 0.0f;
                    yaw += 90.0f;
                }else if(leftMovement){
                    pitch = 0.0f;
                    yaw += 270.0f;
                }
                if(yaw > 180.0f){
                    yaw -= 360.0f;
                }
                Vec3d e = Vec3d.fromPolar(pitch, yaw).normalize();
                double length = Math.sqrt((x * x) + (z * z));
                if(invert){
                    length = - length;
                }
                x = e.getX() * length;
                y = e.getY() * length;
                z = e.getZ() * length;

                fallDistance = 0.0f;

                setSneaking(false);
                setSprinting(false);

                if(! (backwardsMovement || forwardsMovement || leftMovement || rightMovement)){
                    setVelocityClient(0.0, 0.0, 0.0);
                }

                float multiplier = speedEnabled ? 1.0f * LiteModFlyMod.config.getConfig().flySpeedMultiplier : 1.0f;
                x *= multiplier;
                y *= multiplier;
                z *= multiplier;
                super.move(type, new Vec3d(x, y, z));
            }else{
                super.move(type, new Vec3d(x, y, z));
            }
        }else if(LiteModFlyMod.flying == 0){
            LiteModFlyMod.flying = - 1;
            fallDistance = 0.0f;
        }else if (LiteModFlyMod.flying < 0) {
            if (speedEnabled) {
                x *= LiteModFlyMod.config.getConfig().runSpeedMultiplier;
                z *= LiteModFlyMod.config.getConfig().runSpeedMultiplier;
                setSprinting(false);
            }
            super.move(type, new Vec3d(x, y, z));
        }else{
            super.move(type, vec3d);
        }
    }
}