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
import at.pcgf.flymod.FlyingState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("unused")
@Mixin(PlayerEntity.class)
public abstract class LivingEntityMixin extends LivingEntity {
    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void move(MovementType type, Vec3d vec3d) {
        if (FlyModImpl.flyingState == FlyingState.FLYING) {
            fallDistance = 0.0F;
        }
        super.move(type, vec3d);
    }
}
