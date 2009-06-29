package org.seasar.doma.internal.jdbc.sql.node;

import org.seasar.doma.jdbc.SqlNodeVisitor;

/**
 * @author taedium
 * 
 */
public interface WordNodeVisitor<R, P> extends SqlNodeVisitor<R, P> {

    R visitWordNode(WordNode node, P p);
}
