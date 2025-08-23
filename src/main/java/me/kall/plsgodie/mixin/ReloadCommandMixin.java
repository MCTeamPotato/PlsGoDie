package me.kall.plsgodie.mixin;

import me.kall.plsgodie.PlsGoDie;
import net.minecraft.server.commands.ReloadCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadCommand.class)
public abstract class ReloadCommandMixin {
    @Inject(method = "reloadPacks", at = @At("TAIL"))
    private static void reload(CallbackInfo ci) {
        PlsGoDie.init();
        PlsGoDie.requireReload = true;
    }
}
