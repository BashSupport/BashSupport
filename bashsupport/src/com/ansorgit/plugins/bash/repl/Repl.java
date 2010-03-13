/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: Repl.java, Class: Repl
 * Last modified: 2010-03-11
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

import com.ansorgit.plugins.bash.util.BashInterpreterDetection;
import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.PopupHandler;

import java.io.IOException;

/**
 * This code is based on the ClojurePlugin repl console.
 * <p/>
 * User: jansorg
 * Date: Mar 4, 2010
 * Time: 6:26:08 PM
 */
class Repl implements Disposable {
    public ConsoleView view;
    private ProcessHandler processHandler;

    public Repl(Module module, AnAction... customActions) throws IOException, ConfigurationException, CantRunException {
        Project myProject = module.getProject();

        final TextConsoleBuilderImpl builder = new BashTextConsoleBuilder(myProject);
        view = builder.getConsole();
        if (view instanceof ConsoleViewImpl) {
            ConsoleViewImpl v = (ConsoleViewImpl) view;
            for (AnAction a : customActions) {
                v.addCustomConsoleAction(a);
            }
        }

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
        ed.getSettings().setSmartHome(true);
        //ed.getSettings().setVariableInplaceRenameEnabled(true);
        //ed.getSettings().setAnimatedScrolling(true);
        //ed.getSettings().setFoldingOutlineShown(true);
        //ed.getSettings().setLineNumbersShown(true);

        final ActionManager actionManager = ActionManager.getInstance();
        PopupHandler.installPopupHandler(ed.getContentComponent(),
                (ActionGroup) actionManager.getAction("Bash.REPL.Group"), ReplPanel.REPL_TOOLWINDOW_POPUP_PLACE, actionManager);

        view.print(commandLine.getCommandLineString(), ConsoleViewContentType.NORMAL_OUTPUT);
        view.print("Welcome to the BashSupport console\n\n", ConsoleViewContentType.NORMAL_OUTPUT);
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
