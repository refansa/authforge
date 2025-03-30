package com.thumbleweed.authforge.core.datastore;

import com.thumbleweed.authforge.AuthForge;
import com.thumbleweed.authforge.core.datastore.db.ConnectionFactory;
import com.thumbleweed.authforge.core.datastore.db.ConnectionFactoryImpl;
import com.thumbleweed.authforge.core.exception.AuthForgeException;
import org.sqlite.JDBC;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.Map;

public class FileDataStoreStrategy implements DataStoreStrategy {
    public static final String DEFAULT_DRIVER = "org.sqlite.JDBC";

    private final PasswordHash passwordHash;
    private final ConnectionFactory connectionFactory;
    private final DatabaseDataStoreStrategy strategy;

    public FileDataStoreStrategy(File file, PasswordHash passwordHash) throws SQLException, ClassNotFoundException {
        this.passwordHash = passwordHash;

        String url = String.format("jdbc:sqlite:%s", file.getAbsolutePath());

        if (JDBC.isValidURL(url)) {
            AuthForge.LOGGER.info("Using JDBC URL: {}", url);
        }

        this.connectionFactory = new ConnectionFactoryImpl(url, DEFAULT_DRIVER);

        this.createTable();

        this.strategy = new DatabaseDataStoreStrategy(this.connectionFactory);

    }

    public FileDataStoreStrategy(File file) throws SQLException, ClassNotFoundException {
        this(file, new BcryptPasswordHash());
    }

    @Override
    public DataStorePlayer findByUsername(String username) throws AuthForgeException {
        return this.strategy.findByUsername(username);
    }

    @Override
    public boolean add(DataStorePlayer player) throws AuthForgeException {
        return this.strategy.add(player);
    }

    @Override
    public boolean isExist(DataStorePlayer player) throws AuthForgeException {
        return this.findByUsername(player.getUsername()) != null;
    }

    @Override
    public boolean updatePassword(DataStorePlayer player) throws AuthForgeException {
        return this.strategy.updatePassword(player);
    }

    @Override
    public boolean resetPlayer(DataStorePlayer player) throws AuthForgeException {
        return this.strategy.resetPlayer(player);
    }

    @Override
    public PasswordHash getPasswordHash() {
        return this.passwordHash;
    }

    private void createTable() throws SQLException {
        try (
                Connection connection = this.connectionFactory.getConnection();
                Statement statement = connection.createStatement()
        ) {
            Map<DatabaseDataStoreStrategy.Column, String> columns = new EnumMap<>(DatabaseDataStoreStrategy.Column.class);
            for (DatabaseDataStoreStrategy.Column c : DatabaseDataStoreStrategy.Column.values()) {
                switch (c) {
                    case USERNAME:
                        columns.put(c, String.format("%s varchar(255) NOT NULL", c.name().toLowerCase()));
                        break;
                    case PASSWORD:
                    case UUID:
                        columns.put(c, String.format("%s varchar(255)", c.name().toLowerCase()));
                        break;
                    case BANNED:
                        columns.put(c, String.format("%s BOOLEAN DEFAULT 0", c.name().toLowerCase()));
                }
            }
            statement.executeUpdate(
                    String.format(
                            "CREATE TABLE IF NOT EXISTS %s (%s," +
                                    "id integer PRIMARY KEY," +
                                    "UNIQUE (uuid)," +
                                    "UNIQUE (username)" +
                                    ");",
                            DatabaseDataStoreStrategy.DEFAULT_TABLE,
                            String.join(", ", columns.values())
                    )
            );
        }
    }

}