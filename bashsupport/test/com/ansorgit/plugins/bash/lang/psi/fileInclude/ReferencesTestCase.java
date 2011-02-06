package com.ansorgit.plugins.bash.lang.psi.fileInclude;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.junit.Assert;

/**
 * User: jansorg
 * Date: 06.02.11
 * Time: 12:57
 */
public class ReferencesTestCase extends AbstractFileIncludeTest {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "references/";
    }

    public void testValidScope() throws Exception {
        PsiReference variableReference = configure();
        PsiFile included = addFile("included.bash");

        PsiElement varDef = variableReference.resolve();
        Assert.assertNotNull(varDef);
        Assert.assertTrue("var def must resolve to the definition in included.bash", included.equals(varDef.getContainingFile()));

        Assert.assertTrue("The definition and usage scope must be valid", BashVarUtils.isInDefinedScope(variableReference.getElement(), (BashVarDef) varDef));
    }

    public void testInvalidScope() throws Exception {
        PsiReference variableReference = configure();
        PsiFile included = addFile("included.bash");

        final BashVarDef[] includeVarDefs = {null};

        //find the variable definition in the include file
        BashPsiUtils.visitRecursively(included, new BashVisitor() {
            @Override
            public void visitVarDef(BashVarDef varDef) {
                if ("VAR".equals(varDef.getName())) {
                    includeVarDefs[0] = varDef;
                }
            }
        });

        BashVarDef includeVarDef = includeVarDefs[0];
        Assert.assertNotNull("The include file must contain a definition", includeVarDef);

        PsiElement varDef = variableReference.resolve();
        Assert.assertNull("The variable must not resolve", varDef);

        Assert.assertFalse("The definition must not be a valid scope for the use (different files and use before inclusion)",
                BashVarUtils.isInDefinedScope(variableReference.getElement(), includeVarDef));
    }
}
