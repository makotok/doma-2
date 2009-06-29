package org.seasar.doma.internal.apt.entity;

import java.util.List;

import org.seasar.doma.Entity;
import org.seasar.doma.domain.IntegerDomain;


/**
 * @author taedium
 * 
 */
@Entity
public interface ElementOfReturnListNotDomainEntity {

    IntegerDomain id();

    List<String> list();
}
