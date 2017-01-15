package ru.mail.park.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mchange.v2.c3p0.*;
import java.beans.PropertyVetoException;
import java.sql.*;

/**
 * Created by sergeigavrilko on 12.01.17.
 */
public class Database {

    private static ComboPooledDataSource dataSource;

    static {
        dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("com.mysql.jdbc.Driver");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        dataSource.setJdbcUrl(Constants.HOST);
        dataSource.setUser(Constants.USER);
        dataSource.setPassword(Constants.PASSWORD);
        dataSource.setAcquireRetryAttempts(0);
        dataSource.setAcquireIncrement(3);
        dataSource.setMaxPoolSize(20);
    }

    public static void select(String query, ResultCallback callback) throws SQLException, JsonProcessingException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeQuery(query);
                try (ResultSet result = statement.getResultSet()) {
                    callback.call(result);
                }
            }
        }
    }

    public static <T> T select(String query, TResultCallback<T> callback) throws SQLException, JsonProcessingException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeQuery(query);
                try (ResultSet result = statement.getResultSet()) {
                    return callback.call(result);
                }
            }
        }
    }

    public static void update(String query) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);
            }
        }
    }

}
