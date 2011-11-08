/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: Triple.java, Class: Triple
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

/**
 * A triple is a combination of three values of (optionally) different types.
 * <p/>
 * User: jansorg
 * Date: Jan 29, 2010
 * Time: 7:07:23 PM
 */
public final class Triple<A, B, C> {
    public final A first;
    public final B second;
    public final C third;

    public static <A, B, C> Triple<A, B, C> create(A a, B b, C c) {
        return new Triple<A, B, C>(a, b, c);
    }

    private Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
