/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.jdbc.builder;

import junit.framework.TestCase;

import org.seasar.doma.internal.jdbc.mock.MockConfig;

/**
 * @author taedium
 * 
 */
public class DeleteBuilderTest extends TestCase {

    public void test() throws Exception {
        DeleteBuilder builder = DeleteBuilder.newInstance(new MockConfig());
        builder.sql("delete from Emp");
        builder.sql("where");
        builder.sql("name = ").param(String.class, "aaa");
        builder.sql("and");
        builder.sql("salary = ").param(int.class, 10);
        builder.execute();
    }

    public void testGetSql() throws Exception {
        DeleteBuilder builder = DeleteBuilder.newInstance(new MockConfig());
        builder.sql("delete from Emp");
        builder.sql("where");
        builder.sql("name = ").param(String.class, "aaa");
        builder.sql("and");
        builder.sql("salary = ").param(int.class, 10);

        String sql = String.format("delete from Emp%n" + "where%n"
                + "name = ?%n" + "and%n" + "salary = ?");
        assertEquals(sql, builder.getSql().getRawSql());

        builder.execute();
    }

    public void testLiterall() throws Exception {
        DeleteBuilder builder = DeleteBuilder.newInstance(new MockConfig());
        builder.sql("delete from Emp");
        builder.sql("where");
        builder.sql("name = ").literal(String.class, "aaa");
        builder.sql("and");
        builder.sql("salary = ").literal(int.class, 10);

        String sql = String.format("delete from Emp%n" + "where%n"
                + "name = 'aaa'%n" + "and%n" + "salary = 10");
        assertEquals(sql, builder.getSql().getRawSql());

        builder.execute();
    }

}
