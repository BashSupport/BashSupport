/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.util;

import java.util.EmptyStackException;

/*
 * This is originally a copy from IntelliJ
 * @author max
 */
public class IntStack {
    private int[] data;
    private int size;

    public IntStack(int initialCapacity) {
        data = new int[initialCapacity];
        size = 0;
    }

    public IntStack() {
        this(5);
    }

    public void push(int t) {
        if (size >= data.length) {
            int[] newdata = new int[(int) Math.ceil(data.length * 1.5)];
            System.arraycopy(data, 0, newdata, 0, size);
            data = newdata;
        }
        data[size++] = t;
    }

    public int peek() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return data[size - 1];
    }

    public int pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return data[--size];
    }

    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            if (data[i] == value) {
                return true;
            }
        }

        return false;
    }

    public boolean empty() {
        return size == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IntStack) {
            IntStack otherStack = (IntStack) o;
            if (size != otherStack.size) {
                return false;
            }
            for (int i = 0; i < otherStack.size; i++) {
                if (data[i] != otherStack.data[i]) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public void clear() {
        size = 0;
    }
}
