package org.seasar.doma.internal.jdbc.query;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.seasar.doma.domain.BigDecimalDomain;
import org.seasar.doma.domain.IntegerDomain;
import org.seasar.doma.domain.StringDomain;
import org.seasar.doma.internal.jdbc.mock.MockConfig;
import org.seasar.doma.internal.jdbc.query.BatchUpdateQuery;
import org.seasar.doma.internal.jdbc.query.SqlFileBatchUpdateQuery;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.internal.jdbc.sql.PreparedSqlParameter;
import org.seasar.doma.internal.jdbc.sql.SqlFiles;

import junit.framework.TestCase;
import example.entity.Emp;
import example.entity.Emp_;

/**
 * @author taedium
 * 
 */
public class SqlFileBatchUpdateQueryTest extends TestCase {

    private MockConfig runtimeConfig = new MockConfig();

    public void testCompile() throws Exception {
        Emp emp1 = new Emp_();
        emp1.id().set(10);
        emp1.name().set("aaa");
        emp1.version().set(100);

        Emp emp2 = new Emp_();
        emp2.id().set(20);
        emp2.name().set("bbb");
        emp2.version().set(200);

        SqlFileBatchUpdateQuery<Emp, Emp_> query = new SqlFileBatchUpdateQuery<Emp, Emp_>(
                Emp_.class);
        query.setConfig(runtimeConfig);
        query.setSqlFilePath(SqlFiles
                .buildPath(getClass().getName(), getName()));
        query.setParameterName("e");
        query.setEntities(Arrays.asList(emp1, emp2));
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        BatchUpdateQuery batchUpdateQuery = query;
        assertEquals(2, batchUpdateQuery.getSqls().size());
    }

    public void testOption_default() throws Exception {
        Emp emp1 = new Emp_();
        emp1.id().set(10);
        emp1.name().set("aaa");
        emp1.version().set(100);

        Emp emp2 = new Emp_();
        emp2.id().set(20);
        emp2.salary().set(new BigDecimal(2000));
        emp2.version().set(200);

        SqlFileBatchUpdateQuery<Emp, Emp_> query = new SqlFileBatchUpdateQuery<Emp, Emp_>(
                Emp_.class);
        query.setConfig(runtimeConfig);
        query.setSqlFilePath(SqlFiles
                .buildPath(getClass().getName(), getName()));
        query.setParameterName("e");
        query.setEntities(Arrays.asList(emp1, emp2));
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();

        PreparedSql sql = query.getSqls().get(0);
        assertEquals("update emp set name = ?, salary = ? where id = ?", sql
                .getRawSql());
        List<PreparedSqlParameter> parameters = sql.getParameters();
        assertEquals(3, parameters.size());
        assertEquals(new StringDomain("aaa"), parameters.get(0).getDomain());
        assertTrue(parameters.get(1).getDomain().isNull());
        assertEquals(new IntegerDomain(10), parameters.get(2).getDomain());

        sql = query.getSqls().get(1);
        assertEquals("update emp set name = ?, salary = ? where id = ?", sql
                .getRawSql());
        parameters = sql.getParameters();
        assertEquals(3, parameters.size());
        assertTrue(parameters.get(0).getDomain().isNull());
        assertEquals(new BigDecimalDomain(new BigDecimal(2000)), parameters
                .get(1).getDomain());
        assertEquals(new IntegerDomain(20), parameters.get(2).getDomain());
    }

    public void testIsExecutable() throws Exception {
        SqlFileBatchUpdateQuery<Emp, Emp_> query = new SqlFileBatchUpdateQuery<Emp, Emp_>(
                Emp_.class);
        query.setConfig(runtimeConfig);
        query.setSqlFilePath(SqlFiles
                .buildPath(getClass().getName(), getName()));
        query.setParameterName("e");
        query.setEntities(Collections.<Emp> emptyList());
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.compile();
        assertFalse(query.isExecutable());
    }
}
