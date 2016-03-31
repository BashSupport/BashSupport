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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.editor.inspections.quickfix.RegisterShebangCommandQuickfix;
import com.ansorgit.plugins.bash.editor.inspections.quickfix.ReplaceShebangQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.PsiElementVisitor;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.util.List;

/**
 * This inspection offers quickfixes to replace an unknwown shebang line with well-known shebang commands.
 *
 * @author jansorg
 */
public class FixShebangInspection extends LocalInspectionTool {
    private static final List<String> DEFAULT_COMMANDS = Lists.newArrayList("/bin/bash", "/bin/sh");
    private static final List<String> VALID_ENV_SHELLS = Lists.newArrayList("bash", "sh");
    private static final String ELEMENT_NAME_SHEBANG = "shebang";

    private List<String> validShebangCommands = DEFAULT_COMMANDS;

    @Override
    public JComponent createOptionsPanel() {
        FixShebangSettings settings = new FixShebangSettings();
        JTextArea textArea = settings.getValidCommandsTextArea();
        textArea.setText(Joiner.on('\n').join(validShebangCommands));

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                updateShebangLines(documentEvent);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                updateShebangLines(documentEvent);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                updateShebangLines(documentEvent);
            }
        });

        return settings.getSettingsPanel();
    }

    private void updateShebangLines(DocumentEvent documentEvent) {
        validShebangCommands.clear();
        try {
            Document doc = documentEvent.getDocument();
            for (String item : doc.getText(0, doc.getLength()).split("\n")) {
                if (item.trim().length() != 0) {
                    validShebangCommands.add(item);
                }
            }
        } catch (BadLocationException e) {
            throw new RuntimeException("Could not save shebang inspection settings input", e);
        }
    }

    @Override
    public void readSettings(@NotNull Element node) throws InvalidDataException {
        validShebangCommands = Lists.newLinkedList();

        List<Element> shebangs = node.getChildren(ELEMENT_NAME_SHEBANG);
        for (Element shebang : shebangs) {
            validShebangCommands.add(shebang.getText());
        }

        if (validShebangCommands.isEmpty()) {
            validShebangCommands.addAll(DEFAULT_COMMANDS);
        }
    }

    @Override
    public void writeSettings(@NotNull Element node) throws WriteExternalException {
        for (String shebangCommand : validShebangCommands) {
            Element shebandElement = new Element(ELEMENT_NAME_SHEBANG);
            shebandElement.setText(shebangCommand.trim());
            node.addContent(shebandElement);
        }
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitShebang(BashShebang shebang) {
                String shellCommand = shebang.shellCommand(false);
                String shellCommandWithParams = shebang.shellCommand(true);

                if (validShebangCommands.contains(shellCommand) || validShebangCommands.contains(shellCommandWithParams)) {
                    return;
                }

                if ("/usr/bin/env".equals(shellCommand)) {
                    //check the special env shebang command

                    String paramString = shebang.shellCommandParams();
                    String[] params = StringUtils.split(paramString, ' ');
                    boolean noShellParam = params == null || params.length == 0;
                    boolean invalidShell = params != null && params.length > 0 && !VALID_ENV_SHELLS.contains(params[0]);

                    if (noShellParam || invalidShell) {
                        List<LocalQuickFix> quickFixes = Lists.newLinkedList();

                        if (invalidShell) {
                            quickFixes.add(new RegisterShebangCommandQuickfix(FixShebangInspection.this, shebang));
                        }

                        for (String validCommand : VALID_ENV_SHELLS) {
                            quickFixes.add(new ReplaceShebangQuickfix(shebang, "/usr/bin/env " + validCommand, shebang.commandAndParamsRange()));
                        }

                        String message = noShellParam ? "'/usr/bin/env' needs a shell parameter" : "Unknown shell for /usr/bin/env";
                        holder.registerProblem(shebang, message,
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                shebang.commandAndParamsRange(),
                                quickFixes.toArray(new LocalQuickFix[quickFixes.size()]));
                    }
                } else {
                    List<LocalQuickFix> quickFixes = Lists.newLinkedList();

                    if (isOnTheFly) {
                        quickFixes.add(new RegisterShebangCommandQuickfix(FixShebangInspection.this, shebang));
                    }

                    for (String validCommand : validShebangCommands) {
                        if (!validCommand.equals(shellCommand) && !validCommand.equals(shellCommandWithParams)) {
                            quickFixes.add(new ReplaceShebangQuickfix(shebang, validCommand));
                        }
                    }

                    holder.registerProblem(shebang, "Unknown shebang command", ProblemHighlightType.GENERIC_ERROR_OR_WARNING, shebang.commandRange(), quickFixes.toArray(new LocalQuickFix[quickFixes.size()]));
                }
            }
        };
    }

    public void registerShebangCommand(String command) {
        validShebangCommands.add(command);
    }
}
