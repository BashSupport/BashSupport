package com.ansorgit.plugins.bash.editor.codecompletion;

/**
 * The global order of completion suggestions
 * <p/>
 * The defined order is the reverse of what is displayed in the autocomletion popup.
 * <p/>
 * User: jansorg
 * Date: 07.02.11
 * Time: 20:31
 */
public enum CompletionGrouping {
    BuiltInVar,
    GlobalVar,
    NormalVar,

    GlobalCommand,
    Function,

    AbsoluteFilePath,
    RelativeFilePath
}
