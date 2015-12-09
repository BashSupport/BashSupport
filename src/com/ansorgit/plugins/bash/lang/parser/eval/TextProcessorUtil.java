package com.ansorgit.plugins.bash.lang.parser.eval;

class TextProcessorUtil {
    static boolean hasNext(CharSequence chars, int index, CharSequence expected) {
        if (index + expected.length() >= chars.length()) {
            return false;
        }

        return chars.subSequence(index, index + expected.length()).equals(expected);
    }

    static void resetOffsets(int[] sourceOffsets) {
        for (int i = 0; i < sourceOffsets.length; i++) {
            sourceOffsets[i] = -1;
        }
    }
}
