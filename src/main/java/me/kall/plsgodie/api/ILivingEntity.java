package me.kall.plsgodie.api;

import net.minecraft.world.entity.EntityType;

public interface ILivingEntity {
    void plsGoDie$checkBlacklisted(EntityType<?> entityType);
}
