/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: SystemInfopageDocSource.java, Class: SystemInfopageDocSource
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.documentation;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.util.SystemPathUtil;
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
 * <p/>
 * User: jansorg
 * Date: 08.05.2010
 * Time: 11:10:51
 */
class SystemInfopageDocSource implements DocumentationSource, CachableDocumentationSource {
    private String infoExecutable;
    private String txt2htmlExecutable;

    Logger log = Logger.getInstance("#SystemInfopageDocSource");

    @NonNls
    public static final String CHARSET_NAME = "utf-8";
    public static final int TIMEOUT_IN_MILLISECONDS = (int) TimeUnit.SECONDS.toMillis(4);

    SystemInfopageDocSource() {
        infoExecutable = SystemPathUtil.findBestExecutable("info");
        txt2htmlExecutable = SystemPathUtil.findBestExecutable("txt2html");
    }

    public String documentation(PsiElement element, PsiElement originalElement) {
        if (infoExecutable == null) {
            return null;
        }

        if (!(element instanceof BashCommand)) {
            return null;
        }

        BashCommand command = (BashCommand) element;
        if (!command.isExternalCommand()) {
            return null;
        }

        try {
            String commandName = command.getReferencedName();

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

        CapturingProcessHandler processHandler = new CapturingProcessHandler(processBuilder.start(), Charset.forName(CHARSET_NAME));
        ProcessOutput output = processHandler.runProcess(TIMEOUT_IN_MILLISECONDS);

        return output.getExitCode() == 0;
    }

    String loadPlainTextInfoPage(String commandName) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(infoExecutable, "-o", "-", commandName);

        CapturingProcessHandler processHandler = new CapturingProcessHandler(processBuilder.start(), Charset.forName(CHARSET_NAME));
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

        CapturingProcessHandler processHandler = new MyCapturingProcessHandler(processBuilder.start(), infoPageData);

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
        StringBuilder builder = new StringBuilder();
        builder.append("<html><bead></head><body><pre>");
        builder.append(infoPageData);
        builder.append("</pre></body>");

        return builder.toString();
    }

    public String findCacheKey(PsiElement element, PsiElement originalElement) {
        if (element instanceof BashCommand && ((BashCommand) element).isExternalCommand()) {
            return ((BashCommand) element).getReferencedName();
        }

        return null;
    }

    private class MyCapturingProcessHandler extends CapturingProcessHandler {
        private final String stdinData;

        public MyCapturingProcessHandler(Process process, String stdinData) {
            super(process, Charset.forName(SystemInfopageDocSource.CHARSET_NAME));
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
