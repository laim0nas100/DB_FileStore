package com.github.laim0nas100.dbstore.idgen;

import com.github.laim0nas100.dbstore.JdbiMixin;
import com.github.laim0nas100.uncheckedutils.SafeOpt;

/**
 *
 * @author laim0nas100
 */
public interface IdGenerator extends JdbiMixin {

    public SafeOpt getAndIncrement();
    
    public SafeOpt getCurrent();

}
