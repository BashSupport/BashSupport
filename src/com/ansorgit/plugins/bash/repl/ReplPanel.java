/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ReplPanel.java, Class: ReplPanel
 * Last modified: 2010-03-03
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

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.util.BashInterpreterDetection;
import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.ui.PopupHandler;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kurt Christensen, ilyas
 */

public class ReplPanel extends JPanel implements Disposable {

    public static final String REPL_TOOLWINDOW_PLACE = "REPL.ToolWindow";
    public static final String REPL_TOOLWINDOW_POPUP_PLACE = "REPL.ToolWindow.Popup";

    private static final String BASH_REPL_ACTION_GROUP = "Bash.REPL.PanelGroup";

    private Project myProject;
    private Repl myRepl;

    public ReplPanel(@NotNull final Project project, @NotNull final Module module) throws IOException, ConfigurationException, CantRunException {
        setLayout(new BorderLayout());

        myProject = project;
        myRepl = new Repl(module);

        final ActionGroup actions = getActions();

        final JPanel toolbarPanel = new JPanel(new GridLayout());
        toolbarPanel.add(ActionManager.getInstance().createActionToolbar(REPL_TOOLWINDOW_PLACE, actions, false).getComponent());

        add(toolbarPanel, BorderLayout.WEST);
        add(myRepl.getView().getComponent(), BorderLayout.CENTER);

        Disposer.register(this, myRepl);
    }

    private ActionGroup getActions() {
        return (ActionGroup) ActionManager.getInstance().getAction(BASH_REPL_ACTION_GROUP);
    }

    public String writeToCurrentRepl(String s) {
        return writeToCurrentRepl(s, true);
    }

    public String writeToCurrentRepl(String input, boolean requestFocus) {
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
    }

    public void dispose() {
        myProject = null;
        myRepl = null;
    }

    private class Repl implements Disposable {
        public ConsoleView view;
        private ProcessHandler processHandler;

        public Repl(Module module) throws IOException, ConfigurationException, CantRunException {
            final TextConsoleBuilderImpl builder = new TextConsoleBuilderImpl(myProject) {
                private final ArrayList<Filter> filters = new ArrayList<Filter>();

                @Override
                public ConsoleView getConsole() {
                    final ConsoleViewImpl view = new ConsoleViewImpl(myProject, false, BashFileType.BASH_FILE_TYPE);

                    for (Filter filter : filters) {
                        view.addMessageFilter(filter);
                    }

                    return view;
                }

                @Override
                public void addFilter(Filter filter) {
                    filters.add(filter);
                }
            };

            view = builder.getConsole();

            // TODO - What does the "help ID" give us??

            final String baseDir = myProject.getLocation();
            //module.getModuleFilePath(); //fixme
            String[] commandLineArgs = {};//fixme

            GeneralCommandLine commandLine = new GeneralCommandLine();
            BashInterpreterDetection detect = new BashInterpreterDetection();
            commandLine.setExePath(detect.findBestLocation());
            commandLine.setWorkDirectory(baseDir);
            commandLine.getParametersList().addAll(commandLineArgs);

            Process myProcess = null;
            try {
                myProcess = commandLine.createProcess();
            } catch (ExecutionException e) {
                throw new CantRunException("The process could not be started: " + e.getMessage());
            }

            processHandler = new OSProcessHandler(myProcess, commandLine.getCommandLineString());

            ProcessTerminatedListener.attach(processHandler);
            processHandler.startNotify();
            view.attachToProcess(processHandler);

            final EditorEx ed = getEditor();
            /*ed.getContentComponent().addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent event) {
                    // TODO - This is probably wrong, actually, but it's a start...
                    //          ed.getCaretModel().moveToOffset(view.getContentSize());
                    //          ed.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                }
            });*/

            /* TODO - I may want this, but right now it pukes when you "Run Selected Text" from the editor and the result is an error...
                        ed.getContentComponent().addFocusListener(new FocusAdapter() {
                            public void focusGained(FocusEvent event) {
                                // TODO - This is probably wrong, actually, but it's a start...
                                ed.getCaretModel().moveToOffset(view.getContentSize());
                                ed.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                            }
                        });
            */

            // TODO - Experimental... Play around with what widgetry we'd like to see in the REPL
            //ed.getSettings().setSmartHome(true);
            //ed.getSettings().setVariableInplaceRenameEnabled(true);
            //ed.getSettings().setAnimatedScrolling(true);
            ed.getSettings().setFoldingOutlineShown(true);
            ed.getSettings().setLineNumbersShown(true);

            final ActionManager actionManager = ActionManager.getInstance();
            PopupHandler.installPopupHandler(ed.getContentComponent(),
                    (ActionGroup) actionManager.getAction("Bash.REPL.Group"), REPL_TOOLWINDOW_POPUP_PLACE, actionManager);

            view.print("Welcome to the BashSupport console\n", ConsoleViewContentType.NORMAL_OUTPUT);
        }

        public ConsoleView getView() {
            return view;
        }

        public void dispose() {
            if (processHandler != null) {
                processHandler.destroyProcess();
            }
        }

        public EditorEx getEditor() {
            EditorComponentImpl eci = (EditorComponentImpl) view.getPreferredFocusableComponent();
            return eci.getEditor();
        }
    }
}
