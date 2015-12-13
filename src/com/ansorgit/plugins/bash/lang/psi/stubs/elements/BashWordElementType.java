package com.ansorgit.plugins.bash.lang.psi.stubs.elements;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionStub;
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashWordImpl;
import org.jetbrains.annotations.NotNull;

public class BashWordElementType extends AbstractInjectionHostElementType {
    public BashWordElementType() {
        super("bash.wordStubElement", "Bash word stub");
    }

    @Override
    public BashLanguageInjectionHost createPsi(@NotNull BashLanguageInjectionStub stub) {
        return new BashWordImpl(stub, BashElementTypes.PARSED_WORD_ELEMENT);
    }
}
