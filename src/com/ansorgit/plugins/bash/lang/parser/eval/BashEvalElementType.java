package com.ansorgit.plugins.bash.lang.parser.eval;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.ILazyParseableElementType;
import org.jetbrains.annotations.NotNull;

public class BashEvalElementType extends ILazyParseableElementType {
    public BashEvalElementType() {
        super("eval block", BashFileType.BASH_LANGUAGE);
    }

    @Override
    protected ASTNode doParseContents(@NotNull ASTNode chameleon, @NotNull PsiElement psi) {
        Project project = psi.getProject();
        CharSequence originalText = chameleon.getChars().toString();

        if (originalText.length() < 3) {
            return chameleon; //fixme is this right?
        }

        String prefix = originalText.subSequence(0, 1).toString();
        String content = originalText.subSequence(1, originalText.length() - 1).toString();
        String suffix = originalText.subSequence(originalText.length() - 1, originalText.length()).toString();

        TextPreprocessor textProcessor = new BashSimpleTextPreprocessor(TextRange.from(1, content.length()));
        StringBuilder processedContent = new StringBuilder(content.length());
        textProcessor.decode(content, processedContent);

        String processedComplete = prefix + processedContent + suffix;

        ParserDefinition def = LanguageParserDefinitions.INSTANCE.forLanguage(BashFileType.BASH_LANGUAGE);
        PsiParser parser = def.createParser(project);
        Lexer bashLexer = def.createLexer(project);

        PrefixSuffixAddingLexer prefixSuffixLexer = new PrefixSuffixAddingLexer(bashLexer,
                prefix, TokenType.WHITE_SPACE,
                suffix, TokenType.WHITE_SPACE);

        UnescapingPsiBuilder adaptingPsiBuilder = new UnescapingPsiBuilder(project,
                def,
                prefixSuffixLexer,
                chameleon,
                originalText,
                processedComplete,
                textProcessor);

        return parser.parse(this, adaptingPsiBuilder).getFirstChildNode();
    }
}
