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

package com.ansorgit.plugins.bash.documentation;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.util.OSUtil;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NonNls;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Provides documentation by calling the systems info program and converts the output to html.
 * <br>
 * @author jansorg
 */
class SystemInfopageDocSource implements DocumentationSource, CachableDocumentationSource {
    @NonNls
    private static final String CHARSET_NAME = "utf-8";
    private static final int TIMEOUT_IN_MILLISECONDS = (int) TimeUnit.SECONDS.toMillis(4);
    private final String infoExecutable;
    private final String txt2htmlExecutable;
    private final Logger log = Logger.getInstance("#SystemInfopageDocSource");

    SystemInfopageDocSource() {
        infoExecutable = OSUtil.findBestExecutable("info");
        txt2htmlExecutable = OSUtil.findBestExecutable("txt2html");
    }

    public String documentation(PsiElement element, PsiElement originalElement) {
        if (infoExecutable == null) {
            return null;
        }

        if (!(element instanceof BashCommand) && !(element instanceof BashGenericCommand)) {
            return null;
        }

        BashCommand command = BashPsiUtils.findParent(element, BashCommand.class);
        if (command == null || !command.isExternalCommand()) {
            return null;
        }

        try {
            String commandName = command.getReferencedCommandName();

            boolean hasInfoPage = infoFileExists(commandName);
            if (!hasInfoPage) {
                return null;
            }

            String infoPageData = loadPlainTextInfoPage(commandName);

            if (txt2htmlExecutable != null) {
                return callTextToHtml(infoPageData);
            }

            return simpleTextToHtml(infoPageData);
        } catch (IOException e) {
            log.info("Failed to retrieve info page: ", e);
        }

        return null;
    }

    boolean infoFileExists(String commandName) throws IOException {
        //info -w locates an info file, exit status == 1 means that there is no info file 
        ProcessBuilder processBuilder = new ProcessBuilder(infoExecutable, "-w", commandName);

        CapturingProcessHandler processHandler = new CapturingProcessHandler(processBuilder.start(), Charset.forName(CHARSET_NAME), infoExecutable + " -w " + commandName);
        ProcessOutput output = processHandler.runProcess(TIMEOUT_IN_MILLISECONDS);

        return output.getExitCode() == 0 && !output.getStdout().isEmpty();
    }

    String loadPlainTextInfoPage(String commandName) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(infoExecutable, "-o", "-", commandName);

        CapturingProcessHandler processHandler = new CapturingProcessHandler(processBuilder.start(), Charset.forName(CHARSET_NAME), infoExecutable + " -o - " + commandName);
        ProcessOutput output = processHandler.runProcess(TIMEOUT_IN_MILLISECONDS);

        if (output.getExitCode() != 0) {
            return null;
        }

        return output.getStdout();
    }

    String callTextToHtml(final String infoPageData) throws IOException {
        if (txt2htmlExecutable == null) {
            //cheap fallback
            return "<html><body><pre>" + infoPageData + "</pre></body></html>";
        }

        ProcessBuilder processBuilder = new ProcessBuilder(txt2htmlExecutable, "--infile", "-");

        CapturingProcessHandler processHandler = new MyCapturingProcessHandler(processBuilder.start(), infoPageData, txt2htmlExecutable + " --inifile -");

        ProcessOutput output = processHandler.runProcess(TIMEOUT_IN_MILLISECONDS);
        if (output.getExitCode() != 0) {
            return null;
        }

        return output.getStdout();
    }

    public String documentationUrl(PsiElement element, PsiElement originalElement) {
        //there are no external urls for system info pages
        return null;
    }

    private String simpleTextToHtml(String infoPageData) {
        return "<html><bead></head><body><pre>" +
                infoPageData +
                "</pre></body>";
    }

    public String findCacheKey(PsiElement element, PsiElement originalElement) {
        if (element instanceof BashCommand && ((BashCommand) element).isExternalCommand()) {
            return ((BashCommand) element).getReferencedCommandName();
        }

        return null;
    }

    private class MyCapturingProcessHandler extends CapturingProcessHandler {
        private final String stdinData;

        MyCapturingProcessHandler(Process process, String stdinData, String commandLine) {
            super(process, Charset.forName(SystemInfopageDocSource.CHARSET_NAME), commandLine);
            this.stdinData = stdinData;
        }

        @Override
        public void startNotify() {
            super.startNotify();

            //we need to write after the stdout reader has been attached. Otherwise the process may block
            //and wait for the stdout to be read
            try {
                Writer stdinWriter = new OutputStreamWriter(getProcessInput(), CHARSET_NAME);
                stdinWriter.write(stdinData);
                stdinWriter.close();
            } catch (IOException e) {
                log.info("Exception passing data to txt2html", e);
            }
        }
    }
}
