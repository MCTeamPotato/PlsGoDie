package me.kall.plsgodie.mixin;

import me.kall.plsgodie.PlsGoDie;
import me.kall.plsgodie.api.ILivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow public abstract Iterable<ServerLevel> getAllLevels();

    @Inject(method = "tickServer", at = @At("TAIL"))
    private void onReload(CallbackInfo ci) {
        if (PlsGoDie.requireReload) {
            PlsGoDie.requireReload = false;
            for (ServerLevel level : this.getAllLevels()) {
                for (Entity entity : level.getAllEntities()) {
                    if (entity instanceof ILivingEntity) {
                        ((ILivingEntity) entity).plsGoDie$checkBlacklisted(entity.getType());
                    }
                }
            }
        }
    }
}
