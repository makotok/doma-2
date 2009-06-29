package org.seasar.doma.internal.jdbc.command;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.jdbc.JdbcLogger;
import org.seasar.doma.message.MessageCode;


/**
 * @author taedium
 * 
 */
public final class Jdbcs {

    public static Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new JdbcException(MessageCode.DOMA2015, e, e);
        }
    }

    public static PreparedStatement prepareStatement(Connection connection,
            String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new JdbcException(MessageCode.DOMA2016, e, e);
        }
    }

    public static PreparedStatement prepareStatement(Connection connection,
            String sql, boolean autoGeneratedKeysSupported) {
        if (!autoGeneratedKeysSupported) {
            return prepareStatement(connection, sql);
        }
        try {
            return connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            throw new JdbcException(MessageCode.DOMA2016, e, e);
        }
    }

    public static CallableStatement prepareCall(Connection connection,
            String sql) {
        try {
            return connection.prepareCall(sql);
        } catch (SQLException e) {
            throw new JdbcException(MessageCode.DOMA2025, e, e);
        }
    }

    public static void close(Connection connection, JdbcLogger logger) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger
                        .logConnectionClosingFailure(Jdbcs.class.getName(), "close", e);
            }
        }
    }

    public static void close(Statement statement, JdbcLogger logger) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger
                        .logStatementClosingFailure(Jdbcs.class.getName(), "close", e);
            }
        }
    }

    public static void close(ResultSet resultSet, JdbcLogger logger) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger
                        .logResultSetClosingFailure(Jdbcs.class.getName(), "close", e);
            }
        }
    }
}
