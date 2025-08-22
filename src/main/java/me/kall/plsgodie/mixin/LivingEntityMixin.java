package me.kall.plsgodie.mixin;

import me.kall.plsgodie.PlsGoDie;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract boolean isDeadOrDying();
    @Shadow public abstract boolean hurt(DamageSource source, float amount);
    @Shadow public abstract float getHealth();

    @Unique private boolean plsGoDie$isBlacklisted = false;
    @Unique private DamageSource plsGoDie$deathReason = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(EntityType<? extends LivingEntity> entityType, Level level, CallbackInfo ci) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        if (id != null && PlsGoDie.BLACKLIST.contains(id)) this.plsGoDie$isBlacklisted = true;
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.plsGoDie$isBlacklisted) return;
        if (this.isDeadOrDying()) this.plsGoDie$deathReason = source;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (this.plsGoDie$isBlacklisted) return;
        if (this.plsGoDie$deathReason != null && !this.isDeadOrDying()) {
            this.hurt(this.plsGoDie$deathReason, Float.MAX_VALUE);
            PlsGoDie.LOGGER.warn("Entity revived after death: {} (health: {}). Re-applying fatal damage (cause: {})", this.toString(), this.getHealth(), this.plsGoDie$deathReason.toString());
        } else if (this.isDeadOrDying()) {
            this.plsGoDie$deathReason = null;
        }
    }
}
