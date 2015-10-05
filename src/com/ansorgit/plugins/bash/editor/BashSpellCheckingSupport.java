package com.ansorgit.plugins.bash.editor;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.spellchecker.inspections.TextSplitter;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import org.jetbrains.annotations.NotNull;

/**
 * Bash spell checking support.
 *
 * @author jansorg
 */
public class BashSpellCheckingSupport extends SpellcheckingStrategy {

    @NotNull
    @java.lang.Override
    public Tokenizer getTokenizer(PsiElement psiElement) {
        if (psiElement instanceof BashString) {
            return new BashStringTokenizer();
        }

        if (psiElement instanceof BashWord) {
            return new BashWordTokenizer();
        }

        if (psiElement instanceof BashHereDoc) {
            return new BashHeredocTokenizer();
        }

        return super.getTokenizer(psiElement);
    }

    /**
     * Tokenizes string content for the spellchecker
     */
    private static class BashStringTokenizer extends Tokenizer<BashString> {
        @Override
        public void tokenize(@NotNull BashString element, TokenConsumer consumer) {
            //indexes the string content leaf nodes
            ASTNode[] contentNodes = element.getNode().getChildren(TokenSet.create(BashTokenTypes.STRING_CONTENT));
            for (ASTNode node : contentNodes) {
                consumer.consumeToken(node.getPsi(), false, TextSplitter.getInstance());
            }
        }
    }

    /**
     * Tokenizes the content of single-quotes strings for spellchecking support
     */
    private static class BashWordTokenizer extends Tokenizer<BashWord> {
        @Override
        public void tokenize(@NotNull BashWord element, TokenConsumer tokenConsumer) {
            tokenConsumer.consumeToken(element, element.getText(), false, 0, element.getTextContentRange(), TextSplitter.getInstance());
        }
    }

    /**
     * Tokenizes heredoc content for spellchecking support.
     */
    private static class BashHeredocTokenizer extends Tokenizer<BashHereDoc> {
        @Override
        public void tokenize(@NotNull BashHereDoc element, TokenConsumer consumer) {
            //indexes the heredoc content leaf nodes
            ASTNode[] contentNodes = element.getNode().getChildren(TokenSet.create(BashTokenTypes.HEREDOC_CONTENT));
            for (ASTNode node : contentNodes) {
                consumer.consumeToken(node.getPsi(), false, TextSplitter.getInstance());
            }
        }
    }
}
