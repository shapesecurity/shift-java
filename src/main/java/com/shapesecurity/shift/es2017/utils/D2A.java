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

package com.shapesecurity.shift.es2017.utils;

import javax.annotation.Nonnull;

import java.math.BigInteger;

@SuppressWarnings({"checkstyle:magicnumber", "MagicNumber"})
public final class D2A {
    private static final BigInteger FIVE = BigInteger.valueOf(5);

    private D2A() {
    }

    private static BigInteger power(@Nonnull BigInteger b, int p) {
        BigInteger result;
        if (p == 0) {
            result = BigInteger.ONE;
        } else if (p == 1) {
            result = new BigInteger(b.toByteArray());
        } else {
            result = power(b, p >> 1);
            result = result.multiply(result);
            if ((p & 1) != 0) {
                result = result.multiply(b);
            }
        }
        return result;
    }

    @Nonnull
    private static String digitTrim(@Nonnull BigInteger value, @Nonnull String strValue, @Nonnull BigInteger err) {
        if (err.signum() == 0) {
            return strValue;
        }
        for (BigInteger digit = BigInteger.TEN.pow((int) Math.ceil(value.bitLength() * 0.3010299956639812)), half =
             digit.shiftRight(1);

             digit.signum() > 0; digit = digit.divide(BigInteger.TEN), half = digit.shiftRight(1)) {
            BigInteger addHalf = value.add(half);
            BigInteger mod = addHalf.mod(digit);
            BigInteger trimmed = addHalf.subtract(mod);
            if (trimmed.subtract(value).abs().compareTo(err) <= 0) {
                return trimmed.toString();
            }
        }
        return strValue;
    }

    // Gives a 16 digits decimal and an exponential that str[0:0]+"."+str[1:]+"e"+exp.toString is correct.
    @Nonnull
    private static DtoAInfo formatNumberHelper(double number) {
        long p = Double.doubleToRawLongBits(number);
        long shift = p >>> 52;
        int exp;
        long mantle;
        if (shift == 0) {
            exp = 1 - 1023 - 52;
            mantle = p;
        } else {
            exp = (int) (shift - 1023 - 52);
            mantle = (p & ((1L << 52) - 1)) | (1L << 52);
        }
        BigInteger bMantle = BigInteger.valueOf(mantle);
        BigInteger err = BigInteger.ONE;
        if (exp >= 0) {
            bMantle = bMantle.shiftLeft(exp);
            err = err.shiftLeft(exp);
        } else {
            BigInteger power = power(FIVE, -exp);
            bMantle = bMantle.multiply(power);
            err = err.multiply(power);
        }
        err = err.shiftRight(1);
        String original = bMantle.toString();
        int length = Math.min(0, exp) - 1 + original.length();
        String trimmed = digitTrim(bMantle, original, err);
        return new DtoAInfo(trimmed, length + trimmed.length() - original.length());
    }

    @SuppressWarnings("StringContatenationInLoop")
    @Nonnull
    public static String d2a(double number) {
        if (Double.isNaN(number)) {
            return "NaN";
        }
        if (number == 0) {
            return "0";
        }
        if (number < 0) {
            return '-' + d2a(-number);
        }
        if (Double.isInfinite(number)) {
            return "Infinity";
        }

        long numLong = (long) number;
        if ((double) numLong == number) {
            return Long.toString(numLong);
        }

        DtoAInfo info = formatNumberHelper(number);
        if (info.exp >= 21 || info.exp <= -7) {
            String part2 = info.digits.substring(1);
            while (!part2.isEmpty() && part2.charAt(part2.length() - 1) == '0') {
                part2 = part2.substring(0, part2.length() - 1);
            }

            return info.digits.substring(0, 1) + (part2.isEmpty() ? "" : '.' + part2) + (info.exp > 0 ? "e+" : "e") +
                    info.exp;
        } else if (info.exp >= 0) {
            info.digits += "00000";
            String part2 = info.digits.substring(info.exp + 1);
            while (!part2.isEmpty() && part2.charAt(part2.length() - 1) == '0') {
                part2 = part2.substring(0, part2.length() - 1);
            }
            return info.digits.substring(0, info.exp + 1) + (part2.isEmpty() ? "" : '.' + part2);
        } else {
            for (int i = 1; i < -info.exp; i++) {
                info.digits = '0' + info.digits;
            }
            while (!info.digits.isEmpty() && info.digits.charAt(info.digits.length() - 1) == '0') {
                info.digits = info.digits.substring(0, info.digits.length() - 1);
            }
            return "0." + info.digits;
        }
    }

    @Nonnull
    public static String shortD2a(double n) {
        String s = d2a(n);
        if (n >= 1e3 && n % 10 == 0) {
            if (s.indexOf('e') >= 0) {
                return s.replaceAll("e\\+", "e");
            }
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(s.length() - 1 - i) != '0') {
                    if (i > 1) {
                        return s.substring(0, s.length() - i) + "e" + i;
                    } else {
                        return s;
                    }
                }
            }
            return "0"; // Not reached
        } else if (n % 1 == 0) {
            if (n > 1e15 && n < 1e20) {
                long ln = (long) n;
                return "0x" + Long.toHexString(ln).toUpperCase();
            }
            return s.replaceAll("[eE]\\+", "e");
        } else {
            return s.replaceAll("^0\\.", ".").replaceAll("[eE]\\+", "e");
        }
    }

    private static final class DtoAInfo {
        public final int exp;
        @SuppressWarnings("PublicField")
        @Nonnull
        public String digits;

        DtoAInfo(@Nonnull String digits, int exp) {
            this.digits = digits;
            this.exp = exp;
        }
    }
}
