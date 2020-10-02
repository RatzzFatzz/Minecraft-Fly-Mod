package at.pcgf.flymod.mixin;

import at.pcgf.flymod.DTO;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class LivingEntityMixin extends LivingEntity {
    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if(DTO.isIsInvulnerableToFallDamage()){
            System.out.println("outer: " + DTO.isIsInvulnerableToFallDamage());
            if(DamageSource.FALL == damageSource){
                System.out.println("inner: " + DTO.isIsInvulnerableToFallDamage());
                return true;
            }
        }
        return super.isInvulnerableTo(damageSource);
    }
}
