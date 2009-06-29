package org.seasar.doma.internal.jdbc.sql.node;

import org.seasar.doma.jdbc.SqlNodeVisitor;

/**
 * @author taedium
 * 
 */
public interface SelectStatementNodeVisitor<R, P> extends SqlNodeVisitor<R, P> {

    R visitSelectStatementNode(SelectStatementNode node, P p);
}
