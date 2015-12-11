package com.ansorgit.plugins.bash.lang.psi.resolve;

import com.ansorgit.plugins.bash.editor.highlighting.MinMaxValue;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.util.BashAbstractProcessor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;

import java.util.List;

public class ResolveIterationTest extends AbstractResolveTest {
    final Key<Integer> countKey = Key.create("WALK_COUNT");

    public void testResolveWalking() throws Exception {
        checkTreeWalking(new DummyBashProcessor(countKey));
    }

    public void testResolveWalkingBig() throws Exception {
        checkTreeWalking(new DummyBashProcessor(countKey));
    }

    public void testVarResolveWalking() throws Exception {
        PsiReference reference = configure();

        PsiElement element = reference.getElement();
        assert element instanceof BashVar;

        DummyBashProcessor processor = new DummyBashProcessor(countKey);

        BashPsiUtils.varResolveTreeWalkUp(processor, (BashVar) element, element.getContainingFile(), ResolveState.initial());

        assertSingleVisitedElements(element.getContainingFile());
    }

    /**
     * Checks that the file tree walking visits every element only once.
     *
     * @param processor
     * @throws Exception
     */
    protected void checkTreeWalking(BashAbstractProcessor processor) throws Exception {
        PsiFile file = configure().getElement().getContainingFile();
        file.processDeclarations(processor, ResolveState.initial(), null, file);

        assertSingleVisitedElements(file);
    }

    private void assertSingleVisitedElements(PsiFile file) {
        final List<PsiElement> multiVisits = Lists.newLinkedList();
        final int[] singleVisits = {0};
        BashPsiUtils.visitRecursively(file, new BashVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (countKey.isIn(element) && countKey.get(element) > 1) {
                    multiVisits.add(element);
                } else if (countKey.isIn(element) && countKey.get(element) == 1) {
                    singleVisits[0]++;
                }
            }
        });

        if (!multiVisits.isEmpty()) {
            MinMaxValue minMaxValue = new MinMaxValue();
            for (PsiElement element : multiVisits) {
                if (countKey.isIn(element)) {
                    minMaxValue.add(countKey.get(element));
                }
            }

            String elementNames = Iterators.toString(multiVisits.iterator());

            Assert.fail("Every psi element must be only visited once: " + minMaxValue.max() + " maximum per element: Multiple visits for: " + elementNames);
        }

        Assert.assertTrue("There have to be elments visited only once", singleVisits[0] > 0);
    }

    private static class DummyBashProcessor extends BashAbstractProcessor {
        private final Key<Integer> countKey;

        public DummyBashProcessor(Key<Integer> countKey) {
            super(false);
            this.countKey = countKey;
        }

        @Override
        public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
            //executeCount[0]++;

            if (countKey.isIn(element)) {
                countKey.set(element, countKey.get(element) + 1);
            } else {
                countKey.set(element, 1);
            }

            return true;
        }

        @Nullable
        @Override
        public <T> T getHint(@NotNull Key<T> hintKey) {
            return null;
        }
    }
}
