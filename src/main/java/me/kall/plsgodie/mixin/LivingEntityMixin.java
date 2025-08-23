package me.kall.plsgodie.mixin;

import me.kall.plsgodie.PlsGoDie;
import me.kall.plsgodie.api.ILivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
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
    @Shadow public abstract void kill();

    @Unique private boolean plsGoDie$isBlacklisted = false;
    @Unique private DamageSource plsGoDie$deathReason = null;
    @Unique private boolean plsGoDie$tried = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(EntityType<? extends LivingEntity> entityType, Level level, CallbackInfo ci) {
        plsGoDie$checkBlacklisted(entityType);
    }

    @Override
    public void plsGoDie$checkBlacklisted(EntityType<?> entityType) {
        ResourceLocation id = ForgeRegistries.ENTITIES.getKey(entityType);
        if (id != null && PlsGoDie.blacklist.contains(id)) {
            this.plsGoDie$isBlacklisted = true;
        }
    }

    @Inject(method = "tickDeath", at = @At("HEAD"))
    private void onTickDeathStart(CallbackInfo ci) {
        if (this.plsGoDie$deathReason == null && this.lastDamageSource != null) {
            this.plsGoDie$deathReason = this.lastDamageSource;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (this.plsGoDie$isBlacklisted) return;
        if (this.plsGoDie$deathReason != null && !this.isDeadOrDying()) {
            this.hurt(this.plsGoDie$deathReason, 1000000F);

            String entity = this.toString();
            String health = String.valueOf(this.getHealth());
            String deathReason = this.plsGoDie$deathReason.toString();

            PlsGoDie.LOGGER.warn("Entity revived after death: {} (health: {}). Re-applying fatal damage (cause: {})", entity, health, deathReason);
            PlsGoDie.note(false, entity, health, deathReason);

            if (this.plsGoDie$tried && PlsGoDie.force) {
                this.kill();

                PlsGoDie.note(true, entity, health, deathReason);
                PlsGoDie.LOGGER.error("Entity {} is still alive after we applied fatal damage to it. Force-remove it now.", entity);
            }

            this.plsGoDie$tried = true;
        }
    }
}
