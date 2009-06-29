package org.seasar.doma.internal.jdbc.id;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.seasar.doma.GenerationType;
import org.seasar.doma.domain.LongDomain;
import org.seasar.doma.domain.StringDomain;
import org.seasar.doma.internal.jdbc.command.Jdbcs;
import org.seasar.doma.internal.jdbc.sql.BindParameter;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.jdbc.JdbcLogger;
import org.seasar.doma.message.MessageCode;


/**
 * @author taedium
 * 
 */
public class TableIdGenerator extends AbstractPreAllocateIdGenerator {

    protected final String qualifiedTableName;

    protected final String pkColumnName;

    protected final String pkColumnValue;

    protected final String valueColumnName;

    protected final PreparedSql updateSql;

    protected final PreparedSql selectSql;

    public TableIdGenerator(String qualifiedTableName, String pkColumnName,
            String valueColumnName, String pkColumnValue, long initialValue,
            long allocationSize) {
        super(initialValue, allocationSize);
        this.qualifiedTableName = qualifiedTableName;
        this.pkColumnName = pkColumnName;
        this.valueColumnName = valueColumnName;
        this.pkColumnValue = pkColumnValue;
        LongDomain allocationSizeDomain = new LongDomain(allocationSize);
        StringDomain pkColumnValueDomain = new StringDomain(pkColumnValue);
        updateSql = new PreparedSql(createUpdateRawSql(),
                createUpdateFormattedSql(), Arrays.asList(new BindParameter(
                        allocationSizeDomain), new BindParameter(
                        pkColumnValueDomain)));
        selectSql = new PreparedSql(createSelectRawSql(),
                createSelectFormattedSql(), Arrays.asList(new BindParameter(
                        pkColumnValueDomain)));
    }

    protected String createUpdateRawSql() {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("update ");
        buf.append(qualifiedTableName);
        buf.append(" set ");
        buf.append(valueColumnName);
        buf.append(" = ");
        buf.append(valueColumnName);
        buf.append(" + ? where ");
        buf.append(pkColumnName);
        buf.append(" = ?");
        return buf.toString();
    }

    protected String createUpdateFormattedSql() {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("update ");
        buf.append(qualifiedTableName);
        buf.append(" set ");
        buf.append(valueColumnName);
        buf.append(" = ");
        buf.append(valueColumnName);
        buf.append(" + ");
        buf.append(allocationSize);
        buf.append(" where ");
        buf.append(pkColumnName);
        buf.append(" = '");
        buf.append(pkColumnValue);
        buf.append("'");
        return buf.toString();
    }

    protected String createSelectRawSql() {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("select ");
        buf.append(valueColumnName);
        buf.append(" from ");
        buf.append(qualifiedTableName);
        buf.append(" where ");
        buf.append(pkColumnName);
        buf.append(" = ?");
        return new String(buf);
    }

    protected String createSelectFormattedSql() {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("select ");
        buf.append(valueColumnName);
        buf.append(" from ");
        buf.append(qualifiedTableName);
        buf.append(" where ");
        buf.append(pkColumnName);
        buf.append(" = '");
        buf.append(pkColumnValue);
        buf.append("'");
        return new String(buf);
    }

    @Override
    protected long getNewInitialValue(IdGenerationConfig config) {
        updateId(config, updateSql);
        long value = selectId(config, selectSql);
        return value - allocationSize;
    }

    protected void updateId(IdGenerationConfig config, PreparedSql sql) {
        JdbcLogger logger = config.getJdbcLogger();
        Connection connection = Jdbcs.getConnection(config.getDataSource());
        try {
            PreparedStatement preparedStatement = Jdbcs
                    .prepareStatement(connection, sql.getRawSql());
            try {
                logger.logSql(getClass().getName(), "updateId", sql);
                setupOptions(config, preparedStatement);
                preparedStatement.setLong(1, allocationSize);
                preparedStatement.setString(2, pkColumnValue);
                int rows = preparedStatement.executeUpdate();
                if (rows != 1) {
                    throw new JdbcException(MessageCode.DOMA2017, config
                            .getEntity().__getName());
                }
            } catch (SQLException e) {
                throw new JdbcException(MessageCode.DOMA2018, e, config
                        .getEntity().__getName(), e);
            } finally {
                Jdbcs.close(preparedStatement, logger);
            }
        } finally {
            Jdbcs.close(connection, logger);
        }
    }

    protected long selectId(IdGenerationConfig config, PreparedSql sql) {
        JdbcLogger logger = config.getJdbcLogger();
        Connection connection = Jdbcs.getConnection(config.getDataSource());
        try {
            PreparedStatement preparedStatement = Jdbcs
                    .prepareStatement(connection, sql.getRawSql());
            try {
                logger.logSql(getClass().getName(), "selectId", sql);
                setupOptions(config, preparedStatement);
                preparedStatement.setString(1, pkColumnValue);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Object result = resultSet.getObject(1);
                    if (result != null && Number.class.isInstance(result)) {
                        return Number.class.cast(result).longValue();
                    }
                }
                throw new JdbcException(MessageCode.DOMA2017, config
                        .getEntity().__getName());
            } catch (SQLException e) {
                throw new JdbcException(MessageCode.DOMA2018, e, config
                        .getEntity().__getName(), e);
            } finally {
                Jdbcs.close(preparedStatement, logger);
            }
        } finally {
            Jdbcs.close(connection, logger);
        }
    }

    @Override
    public GenerationType getGenerationType() {
        return GenerationType.TABLE;
    }

}
