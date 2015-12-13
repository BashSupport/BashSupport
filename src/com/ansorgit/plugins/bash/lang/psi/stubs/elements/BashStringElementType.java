package com.ansorgit.plugins.bash.lang.psi.stubs.elements;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionStub;
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashStringImpl;
import org.jetbrains.annotations.NotNull;

public class BashStringElementType extends AbstractInjectionHostElementType {
    public BashStringElementType() {
        super("bash.stringStubElement", "Bash string element type");
    }

    @Override
    public BashLanguageInjectionHost createPsi(@NotNull BashLanguageInjectionStub stub) {
        return new BashStringImpl(stub, BashElementTypes.STRING_ELEMENT);
    }
}
