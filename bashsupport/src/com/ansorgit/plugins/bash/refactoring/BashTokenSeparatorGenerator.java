package com.ansorgit.plugins.bash.refactoring;

import com.intellij.lang.ASTNode;
import com.intellij.lang.TokenSeparatorGenerator;

/**
 * The token separator generator is called for PSI element replacements, e.g. during a rename refactoring.
 * We do not want to insert extra separators (e.g. whitespace) between tokens so we always return null.
 * <p/>
 * User: jansorg
 * Date: 10.12.10
 * Time: 18:01
 */
public class BashTokenSeparatorGenerator implements TokenSeparatorGenerator {
    public ASTNode generateWhitespaceBetweenTokens(ASTNode left, ASTNode right) {
        return null;
    }
}
