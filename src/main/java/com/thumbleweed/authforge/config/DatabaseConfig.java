package com.thumbleweed.authforge.config;

import com.thumbleweed.authforge.core.datastore.DatabaseDataStoreStrategy;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.EnumMap;
import java.util.Map;

public class DatabaseConfig {
    public final ForgeConfigSpec.ConfigValue<String> host;
    public final ForgeConfigSpec.IntValue port;
    public final ForgeConfigSpec.ConfigValue<String> dialect;
    public final ForgeConfigSpec.ConfigValue<String> db;
    public final ForgeConfigSpec.ConfigValue<String> username;
    public final ForgeConfigSpec.ConfigValue<String> password;
    public final ForgeConfigSpec.ConfigValue<String> table;
    public final ForgeConfigSpec.ConfigValue<String> driver;
    public final Map<DatabaseDataStoreStrategy.Column, ForgeConfigSpec.ConfigValue<String>> columns = new EnumMap<>(DatabaseDataStoreStrategy.Column.class);

    public DatabaseConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Database configuration").push("database");

        this.host = builder.
                comment("Host of the database, default to localhost").
                define("host", "localhost");

        this.port = builder.
                comment("Port of the database, default to 3306").
                defineInRange("port", 3306, 1024, 65535);

        this.dialect = builder.
                comment("SQL dialect, default to 'mariadb'").
                define("dialect", "mariadb");

        this.db = builder.
                comment("Name of the database, default to 'auth-forge'").
                define("db", "auth-forge");

        this.username = builder.
                comment("Database user's name, default to 'user'").
                define("username", "user");

        this.password = builder.
                comment("Database user's password, default to 'password'").
                define("password", "password");

        this.table = builder.
                comment("Database table's name to store the data, default to 'players'").
                define("table", DatabaseDataStoreStrategy.DEFAULT_TABLE);

        this.driver = builder.
                comment("Database JDBC to use, default to 'org.mariadb.jdbc.Driver'").
                define("driver", "org.mariadb.jdbc.Driver");

        for (DatabaseDataStoreStrategy.Column c : DatabaseDataStoreStrategy.Column.values()) {
            String capitalized = c.name().substring(0, 1).toUpperCase() + c.name().substring(1).toLowerCase();
            this.columns.put(
                    c,
                    builder.
                            comment(String.format("Column name of '%s'", c.name().toLowerCase())).
                            define(String.format("column%s", capitalized), c.name().toLowerCase())
            );
        }

        builder.pop();
    }
}
