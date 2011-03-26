package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashAssignmentList;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * User: jansorg
 * Date: 26.03.11
 * Time: 19:45
 */
public class BashAssignmentListImpl extends BashPsiElementImpl implements BashAssignmentList {
    public BashAssignmentListImpl(ASTNode node) {
        super(node, "assignment list");
    }
}
