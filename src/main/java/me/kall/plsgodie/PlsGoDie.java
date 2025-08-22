package me.kall.plsgodie;

import com.google.common.collect.Lists;
import me.kall.jsonate.api.JsonConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
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
            .initialize();

    public static Set<ResourceLocation> BLACKLIST = init();

    public static Set<ResourceLocation> init() {
        return CONFIG.getStream("Blacklist", String.class)
                .map(ResourceLocation::parse)
                .collect(Collectors.toSet());
    }
}
