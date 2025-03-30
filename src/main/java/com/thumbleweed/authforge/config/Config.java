package com.thumbleweed.authforge.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.thumbleweed.authforge.AuthForge;
import com.thumbleweed.authforge.core.FactoryConfig;
import com.thumbleweed.authforge.core.datastore.DataStoreStrategy;
import com.thumbleweed.authforge.core.datastore.DatabaseDataStoreStrategy;
import com.thumbleweed.authforge.core.i18n.LanguageMap;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = AuthForge.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static final ForgeConfigSpec SPEC;

    // Configuration Properties
    public static final ForgeConfigSpec.BooleanValue enableLogin;
    public static final ForgeConfigSpec.BooleanValue enableRegister;
    public static final ForgeConfigSpec.BooleanValue enableChangePassword;
    public static final ForgeConfigSpec.IntValue delay;
    public static final ForgeConfigSpec.EnumValue<LanguageMap.Language> language;
    public static final ForgeConfigSpec.EnumValue<DataStoreStrategy.Strategy> dataStore;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistedCommands;

    public static final DatabaseConfig databaseConfig;
    public static final I18nConfig i18nConfig;

    static {
        BUILDER.comment("Server configuration").push("server");

        enableLogin = BUILDER.
                comment("Enable or disable the /login command. If disabled, the server will be opened to everyone. Default to true").
                define("enableLogin", true);

        enableRegister = BUILDER.
                comment("Enable or disable the /register command. Default to true").
                define("enableRegister", true);

        enableChangePassword = BUILDER.
                comment("Enable or disable the /changepassword command. Default to true")
                .define("enableChangePassword", true);

        delay = BUILDER.
                comment("delay in seconds a player can authenticate before being automatically kicked from the server. Default to 60").
                defineInRange("delay", 60, 1, 600);

        language = BUILDER.
                comment("language locale to be used. Default to 'en_us'")
                .defineEnum("language", LanguageMap.Language.EN_US);

        dataStore = BUILDER.
                comment("data store strategy to store player's data, choose between 'database' or 'file'. Default to 'file'").
                defineEnum("dataStore", DataStoreStrategy.Strategy.FILE);

        List<String> defaultWhitelist = Arrays.asList("register", "logged", "login", "help");
        whitelistedCommands = BUILDER.
                comment("whitelisted commands (can be used without being logged in)").
                defineList("whitelistedCommands", defaultWhitelist, x -> true);

        BUILDER.pop();

        databaseConfig = new DatabaseConfig(BUILDER);
        i18nConfig = new I18nConfig(BUILDER);

        SPEC = BUILDER.build();
    }

    /**
     * Check whether Auth Forge functionality is partially enabled.
     *
     * @return boolean
     */
    public static boolean isAuthEnabled() {
        return enableLogin.get() || enableRegister.get();
    }

    public static void register(final ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, SPEC);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        setup();
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        setup();
    }

    private static void setup() {
        LanguageMap.loadTranslations(language.get().name());
        LanguageMap.replaceTranslations(i18nConfig.getTranslations());
    }

    public static void loadConfig() {
        loadConfig(getConfigurationFile());
    }

    public static void loadConfig(Path path) {
        if (path != null) {
            try {
                final CommentedFileConfig file = CommentedFileConfig
                        .builder(path.toFile())
                        .sync()
                        .autosave()
                        .writingMode(WritingMode.REPLACE)
                        .build();
                file.load();
                SPEC.setConfig(file);
            } catch (Exception e) {
                AuthForge.LOGGER.catching(e);
            }
        }
    }

    public static Path getConfigurationFile() {
        return FMLPaths.CONFIGDIR.get().resolve("../world/serverconfig/authforge-server.toml").normalize();
    }

    public static FactoryConfig getFactoryConfig() {
        Map<DatabaseDataStoreStrategy.Column, String> columns = new EnumMap<>(DatabaseDataStoreStrategy.Column.class);
        for (DatabaseDataStoreStrategy.Column c : DatabaseDataStoreStrategy.Column.values()) {
            columns.put(c, Config.databaseConfig.columns.get(c).get());
        }
        return new FactoryConfig()
                .setConfigDirectory(getConfigurationFile().resolve("..").normalize())
                .setStrategy(Config.dataStore.get())
                .setDialect(databaseConfig.dialect.get())
                .setDatabase(databaseConfig.db.get())
                .setTable(databaseConfig.table.get())
                .setHost(databaseConfig.host.get())
                .setPort(databaseConfig.port.get())
                .setUser(databaseConfig.username.get())
                .setDriver(databaseConfig.driver.get())
                .setPassword(databaseConfig.password.get())
                .setColumns(columns);
    }
}
