package com.thumbleweed.authforge;

import com.thumbleweed.authforge.config.Config;
import com.thumbleweed.authforge.core.Guard;
import com.thumbleweed.authforge.core.GuardFactory;
import com.thumbleweed.authforge.event.Handler;
import com.thumbleweed.authforge.setup.CommandsSetup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AuthForge.ID)
public class AuthForge {
    public static final String ID = "authforge";
    public static final String NAME = "Auth Forge";
    public static final String VERSION = "1.19.2-0.0.3.0";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public AuthForge() {
        LOGGER.info("Initializing {} {}!", NAME, VERSION);
        LOGGER.info("Registering configuration...");

        @SuppressWarnings("removal")
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        Config.register(modLoadingContext);
        Config.loadConfig();

        MinecraftForge.EVENT_BUS.register(this);

        if (Config.isAuthEnabled()) {
            AuthForge.LOGGER.info("Auth enabled!");
            try {
                Guard guard = createGuard();
                Handler handler = new Handler();

                MinecraftForge.EVENT_BUS.register(handler);
                MinecraftForge.EVENT_BUS.register(new CommandsSetup(handler, guard));
            } catch (Exception e) {
                AuthForge.LOGGER.error("Whoops! An exception occurred: ", e);
            }
        } else {
            AuthForge.LOGGER.info("Auth disabled!");
        }
    }

    private static Guard createGuard() throws Exception {
        return GuardFactory.createFromConfig(Config.getFactoryConfig());
    }
}
