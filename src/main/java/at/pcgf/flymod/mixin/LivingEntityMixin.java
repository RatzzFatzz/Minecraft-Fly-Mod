package at.pcgf.flymod.mixin;

import at.pcgf.flymod.FlyModImpl;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class LivingEntityMixin extends LivingEntity {
    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void move(MovementType type, Vec3d vec3d) {
        if (FlyModImpl.flying > 0) {
            fallDistance = 0.0F;
        } else if (FlyModImpl.flying == 0) {
            fallDistance = 0.0F;
        }
        super.move(type, vec3d);
    }
}
