/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ReplPanel.java, Class: ReplPanel
 * Last modified: 2010-04-24
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
package com.ansorgit.plugins.bash.repl;

import com.ansorgit.plugins.bash.actions.repl.AddReplAction;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The panel which contains the console view.
 * This class is based on code of the Clojure plugin.
 *
 * @author Kurt Christensen, ilyas
 */
public class ReplPanel extends JPanel implements Disposable {
    public static final String REPL_TOOLWINDOW_PLACE = "REPL.ToolWindow";
    public static final String REPL_TOOLWINDOW_POPUP_PLACE = "REPL.ToolWindow.Popup";

    private static final String BASH_REPL_ACTION_GROUP = "Bash.REPL.PanelGroup";

    private Project myProject;
    private Repl myRepl;
    private DefaultActionGroup actions;

    public ReplPanel(@NotNull final Project project, @NotNull final Module module) throws IOException, ConfigurationException {
        setLayout(new BorderLayout());

        myProject = project;
        myRepl = new Repl(module, AnAction.EMPTY_ARRAY);

        this.actions = new DefaultActionGroup(ActionManager.getInstance().getAction(AddReplAction.class.getName()));
        //actions.addAll(myRepl.view.createConsoleActions());
        //final AnAction stopAction = ActionManager.getInstance().getAction(IdeActions.ACTION_STOP_PROGRAM);
        //actions.add(stopAction);

        //actions.add(new CloseAction(myExecutor, contentDescriptor, myProject));

        //final JPanel toolbarPanel = new JPanel(new GridLayout());
        //toolbarPanel.add(ActionManager.getInstance().createActionToolbar(REPL_TOOLWINDOW_PLACE, actions, false).getComponent());
        //add(toolbarPanel, BorderLayout.WEST);
        final JComponent actionToolbar =
                ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actions, false).getComponent();
        //ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actions, true).getComponent();

        add(actionToolbar, BorderLayout.WEST);
        add(myRepl.getView().getComponent(), BorderLayout.CENTER);

        Disposer.register(this, myRepl);
    }

    /*private ActionGroup getActions() {
        return (ActionGroup) ActionManager.getInstance().getAction(BASH_REPL_ACTION_GROUP);
    } */

    /*public String writeToCurrentRepl(String s) {
        return writeToCurrentRepl(s, true);
    } */

    /*public String writeToCurrentRepl(String input, boolean requestFocus) {
        if (myRepl != null) {
            final PipedWriter pipeOut;
            PipedReader pipeIn = null;
            try {
                if (requestFocus) {
                    requestFocus();
                }

                pipeOut = new PipedWriter();
                pipeIn = new PipedReader(pipeOut);
                BufferedReader in = new BufferedReader(pipeIn);

                ProcessListener processListener = new ProcessAdapter() {
                    @Override
                    public void onTextAvailable(ProcessEvent event, Key outputType) {
                        try {
                            pipeOut.write(event.getText());
                            pipeOut.flush();
                            pipeOut.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                myRepl.processHandler.addProcessListener(processListener);

                final ConsoleView consoleView = myRepl.view;
                if (consoleView instanceof ConsoleViewImpl) {
                    final ConsoleViewImpl cView = (ConsoleViewImpl) consoleView;
                    final List<String> oldHistory = cView.getHistory();
                    final ArrayList<String> newHistory = new ArrayList<String>(oldHistory.size() + 1);
                    newHistory.addAll(oldHistory);
                    newHistory.add(input);
                    cView.importHistory(newHistory);
                }

                consoleView.print(input + "\r\n", ConsoleViewContentType.USER_INPUT);

                StringBuffer buf = new StringBuffer();
                //if (pipeIn.ready()) {
                String str;
                while ((str = in.readLine()) != null) {
                    buf.append(str);
                }
                //}
                myRepl.processHandler.removeProcessListener(processListener);

                return buf.toString();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (pipeIn != null) {
                    try {
                        pipeIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }*/

    public void dispose() {
        myProject = null;
        myRepl = null;
    }
}
