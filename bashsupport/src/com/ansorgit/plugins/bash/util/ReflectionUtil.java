/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ReflectionUtil.java, Class: ReflectionUtil
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Helper methods to help with reflection runtime operations.
 * <p/>
 * Date: 18.04.2009
 * Time: 12:00:35
 *
 * @author Joachim Ansorg
 */
public class ReflectionUtil {
    private static Logger log = Logger.getInstance("#bash.ReflectionUtil");

    private static final Map<Pair<Class<?>, String>, Field> fieldCache = new ConcurrentHashMap<Pair<Class<?>, String>, Field>();

    /**
     * Changes a value of a short member using a certain variable name.
     *
     * @param owner The object which contains the short member variable.
     * @param name  The name of the member variable.
     * @param value The value to set to
     * @return True if the action was successful, false if was not successful.
     */
    public static boolean setShort(@NotNull final Object owner, final String name, final short value) {
        checkArgument(StringUtil.isNotEmpty(name));

        final Class<?> aClass = owner.getClass();
        boolean result = false;

        try {
            Pair<Class<?>, String> mapKey = new Pair<Class<?>, String>(aClass, name);

            Field field = fieldCache.get(mapKey);

            if (field == null) {
                field = aClass.getDeclaredField(name);
                field.setAccessible(true);

                fieldCache.put(mapKey, field);
            }

            field.setShort(owner, value);

            result = true;
        } catch (Exception e) {
            log.warn("Illegal access", e);
        }

        return result;
    }
}
