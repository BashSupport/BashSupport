/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: FixShebangInspection.java, Class: FixShebangInspection
 * Last modified: 2010-04-19
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.editor.inspections.quickfix.ShebangQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.google.common.collect.Sets;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Set;

/**
 * This inspection offers quickfixes to replace an unknwown shebang line with well-known shebang commands.
 * Date: 15.05.2009
 * Time: 14:56:55
 *
 * @author Joachim Ansorg
 */
public class FixShebangInspection extends AbstractBashInspection {
    private String commands;
    private final FixShebangSettings settingsPanel = new FixShebangSettings();

    //fixme fix this for windows cygwin environments
    private static final String defaultCommands = "/bin/sh\n/bin/bash";

    public FixShebangInspection() {
    }

    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "FixShebang";
    }

    @NotNull
    public String getShortName() {
        return "Fix shebang";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Fix unusual shebang lines";
    }

    @Override
    public JComponent createOptionsPanel() {
        settingsPanel.getValidCommandsTextArea().setText(commands);
        return settingsPanel.getSettingsPanel();
    }

    @Override
    public void writeSettings(Element element) throws WriteExternalException {
        element.setText(settingsPanel.getValidCommandsTextArea().getText().replace('\n', '#'));
    }

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    private String configuredCommands() {
        String value = settingsPanel.getValidCommandsTextArea().getText();
        return value.trim().length() > 0 ? value : defaultCommands;
    }

    @Override
    public void readSettings(Element element) throws InvalidDataException {
        commands = element.getText().replace('#', '\n');
        if (commands.trim().length() == 0) { //isEmpty has been added in Java 6, so don't use it :)
            commands = defaultCommands;
        }

        settingsPanel.getValidCommandsTextArea().setText(commands);
    }


    @Override
    public String getStaticDescription() {
        return "This inspection can replace unknown shebang commands with one of the registered commands, like /bin/sh .";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        final Set<String> commands = Sets.newHashSet(configuredCommands().split("\\n"));
        commands.remove(""); //invalid command, may not be offered as replacement

        return new BashVisitor() {
            @Override
            public void visitShebang(BashShebang shebang) {

                if (isOnTheFly && !commands.contains(shebang.shellCommand()) && commands.size() > 0) {
                    commands.remove(shebang.shellCommand());//currently used command
                    for (String command : commands) {
                        holder.registerProblem(shebang, getShortName(), new ShebangQuickfix(shebang, command));
                    }
                }
            }
        };
    }
}
