package com.thumbleweed.authforge.core.datastore.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {
    /**
     * Get a connection to the database.
     *
     * @return Connection
     * @throws SQLException unexpected exception
     */
    Connection getConnection() throws SQLException;

    String getURL();
}
