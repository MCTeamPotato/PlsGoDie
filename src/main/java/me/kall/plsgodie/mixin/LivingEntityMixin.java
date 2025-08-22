package me.kall.plsgodie.mixin;

import me.kall.plsgodie.PlsGoDie;
import me.kall.plsgodie.api.ILivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntity {
    @Shadow public abstract boolean isDeadOrDying();
    @Shadow public abstract float getHealth();
    @Shadow @Nullable private DamageSource lastDamageSource;
    @Shadow public abstract boolean hurt(@NotNull DamageSource source, float amount);
    @Shadow public abstract void remove(Entity.RemovalReason reason);

    @Unique private boolean plsGoDie$isBlacklisted = false;
    @Unique private DamageSource plsGoDie$deathReason = null;
    @Unique private int plsGoDie$tryCount = 0;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(EntityType<? extends LivingEntity> entityType, Level level, CallbackInfo ci) {
        plsGoDie$checkBlacklisted(entityType);
    }

    @Override
    public void plsGoDie$checkBlacklisted(EntityType<?> entityType) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        if (id != null && PlsGoDie.blacklist.contains(id)) {
            this.plsGoDie$isBlacklisted = true;
        }
    }

    @Inject(method = "tickDeath", at = @At("HEAD"))
    private void onTickDeathStart(CallbackInfo ci) {
        if (this.plsGoDie$deathReason == null && this.lastDamageSource != null) {
            this.plsGoDie$deathReason = new DamageSource(this.lastDamageSource.typeHolder(), this.lastDamageSource.getDirectEntity(), this.lastDamageSource.getEntity(), this.lastDamageSource.getSourcePosition());
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (this.plsGoDie$isBlacklisted) return;
        if (this.plsGoDie$deathReason != null && !this.isDeadOrDying()) {
            this.hurt(this.plsGoDie$deathReason, 1000000F);
            plsGoDie$tryCount++;
            PlsGoDie.LOGGER.warn("Entity revived after death: {} (health: {}). Re-applying fatal damage (cause: {})", this.toString(), this.getHealth(), this.plsGoDie$deathReason.toString());
            if (plsGoDie$tryCount == 3) {
                PlsGoDie.LOGGER.error("Entity {} is still alive after we applied fatal damage to it. Force-remove it now.", this.toString());
                this.remove(Entity.RemovalReason.KILLED);
            }
        }
    }
}
