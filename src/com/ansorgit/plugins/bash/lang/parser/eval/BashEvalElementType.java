package com.ansorgit.plugins.bash.lang.parser.eval;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.ILazyParseableElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class BashEvalElementType extends ILazyParseableElementType {
    public BashEvalElementType() {
        super("eval block", BashFileType.BASH_LANGUAGE);
    }

    @Override
    protected ASTNode doParseContents(@NotNull ASTNode chameleon, @NotNull PsiElement psi) {
        Project project = psi.getProject();

        String originalText = chameleon.getChars().toString();
        ParserDefinition def = LanguageParserDefinitions.INSTANCE.forLanguage(BashFileType.BASH_LANGUAGE);

        boolean isDoubleQuoted = originalText.startsWith("\"") && originalText.endsWith("\"");
        boolean isSingleQuoted = originalText.startsWith("'") && originalText.endsWith("'");
        boolean isEscapingSingleQuoted = originalText.startsWith("$'") && originalText.endsWith("'");
        boolean isUnquoted = !isDoubleQuoted && !isSingleQuoted && !isEscapingSingleQuoted;

        String prefix = isUnquoted ? "" : originalText.subSequence(0, isEscapingSingleQuoted ? 2 : 1).toString();
        String content = isUnquoted ? originalText : originalText.subSequence(isEscapingSingleQuoted ? 2 : 1, originalText.length() - 1).toString();
        String suffix = isUnquoted ? "" : originalText.subSequence(originalText.length() - 1, originalText.length()).toString();

        TextPreprocessor textProcessor;
        if (isEscapingSingleQuoted) {
            textProcessor = new BashEnhancedTextPreprocessor(TextRange.from(2, content.length()));
        } else if (isSingleQuoted) {
            //no escape handling for single-quoted strings
            textProcessor = new BashIdentityTextPreprocessor(TextRange.from(prefix.length(), content.length()));
        } else {
            //fallback to simple escape handling
            textProcessor = new BashSimpleTextPreprocessor(TextRange.from(prefix.length(), content.length()));
        }

        StringBuilder unescapedContent = new StringBuilder(content.length());
        textProcessor.decode(content, unescapedContent);

        Lexer lexer = isUnquoted
                ? def.createLexer(project)
                : new PrefixSuffixAddingLexer(def.createLexer(project), prefix, TokenType.WHITE_SPACE, suffix, TokenType.WHITE_SPACE);

        PsiBuilder psiBuilder = new UnescapingPsiBuilder(project,
                def,
                lexer,
                chameleon,
                originalText,
                prefix + unescapedContent + suffix,
                textProcessor);

        return def.createParser(project).parse(this, psiBuilder).getFirstChildNode();
    }
}
