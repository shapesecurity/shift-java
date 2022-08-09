/*
 * Copyright 2014 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shapesecurity.shift.es2018.utils;

import org.mozilla.javascript.ScriptRuntime;

import javax.annotation.Nonnull;

public final class D2A {
    private D2A() {
    }

    @Nonnull
    public static String d2a(double number) {
        return ScriptRuntime.numberToString(number, 10);
    }

    @Nonnull
    public static String shortD2a(double number) {
        if (Double.isNaN(number)) {
            return "NaN";
        }
        if (number == 0) {
            return "0";
        }
        if (Double.isInfinite(number)) {
            return number < 0 ? "-2e308" : "2e308";
        }
        String s = d2a(number);
        if (number >= 1e3 && number % 10 == 0) {
            if (s.indexOf('e') >= 0) {
                return s.replaceAll("e\\+", "e");
            }
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(s.length() - 1 - i) != '0') {
                    if (i > 2) {
                        return s.substring(0, s.length() - i) + "e" + i;
                    } else {
                        return s;
                    }
                }
            }
            return "0"; // Not reached
        } else if (number % 1 == 0) {
            if (number > 1e12 && number < Math.pow(2, 63)) {
                long ln = (long) number;
                return "0x" + Long.toHexString(ln).toUpperCase();
            }
            return s.replaceAll("[eE]\\+", "e");
        } else {
            return s.replaceAll("^0\\.", ".").replaceAll("[eE]\\+", "e");
        }
    }
}
