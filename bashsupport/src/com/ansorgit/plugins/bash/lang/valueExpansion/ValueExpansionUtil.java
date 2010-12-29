/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ValueExpansionUtil.java, Class: ValueExpansionUtil
 * Last modified: 2010-01-27
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.valueExpansion;

import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to work with expansions.
 * It can expand static expansions into the value which would be printed by a bash shell.
 * <p/>
 * <p/>
 * User: jansorg
 * Date: Nov 11, 2009
 * Time: 8:21:06 PM
 */
public class ValueExpansionUtil {
    /**
     * Returns the evaluated expansion which is the result of evaluating the spec parameter.
     *
     * @param spec           The expansion specification, e.g. a{1,2,3}
     * @param enhancedSyntax Whether the advanced Bash4 syntax should be supported or not
     * @return The result as a string. If the spec can not be evaluated null is returned.
     */
    public static String expand(String spec, boolean enhancedSyntax) {
        return expand(split(spec, enhancedSyntax)).toString();
    }

    static List<Expansion> split(String spec, boolean enhancedSyntax) {
        List<Expansion> result = new LinkedList<Expansion>();
        List<String> parts = StringUtil.split(spec, "{");

        for (String part : parts) {
            if (part.contains("}")) {
                int endOffset = part.indexOf('}');

                List<String> values = evaluateExpansionPattern(part.substring(0, endOffset), enhancedSyntax);
                result.add(new IteratingExpansion(values));

                if (endOffset + 1 < part.length()) {
                    result.add(new StaticExpansion(part.substring(endOffset + 1)));
                }
            } else {
                result.add(new StaticExpansion(part));
            }
        }

        return result;
    }

    /**
     * Evaluates a pattern like a,b,c or a..c. The string input is expected to be a comma
     * separated list. Each element is either a string or a range. A range cen either be numeric (1..10) or
     * a range between characters (a..z).
     * <p/>
     * Valid patterns examples: "a", "a,b", "a..z", "1..999", "abc,def"
     *
     * @param part           The element list to evaluate
     * @param enhancedSyntax Support the enhanced syntax of bash v4. This adds an additional step specifier in ranges and the zero padded numbers in ranges
     * @return The evaluated list of values which. Each element is evaluated and all values are added to the list.
     */
    static List<String> evaluateExpansionPattern(String part, boolean enhancedSyntax) {
        List<String> stringList = StringUtil.split(part, ",");
        List<String> result = new LinkedList<String>();

        //a range has to be the only expression in an expansion, otherwise it's treated as string / static expression
        if (stringList.size() == 1 && stringList.get(0).contains("..")) {
            if (!evaluateRangeExpression(stringList.get(0), result, enhancedSyntax)) {
                return Collections.emptyList();
            }
        } else {
            for (String e : stringList) {
                result.add(e); //single element
            }
        }

        return result;
    }

    private static boolean evaluateRangeExpression(String rangeText, List<String> result, boolean enhancedSyntax) {
        //range. The range is evaluated
        List<String> startEnd = StringUtil.split(rangeText, "..");
        if (enhancedSyntax && (startEnd.size() > 3 || startEnd.size() < 2)) {
            return false;
        } else if (!enhancedSyntax && startEnd.size() != 2) {
            return false;
        }

        String first = startEnd.get(0);
        String second = startEnd.get(1);
        String stepSpec = enhancedSyntax && startEnd.size() == 3 ? startEnd.get(2) : "";
        int step = NumberUtils.toInt(stepSpec, 1);
        if (NumberUtils.toInt(first, 1) > NumberUtils.toInt(second, 1) && stepSpec.isEmpty()) {
            step = -1;
        }
        boolean hasValidStep = stepSpec.isEmpty() || NumberUtils.isNumber(stepSpec);

        if (NumberUtils.isNumber(first) && NumberUtils.isNumber(second) && hasValidStep) {
            //numeric range
            int current = NumberUtils.toInt(first);
            int end = NumberUtils.toInt(second);

            //find out if the numbers are padded. If yes, add ad left padding (in enhanced mode only)
            int padTargetWidth = -1;
            if (enhancedSyntax) {
                boolean hasPadding = (first.startsWith("0") || second.startsWith("0"));
                padTargetWidth = hasPadding ? Math.max(first.length(), second.length()) : -1;
            }

            while (step > 0 ? current <= end : current >= end) {
                String value = String.valueOf(current);
                if (padTargetWidth > value.length()) {
                    //prepend the padding on the left
                    value = StringUtil.repeatSymbol('0', padTargetWidth - value.length()) + value;
                }

                result.add(value);
                current += step;
            }
        } else if (first.length() == 1 && second.length() == 1 && hasValidStep) {
            //single character range
            char current = first.charAt(0);
            char end = second.charAt(0);

            while (current <= end) {
                result.add(String.valueOf(current));
                current += step;
            }
        } else {
            //add as a static expression
            result.add(rangeText);
        }

        return true;
    }

    private static StringBuilder expand(List<Expansion> expansions) {
        List<Expansion> reversed = new ArrayList<Expansion>(expansions);
        Collections.reverse(reversed);

        StringBuilder result = new StringBuilder();
        do {
            result.append(makeLine(reversed));

            if (stillMore(reversed)) {
                result.append(" ");
            }
        } while (stillMore(reversed));

        return result;
    }

    private static boolean stillMore(List<Expansion> expansions) {
        for (Expansion expansion : expansions) {
            if (expansion.hasNext()) return true;
        }

        return false;
    }

    private static StringBuilder makeLine(List<Expansion> reversed) {
        boolean lastFlipped = true;
        StringBuilder line = new StringBuilder();
        for (Expansion e : reversed) {
            line.insert(0, e.findNext(lastFlipped));
            lastFlipped = e.isFlipped();
        }
        return line;
    }

}
