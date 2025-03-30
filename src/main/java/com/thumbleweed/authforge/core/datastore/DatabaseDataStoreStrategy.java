package com.thumbleweed.authforge.core.datastore;

import com.thumbleweed.authforge.core.datastore.db.ConnectionFactory;
import com.thumbleweed.authforge.core.exception.AuthForgeException;

import java.sql.*;
import java.util.EnumMap;
import java.util.Map;

public class DatabaseDataStoreStrategy implements DataStoreStrategy {
    public static final String DEFAULT_TABLE = "players";

    public enum Column {
        USERNAME,
        UUID,
        PASSWORD,
        BANNED,

    }

    private final String table;
    private final Map<Column, String> columns;
    private final PasswordHash passwordHash;
    private final ConnectionFactory connectionFactory;

    public DatabaseDataStoreStrategy(
            String table,
            ConnectionFactory connectionFactory,
            Map<Column, String> customColumns,
            PasswordHash passwordHash
    ) throws SQLException {
        this.table = table;
        this.connectionFactory = connectionFactory;

        this.columns = new EnumMap<>(Column.class);
        for (Column c : Column.values()) {
            this.columns.put(c, c.name().toLowerCase());
        }

        this.columns.putAll(customColumns);
        this.passwordHash = passwordHash;

        this.checkTable();
    }

    public DatabaseDataStoreStrategy(ConnectionFactory connectionFactory) throws SQLException {
        this(DEFAULT_TABLE, connectionFactory, new EnumMap<>(Column.class), new BcryptPasswordHash());
    }

    @Override
    public DataStorePlayer findByUsername(String username) throws AuthForgeException {
        try (
                Connection conn = this.connectionFactory.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        String.format(
                                "SELECT * FROM %s WHERE LOWER(%s) = LOWER(?)",
                                this.table,
                                this.columns.get(Column.USERNAME)
                        )
                )
        ) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            return this.createPlayer(rs);
        } catch (SQLException e) {
            throw new AuthForgeException(e.getMessage());
        }
    }

    @Override
    public boolean add(DataStorePlayer player) throws AuthForgeException {
        String query = String.format(
                "INSERT INTO %s(%s, %s, %s) VALUES(?, ?, ?)",
                this.table,
                this.columns.get(Column.PASSWORD),
                this.columns.get(Column.USERNAME),
                this.columns.get(Column.UUID)
        );
        try (
                Connection conn = this.connectionFactory.getConnection();
                PreparedStatement statement = conn.prepareStatement(query)
        ) {
            statement.setString(1, player.getPassword());
            statement.setString(2, player.getUsername());

            if (player.isPremium()) {
                statement.setString(3, player.getUUID());
            } else statement.setNull(3, Types.VARCHAR); {
                statement.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            throw new AuthForgeException(e.getMessage());
        }
    }

    @Override
    public boolean isExist(DataStorePlayer player) throws AuthForgeException {
        return this.findByUsername(player.getUsername()) != null;
    }

    @Override
    public boolean updatePassword(DataStorePlayer player) throws AuthForgeException {
        String query = String.format(
                "UPDATE %s SET %s = ? WHERE %s = ?;",
                this.table,
                this.columns.get(Column.PASSWORD),
                this.columns.get(Column.USERNAME)
        );
        try (
                Connection conn = this.connectionFactory.getConnection();
                PreparedStatement statement = conn.prepareStatement(query)
        ) {
            statement.setString(1, player.getPassword());
            statement.setString(2, player.getUsername());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new AuthForgeException(e.getMessage());
        }
    }

    @Override
    public boolean resetPlayer(DataStorePlayer player) throws AuthForgeException {
        String query = String.format(
                "DELETE FROM %s WHERE %s = ?",
                this.table,
                this.columns.get(Column.USERNAME)
        );
        try (
                Connection conn = this.connectionFactory.getConnection();
                PreparedStatement statement = conn.prepareStatement(query)
        ) {
            statement.setString(1, player.getUsername());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new AuthForgeException(e.getMessage());
        }
    }

    @Override
    public PasswordHash getPasswordHash() {
        return this.passwordHash;
    }

    private void checkTable() throws SQLException {
        try (
                Connection connection = this.connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        String.format(
                                "SELECT %s,%s,%s,%s FROM %s",
                                this.columns.get(Column.BANNED),
                                this.columns.get(Column.PASSWORD),
                                this.columns.get(Column.USERNAME),
                                this.columns.get(Column.UUID),
                                this.table
                        )
                )
        ) {
            statement.executeQuery();
        }
    }

    private DataStorePlayer createPlayer(ResultSet rs) throws SQLException {
        DataStorePlayer player = null;
        if (rs != null && rs.next()) {
            player = new DataStorePlayerImpl();
            player.setBanned(rs.getInt(this.columns.get(Column.BANNED)) != 0);
            player.setPassword(rs.getString(this.columns.get(Column.PASSWORD)));
            player.setUsername(rs.getString(this.columns.get(Column.USERNAME)));
            player.setUUID(rs.getString(this.columns.get(Column.UUID)));
        }
        if (rs != null) {
            rs.close();
        }
        return player;
    }
}
