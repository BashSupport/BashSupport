package com.ansorgit.plugins.bash.lang.psi.api;

import com.intellij.psi.PsiComment;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jansorg
 * Date: 12.02.11
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
public interface DocumentationAwareElement {
    /**
     * Tries to find an attached function comment which explains what this function does.
     * A function comment has to be the previous token in the tree right before this function element.
     *
     * @return The comment psi element, if found. If unavailable null is returned.
     */
    List<PsiComment> findAttachedComment();
}
