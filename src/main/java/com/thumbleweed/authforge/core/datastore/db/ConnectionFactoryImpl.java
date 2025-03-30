package com.thumbleweed.authforge.core.datastore.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactoryImpl implements ConnectionFactory {
    private final String url;
    private final String user;
    private final String password;

    public ConnectionFactoryImpl(String url) {
        this.url = url;
        this.password = null;
        this.user = null;
    }

    public ConnectionFactoryImpl(String url, String driver) throws ClassNotFoundException {
        this.url = url;
        this.password = null;
        this.user = null;
        Class.forName(driver);
    }

    public ConnectionFactoryImpl(String dialect, String host, int port, String database, String user, String password, String driver) throws ClassNotFoundException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("jdbc:").append(dialect);

        if (!host.isEmpty()) {
            urlBuilder.append("://").append(host).append(":").append(port).append("/").append(database);
        } else {
            urlBuilder.append(":").append(database);
        }

        this.url = urlBuilder.toString();
        this.user = user;
        this.password = password;
        Class.forName(driver);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return user == null ? DriverManager.getConnection(this.url) : DriverManager.getConnection(this.url, this.user, this.password);
    }

    @Override
    public String getURL() {
        return this.url;
    }
}