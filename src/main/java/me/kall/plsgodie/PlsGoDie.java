package me.kall.plsgodie;

import com.google.common.collect.Lists;
import me.kall.jsonate.api.JsonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.stream.Collectors;

@Mod(PlsGoDie.MOD_ID)
public final class PlsGoDie {
    public static final String MOD_ID = "plsgodie";
    public static final Logger LOGGER = LogManager.getLogger(PlsGoDie.class);
    public static boolean requireReload = false;

    private static final JsonConfig CONFIG = JsonConfig.create(MOD_ID, "1.0.0")
            .put("Blacklist", Lists.newArrayList())
            .put("NotifyPlayersOnKilling", true)
            .put("ForceRemoveEntityIfItsStillAliveAfterWeApplyFatalDamageOnIt", false)
            .initialize();

    public static Set<ResourceLocation> blacklist = null;
    private static boolean notification;
    public static boolean force;

    static {
        init();
    }

    public static void init() {
        blacklist = CONFIG.getStream("Blacklist", String.class)
                .map(ResourceLocation::parse)
                .collect(Collectors.toSet());
        notification = CONFIG.getBoolean("NotifyPlayersOnKilling");
        force = CONFIG.getBoolean("ForceRemoveEntityIfItsStillAliveAfterWeApplyFatalDamageOnIt");
    }

    public static void note(boolean isForce, String entity, String health, String deathReason) {
        if (!notification) return;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        Component msg = isForce ? Component.translatable("note.plsgodie.info.force", entity) : Component.translatable("note.plsgodie.info", entity, health, deathReason);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.displayClientMessage(msg, false);
        }
    }
}
