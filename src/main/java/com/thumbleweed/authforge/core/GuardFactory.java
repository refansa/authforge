package com.thumbleweed.authforge.core;

import com.thumbleweed.authforge.core.datastore.BcryptPasswordHash;
import com.thumbleweed.authforge.core.datastore.DataStoreStrategy;
import com.thumbleweed.authforge.core.datastore.DatabaseDataStoreStrategy;
import com.thumbleweed.authforge.core.datastore.FileDataStoreStrategy;
import com.thumbleweed.authforge.core.datastore.db.ConnectionFactory;
import com.thumbleweed.authforge.core.datastore.db.ConnectionFactoryImpl;

import java.nio.file.Paths;
import java.sql.SQLException;

public class GuardFactory {

    private GuardFactory() {
    }

    public static Guard createFromConfig(FactoryConfig config) throws ClassNotFoundException, SQLException {
        @SuppressWarnings("SwitchStatementWithTooFewBranches")
        DataStoreStrategy dataStore = switch (config.getStrategy()) {
            case DATABASE -> {
                ConnectionFactory connectionFactory = new ConnectionFactoryImpl(
                        config.getDialect(),
                        config.getHost(),
                        config.getPort(),
                        config.getDatabase(),
                        config.getUser(),
                        config.getPassword(),
                        config.getDriver()
                );
                yield new DatabaseDataStoreStrategy(config.getTable(), connectionFactory, config.getColumns(), new BcryptPasswordHash());
            }
            default ->
                    new FileDataStoreStrategy(Paths.get(config.getConfigDirectory().toString(), "authforge.sqlite").toFile());
        };
        return new DataStoreGuard(dataStore);
    }
}