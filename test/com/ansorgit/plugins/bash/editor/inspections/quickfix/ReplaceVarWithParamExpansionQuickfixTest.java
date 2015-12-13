package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.editor.inspections.inspections.SimpleVarUsageInspection;
import com.intellij.codeInsight.daemon.quickFix.LightQuickFixParameterizedTestCase;
import org.jetbrains.annotations.NotNull;

public class ReplaceVarWithParamExpansionQuickfixTest extends LightQuickFixParameterizedTestCase {

    @Override
    protected void doSingleTest(String fileSuffix, String testDataPath) {
        enableInspectionTools(SimpleVarUsageInspection.class);

        super.doSingleTest(fileSuffix, testDataPath);
    }

    @Override
    protected boolean isRunInWriteAction() {
        return true;
    }

    @NotNull
    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath();
    }

    @Override
    protected String getBasePath() {
        return "/quickfixes/replaceVarWithParamExpansionQuickfix";
    }
}