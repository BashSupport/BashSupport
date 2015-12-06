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
import com.intellij.psi.tree.ILazyParseableElementType;
import org.jetbrains.annotations.NotNull;

public class BashEvalElementType extends ILazyParseableElementType {
    public BashEvalElementType() {
        super("eval block", BashFileType.BASH_LANGUAGE);
    }

    @Override
    protected ASTNode doParseContents(@NotNull ASTNode chameleon, @NotNull PsiElement psi) {
        final Project project = psi.getProject();

        CharSequence chars = chameleon.getChars();

        String prefix = chars.subSequence(0, 1).toString();
        String suffix = chars.subSequence(chars.length() - 1, chars.length()).toString();
        String content = chars.subSequence(1, chars.length() - 1).toString();

        TextPreprocessor textProcessor = new BashSimpleTextPreprocessor(TextRange.from(1, content.length()));

        StringBuilder processedText = new StringBuilder(content.length());
        textProcessor.decode(content, processedText);

        ParserDefinition languageDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(BashFileType.BASH_LANGUAGE);

        Lexer lexer = languageDefinition.createLexer(project);
        PrefixSuffixAddingLexer wrappingLexer = new PrefixSuffixAddingLexer(lexer,
                prefix, BashTokenTypes.WORD,
                suffix, BashTokenTypes.WORD);

        PsiParser parser = languageDefinition.createParser(project);
        UnescapingPsiBuilder adaptingPsiBuilder = new UnescapingPsiBuilder(project, languageDefinition, wrappingLexer, chameleon, processedText, textProcessor);

        return parser.parse(this, adaptingPsiBuilder).getFirstChildNode();
    }
}
