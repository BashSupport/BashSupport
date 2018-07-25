package com.ansorgit.plugins.bash.runner.terminal;

import com.google.common.collect.Maps;
import com.intellij.execution.configurations.EncodingEnvironmentUtil;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.project.Project;
import com.pty4j.PtyProcess;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.terminal.LocalTerminalDirectRunner;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author jansorg
 */
public class BashLocalTerminalRunner extends LocalTerminalDirectRunner {
    private final String scriptName;
    private final GeneralCommandLine cmd;
    private volatile ProcessHandler processHandler;

    public BashLocalTerminalRunner(Project project, String scriptName, GeneralCommandLine cmd) {
        super(project);
        this.scriptName = scriptName;
        this.cmd = cmd;
    }

    @Override
    public String runningTargetName() {
        return "BashSupport " + scriptName;
    }

    @Override
    protected String getTerminalConnectionName(PtyProcess process) {
        return "BashSupport " + scriptName;
    }

    // @Override 182.x doesn't implement this anymore
    public String[] getCommand() {
        String exePath = cmd.getExePath();
        return cmd.getCommandLineList(exePath).toArray(new String[0]);
    }

    @Override
    protected PtyProcess createProcess(@Nullable String directory) throws ExecutionException {
        // the env setup must add TERM as in the super implementation
        Map<String, String> env = Maps.newHashMap();
        if (cmd.isPassParentEnvironment()) {
            env.putAll(cmd.getParentEnvironment());
        }
        env.putAll(cmd.getEnvironment());

        env.put("TERM", "xterm-256color");
        EncodingEnvironmentUtil.setLocaleEnvironmentIfMac(env, cmd.getCharset());

        try {
            return PtyProcess.exec(getCommand(), env, directory != null ? directory : cmd.getWorkDirectory().getAbsolutePath(), true);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    public ProcessHandler getProcessHandler() {
        return processHandler;
    }

    @Override
    protected ProcessHandler createProcessHandler(PtyProcess process) {
        if (processHandler == null) {
            processHandler = super.createProcessHandler(process);
        }
        return processHandler;
    }
}
