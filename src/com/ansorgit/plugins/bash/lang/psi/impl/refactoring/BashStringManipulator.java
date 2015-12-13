package com.ansorgit.plugins.bash.lang.psi.impl.refactoring;

import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashStringUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Element manipulator for String elenents.
 *
 * @author jansorg
 */
public class BashStringManipulator implements ElementManipulator<BashString> {
    @Override
    public BashString handleContentChange(@NotNull BashString element, @NotNull TextRange textRange, String contentForRange) throws IncorrectOperationException {
        TextRange elementContentRange = element.getTextContentRange();

        if (contentForRange.length() > 2 && textRange.getStartOffset() == 0 && textRange.getLength() == element.getTextLength()) {
            contentForRange = contentForRange.substring(1, contentForRange.length() - 1);
        }

        String escapedContent = BashStringUtils.escape(contentForRange, '"');
        String newContent = elementContentRange.replace(element.getText(), escapedContent);

        BashString replacement = BashPsiElementFactory.createString(element.getProject(), newContent);
        assert replacement != null;

        return BashPsiUtils.replaceElement(element, replacement);
    }

    @Override
    public BashString handleContentChange(@NotNull BashString element, String newContent) throws IncorrectOperationException {
        //fixme
        //IntelliJ 13.x seems to work differently than >= 14.x
        //newContent already contains the string markers, thus we need to adjust the replacement range to include only the string content without the quotes

        if (newContent.endsWith("\"")) {
            if (newContent.startsWith("\"")) {
                newContent = newContent.substring(1, newContent.length() - 1);
            } else if (newContent.startsWith("$\"")) {
                newContent = newContent.substring(2, newContent.length() - 1);
            }
        }

        return handleContentChange(element, TextRange.create(0, element.getTextLength()), newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull BashString element) {
        return element.getTextContentRange();
    }
}
