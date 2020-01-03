package at.pcgf.flymod.mixin;

import at.pcgf.flymod.FlyModImpl;
import at.pcgf.flymod.gui.FlyModConfigManager;
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
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void move(MovementType type, Vec3d vec3d) {
        double x = vec3d.getX();
        double y = vec3d.getY();
        double z = vec3d.getZ();
        boolean speedEnabled = MinecraftClient.getInstance().options.keySprint.isPressed();
        if(FlyModImpl.flying > 0){
            boolean backwardsMovement = MinecraftClient.getInstance().options.keyBack.isPressed();
            boolean forwardsMovement = MinecraftClient.getInstance().options.keyForward.isPressed();
            boolean leftMovement = MinecraftClient.getInstance().options.keyLeft.isPressed();
            boolean rightMovement = MinecraftClient.getInstance().options.keyRight.isPressed();
            y = 0.0;
            double flyUpDownBlocks = FlyModConfigManager.getConfig().flyUpDownBlocks;
            if(MinecraftClient.getInstance().options.keySneak.isPressed()){
                y -= flyUpDownBlocks;
            }else if(MinecraftClient.getInstance().options.keyJump.isPressed()){
                y += flyUpDownBlocks;
            }

            setSneaking(false);
            setSprinting(false);

            if(FlyModConfigManager.getConfig().mouseControl){
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
                y += e.getY() * length;
                z = e.getZ() * length;

                fallDistance = 0.0f;


                if(! (backwardsMovement || forwardsMovement || leftMovement || rightMovement)){
                    setVelocityClient(0.0, 0.0, 0.0);
                }

                Vec3d vec = applyFlyMultiplier(x, y, z);
                super.move(type, vec);
            }else{
                Vec3d vec = applyFlyMultiplier(x, y, z);
                super.move(type, vec);
            }
        }else if(FlyModImpl.flying == 0){
            FlyModImpl.flying = - 1;
            fallDistance = 0.0f;
        }else if(FlyModImpl.flying < 0){
            if(speedEnabled){
                x *= FlyModConfigManager.getConfig().runSpeedMultiplier;
                z *= FlyModConfigManager.getConfig().runSpeedMultiplier;
                setSprinting(false);
            }
            super.move(type, new Vec3d(x, y, z));
        }else{
            super.move(type, vec3d);
        }
    }

    public void moves(MovementType type, Vec3d vec3d) {
        double x = vec3d.getX();
        double y = vec3d.getY();
        double z = vec3d.getZ();
        if(FlyModImpl.flying > 0){
            y = calcFlyUpDown(y);
            setSneaking(false);
            setSprinting(false);
            if(FlyModConfigManager.getConfig().mouseControl){
                Vec3d vec = calcMouseMovement(x, y, z);
                fallDistance = 0.0f;
                if(! (MinecraftClient.getInstance().options.keyBack.isPressed() &&
                        MinecraftClient.getInstance().options.keyForward.isPressed() &&
                        MinecraftClient.getInstance().options.keyLeft.isPressed() &&
                        MinecraftClient.getInstance().options.keyRight.isPressed())){
                    setVelocity(0.0, 0.0, 0.0);
                }
                super.move(type, applyFlyMultiplier(vec.getX(), vec.getY(), vec.getZ()));
            }else{
                super.move(type, new Vec3d(x, y, z));
            }
        }else if(FlyModImpl.flying == 0){
            FlyModImpl.flying = - 1;
            fallDistance = 0.0f;
        }else if(FlyModImpl.flying < 0){
            if(MinecraftClient.getInstance().options.keySprint.isPressed()){
                x *= FlyModConfigManager.getConfig().runSpeedMultiplier;
                z *= FlyModConfigManager.getConfig().runSpeedMultiplier;
                setSprinting(false);
            }
            super.move(type, new Vec3d(x, y, z));
        }else{
            super.move(type, vec3d);
        }
    }

    private Vec3d calcMouseMovement(double x, double y, double z) {
        float pitch = prevPitch;
        float yaw = prevYaw;
        boolean invert = false;
        if(MinecraftClient.getInstance().options.keyForward.isPressed()){
            if(MinecraftClient.getInstance().options.keyRight.isPressed()){
                yaw += 45.0f;
            }else if(MinecraftClient.getInstance().options.keyLeft.isPressed()){
                yaw += 315.0f;
            }
        }else if(MinecraftClient.getInstance().options.keyBack.isPressed()){
            if(MinecraftClient.getInstance().options.keyRight.isPressed()){
                yaw += 315.0f;
            }else if(MinecraftClient.getInstance().options.keyLeft.isPressed()){
                yaw += 45.0f;
            }
        }else if(MinecraftClient.getInstance().options.keyRight.isPressed()){
            pitch = 0.0f;
            yaw += 90.0f;
        }else if(MinecraftClient.getInstance().options.keyLeft.isPressed()){
            pitch = 0.0f;
            yaw += 270.0f;
        }
        if(yaw > 180.0f){
            yaw -= 360.0f;
        }
        Vec3d vec = Vec3d.fromPolar(pitch, yaw).normalize();
        fallDistance = 0.0f;

        double lengthMultiplier = calcInvert(invert, x, z);
        return new Vec3d(vec.getX() * lengthMultiplier,
                (vec.getY() * lengthMultiplier) + y,
                vec.getZ() * lengthMultiplier);
    }

    private double calcInvert(boolean invert, double x, double z) {
        double length = Math.sqrt((x * x) + (z * z));
        if(invert){
            length = - length;
        }
        return length;
    }

    private double calcFlyUpDown(double y) {
        double flyUpDownBlocks = FlyModConfigManager.getConfig().flyUpDownBlocks;
        if(MinecraftClient.getInstance().options.keySneak.isPressed()){
            y -= flyUpDownBlocks;
        }else if(MinecraftClient.getInstance().options.keyJump.isPressed()){
            y += flyUpDownBlocks;
        }
        return y;
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
}