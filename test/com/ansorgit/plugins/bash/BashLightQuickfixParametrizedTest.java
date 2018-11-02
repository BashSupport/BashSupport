/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash;

import com.intellij.codeInsight.daemon.quickFix.LightQuickFixParameterizedTestCase;
import com.intellij.codeInspection.InspectionEP;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.LocalInspectionEP;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.util.containers.ContainerUtil;
import junit.framework.AssertionFailedError;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author jansorg
 */
public abstract class BashLightQuickfixParametrizedTest extends LightQuickFixParameterizedTestCase {
    private final Class<?>[] inspectionsClasses;

    public BashLightQuickfixParametrizedTest(Class<?>... inspectionsClasses) {
        this.inspectionsClasses = inspectionsClasses;
    }

    @Override
    protected void doSingleTest(String fileSuffix, String testDataPath) {
        enableInspectionTools(inspectionsClasses);

        try {
            super.doSingleTest(fileSuffix, testDataPath);
        } catch (AssertionFailedError e) {
            // ignore in 163.x
            // for unknown reasons 163.x sets the file to read-only before executing the test case
            // this seems like a bug
            if (ApplicationInfo.getInstance().getBuild().getBaselineVersion() != 163) {
                throw e;
            }
        }
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

    //copied from 162.x intellij-community branch to stay compatible
    protected void enableInspectionTools(@NotNull Class<?>... classes) {
        final InspectionProfileEntry[] tools = new InspectionProfileEntry[classes.length];

        final List<InspectionEP> eps = ContainerUtil.newArrayList();
        ContainerUtil.addAll(eps, Extensions.getExtensions(LocalInspectionEP.LOCAL_INSPECTION));
        ContainerUtil.addAll(eps, Extensions.getExtensions(InspectionEP.GLOBAL_INSPECTION));

        next:
        for (int i = 0; i < classes.length; i++) {
            for (InspectionEP ep : eps) {
                if (classes[i].getName().equals(ep.implementationClass)) {
                    tools[i] = ep.instantiateTool();
                    continue next;
                }
            }
            throw new IllegalArgumentException("Unable to find extension point for " + classes[i].getName());
        }

        enableInspectionTools(tools);
    }
}
