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

package com.shapesecurity.shift.es2016.utils;

import com.shapesecurity.functional.data.ImmutableList;

import javax.annotation.Nonnull;

import java.util.HashSet;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.shapesecurity.shift.es2016.parser.Tokenizer.fromCodePoint;

@SuppressWarnings("MagicNumber")
public final class Utils {
    // static only
    private Utils() {
    }

    public static boolean isRestrictedWord(@Nonnull String name) {
        return "eval".equals(name) || "arguments".equals(name);
    }

    public static boolean areUniqueNames(@Nonnull ImmutableList<String> list) {
        HashSet<String> paramSet = new HashSet<>();
        for (String item : list) {
            if (paramSet.contains(item)) {
                return false;
            } else {
                paramSet.add(item);
            }
        }
        return true;
    }

    @Nonnull
    public static String escapeStringLiteral(@Nonnull String stringValue) {
        int nSingle = 0;
        int nDouble = 0;
        for (int i = 0, l = stringValue.length(); i < l; ++i) {
            char ch = stringValue.charAt(i);
            if (ch == '\"') {
                ++nDouble;
            } else if (ch == '\'') {
                ++nSingle;
            }
        }
        char delim = nDouble > nSingle ? '\'' : '\"';
        return escapeStringLiteral(stringValue, delim);
    }

    @Nonnull
    public static String escapeStringLiteral(@Nonnull String stringValue, char delim) {
        StringBuilder result = new StringBuilder();
        result.append(delim);
        for (int i = 0; i < stringValue.length(); i++) {
            char ch = stringValue.charAt(i);
            if (ch == delim) {
                result.append("\\").append(delim);
            } else if (ch == '\b') {
                result.append("\\b");
            } else if (ch == '\t') {
                result.append("\\t");
            } else if (ch == '\n') {
                result.append("\\n");
            } else if (ch == '\u000B') {
                result.append("\\v");
            } else if (ch == '\u000C') {
                result.append("\\f");
            } else if (ch == '\r') {
                result.append("\\r");
            } else if (ch == '\\') {
                result.append("\\\\");
            } else if (ch == '\u2028') {
                result.append("\\u2028");
            } else if (ch == '\u2029') {
                result.append("\\u2029");
            } else {
                result.append(ch);
            }
        }
        result.append(delim);
        return result.toString();
    }

    public static boolean isReservedWordES5(@Nonnull String word) {
        switch (word) {
            case "break":
            case "case":
            case "catch":
            case "class":
            case "const":
            case "continue":
            case "debugger":
            case "default":
            case "delete":
            case "do":
            case "else":
            case "enum":
            case "export":
            case "extends":
            case "finally":
            case "for":
            case "function":
            case "if":
            case "import":
            case "in":
            case "instanceof":
            case "new":
            case "return":
            case "super":
            case "switch":
            case "this":
            case "throw":
            case "try":
            case "typeof":
            case "var":
            case "void":
            case "while":
            case "with":
                return true;
            default:
                return false;
        }
    }

    public static boolean isReservedWord(@Nonnull String word) {
        switch (word) {
            case "break":
            case "case":
            case "catch":
            case "class":
            case "const":
            case "continue":
            case "debugger":
            case "default":
            case "delete":
            case "do":
            case "else":
            case "enum":
            case "export":
            case "extends":
            case "false":
            case "finally":
            case "for":
            case "function":
            case "if":
            case "import":
            case "in":
            case "instanceof":
            case "new":
            case "null":
            case "return":
            case "super":
            case "switch":
            case "this":
            case "throw":
            case "true":
            case "try":
            case "typeof":
            case "var":
            case "void":
            case "while":
            case "with":
            case "yield":
                return true;
            default:
                return false;
        }
    }

    public static boolean isStrictModeReservedWordES5(@Nonnull String word) {
        switch (word) {
            case "implements":
            case "interface":
            case "package":
            case "protected":
            case "public":
            case "static":
            case "let":
            case "yield":
                return true;
            default:
                return isReservedWordES5(word);
        }
    }

    public static boolean isStrictModeReservedWord(@Nonnull String word) {
        switch (word) {
            case "implements":
            case "interface":
            case "let":
            case "package":
            case "private":
            case "protected":
            case "public":
            case "static":
                return true;
            default:
                return isReservedWord(word);
        }
    }

    public static boolean isDecimalDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }

    // 7.3 Line Terminators
    public static boolean isLineTerminator(char ch) {
        return (ch == '\r') || (ch == '\n') || (ch == '\u2028') || (ch == '\u2029');
    }

    // This regex is generated from scripts/make-unicode-regexen.js
    private static final Predicate<String> isWhitespace = Pattern.compile("[\\u00a0\\u1680\\u2000-\\u200a\\u202f\\u205f\\u3000\\ufeff]").asPredicate();
    public static boolean isWhitespace(char ch) {
        if (ch < 0x80) {
            return (ch == 0x20) || (ch == 0x09) || (ch == 0x0B) || (ch == 0x0C);
        }
        return isWhitespace.test(fromCodePoint(ch));
    }

    private static final boolean[] IDENTIFIER_START = new boolean[0x80];
    // This regex is generated from scripts/make-unicode-regexen.js
    private static final Predicate<String> isNonAsciiIdentifierStart = Pattern.compile("[\\u00aa\\u00b5\\u00ba\\u00c0-\\u00d6\\u00d8-\\u00f6\\u00f8-\\u02c1\\u02c6-\\u02d1\\u02e0-\\u02e4\\u02ec\\u02ee\\u0370-\\u0374\\u0376\\u0377\\u037a-\\u037d\\u037f\\u0386\\u0388-\\u038a\\u038c\\u038e-\\u03a1\\u03a3-\\u03f5\\u03f7-\\u0481\\u048a-\\u052f\\u0531-\\u0556\\u0559\\u0561-\\u0587\\u05d0-\\u05ea\\u05f0-\\u05f2\\u0620-\\u064a\\u066e\\u066f\\u0671-\\u06d3\\u06d5\\u06e5\\u06e6\\u06ee\\u06ef\\u06fa-\\u06fc\\u06ff\\u0710\\u0712-\\u072f\\u074d-\\u07a5\\u07b1\\u07ca-\\u07ea\\u07f4\\u07f5\\u07fa\\u0800-\\u0815\\u081a\\u0824\\u0828\\u0840-\\u0858\\u08a0-\\u08b4\\u08b6-\\u08bd\\u0904-\\u0939\\u093d\\u0950\\u0958-\\u0961\\u0971-\\u0980\\u0985-\\u098c\\u098f\\u0990\\u0993-\\u09a8\\u09aa-\\u09b0\\u09b2\\u09b6-\\u09b9\\u09bd\\u09ce\\u09dc\\u09dd\\u09df-\\u09e1\\u09f0\\u09f1\\u0a05-\\u0a0a\\u0a0f\\u0a10\\u0a13-\\u0a28\\u0a2a-\\u0a30\\u0a32\\u0a33\\u0a35\\u0a36\\u0a38\\u0a39\\u0a59-\\u0a5c\\u0a5e\\u0a72-\\u0a74\\u0a85-\\u0a8d\\u0a8f-\\u0a91\\u0a93-\\u0aa8\\u0aaa-\\u0ab0\\u0ab2\\u0ab3\\u0ab5-\\u0ab9\\u0abd\\u0ad0\\u0ae0\\u0ae1\\u0af9\\u0b05-\\u0b0c\\u0b0f\\u0b10\\u0b13-\\u0b28\\u0b2a-\\u0b30\\u0b32\\u0b33\\u0b35-\\u0b39\\u0b3d\\u0b5c\\u0b5d\\u0b5f-\\u0b61\\u0b71\\u0b83\\u0b85-\\u0b8a\\u0b8e-\\u0b90\\u0b92-\\u0b95\\u0b99\\u0b9a\\u0b9c\\u0b9e\\u0b9f\\u0ba3\\u0ba4\\u0ba8-\\u0baa\\u0bae-\\u0bb9\\u0bd0\\u0c05-\\u0c0c\\u0c0e-\\u0c10\\u0c12-\\u0c28\\u0c2a-\\u0c39\\u0c3d\\u0c58-\\u0c5a\\u0c60\\u0c61\\u0c80\\u0c85-\\u0c8c\\u0c8e-\\u0c90\\u0c92-\\u0ca8\\u0caa-\\u0cb3\\u0cb5-\\u0cb9\\u0cbd\\u0cde\\u0ce0\\u0ce1\\u0cf1\\u0cf2\\u0d05-\\u0d0c\\u0d0e-\\u0d10\\u0d12-\\u0d3a\\u0d3d\\u0d4e\\u0d54-\\u0d56\\u0d5f-\\u0d61\\u0d7a-\\u0d7f\\u0d85-\\u0d96\\u0d9a-\\u0db1\\u0db3-\\u0dbb\\u0dbd\\u0dc0-\\u0dc6\\u0e01-\\u0e30\\u0e32\\u0e33\\u0e40-\\u0e46\\u0e81\\u0e82\\u0e84\\u0e87\\u0e88\\u0e8a\\u0e8d\\u0e94-\\u0e97\\u0e99-\\u0e9f\\u0ea1-\\u0ea3\\u0ea5\\u0ea7\\u0eaa\\u0eab\\u0ead-\\u0eb0\\u0eb2\\u0eb3\\u0ebd\\u0ec0-\\u0ec4\\u0ec6\\u0edc-\\u0edf\\u0f00\\u0f40-\\u0f47\\u0f49-\\u0f6c\\u0f88-\\u0f8c\\u1000-\\u102a\\u103f\\u1050-\\u1055\\u105a-\\u105d\\u1061\\u1065\\u1066\\u106e-\\u1070\\u1075-\\u1081\\u108e\\u10a0-\\u10c5\\u10c7\\u10cd\\u10d0-\\u10fa\\u10fc-\\u1248\\u124a-\\u124d\\u1250-\\u1256\\u1258\\u125a-\\u125d\\u1260-\\u1288\\u128a-\\u128d\\u1290-\\u12b0\\u12b2-\\u12b5\\u12b8-\\u12be\\u12c0\\u12c2-\\u12c5\\u12c8-\\u12d6\\u12d8-\\u1310\\u1312-\\u1315\\u1318-\\u135a\\u1380-\\u138f\\u13a0-\\u13f5\\u13f8-\\u13fd\\u1401-\\u166c\\u166f-\\u167f\\u1681-\\u169a\\u16a0-\\u16ea\\u16ee-\\u16f8\\u1700-\\u170c\\u170e-\\u1711\\u1720-\\u1731\\u1740-\\u1751\\u1760-\\u176c\\u176e-\\u1770\\u1780-\\u17b3\\u17d7\\u17dc\\u1820-\\u1877\\u1880-\\u18a8\\u18aa\\u18b0-\\u18f5\\u1900-\\u191e\\u1950-\\u196d\\u1970-\\u1974\\u1980-\\u19ab\\u19b0-\\u19c9\\u1a00-\\u1a16\\u1a20-\\u1a54\\u1aa7\\u1b05-\\u1b33\\u1b45-\\u1b4b\\u1b83-\\u1ba0\\u1bae\\u1baf\\u1bba-\\u1be5\\u1c00-\\u1c23\\u1c4d-\\u1c4f\\u1c5a-\\u1c7d\\u1c80-\\u1c88\\u1ce9-\\u1cec\\u1cee-\\u1cf1\\u1cf5\\u1cf6\\u1d00-\\u1dbf\\u1e00-\\u1f15\\u1f18-\\u1f1d\\u1f20-\\u1f45\\u1f48-\\u1f4d\\u1f50-\\u1f57\\u1f59\\u1f5b\\u1f5d\\u1f5f-\\u1f7d\\u1f80-\\u1fb4\\u1fb6-\\u1fbc\\u1fbe\\u1fc2-\\u1fc4\\u1fc6-\\u1fcc\\u1fd0-\\u1fd3\\u1fd6-\\u1fdb\\u1fe0-\\u1fec\\u1ff2-\\u1ff4\\u1ff6-\\u1ffc\\u2071\\u207f\\u2090-\\u209c\\u2102\\u2107\\u210a-\\u2113\\u2115\\u2118-\\u211d\\u2124\\u2126\\u2128\\u212a-\\u2139\\u213c-\\u213f\\u2145-\\u2149\\u214e\\u2160-\\u2188\\u2c00-\\u2c2e\\u2c30-\\u2c5e\\u2c60-\\u2ce4\\u2ceb-\\u2cee\\u2cf2\\u2cf3\\u2d00-\\u2d25\\u2d27\\u2d2d\\u2d30-\\u2d67\\u2d6f\\u2d80-\\u2d96\\u2da0-\\u2da6\\u2da8-\\u2dae\\u2db0-\\u2db6\\u2db8-\\u2dbe\\u2dc0-\\u2dc6\\u2dc8-\\u2dce\\u2dd0-\\u2dd6\\u2dd8-\\u2dde\\u3005-\\u3007\\u3021-\\u3029\\u3031-\\u3035\\u3038-\\u303c\\u3041-\\u3096\\u309b-\\u309f\\u30a1-\\u30fa\\u30fc-\\u30ff\\u3105-\\u312d\\u3131-\\u318e\\u31a0-\\u31ba\\u31f0-\\u31ff\\u3400-\\u4db5\\u4e00-\\u9fd5\\ua000-\\ua48c\\ua4d0-\\ua4fd\\ua500-\\ua60c\\ua610-\\ua61f\\ua62a\\ua62b\\ua640-\\ua66e\\ua67f-\\ua69d\\ua6a0-\\ua6ef\\ua717-\\ua71f\\ua722-\\ua788\\ua78b-\\ua7ae\\ua7b0-\\ua7b7\\ua7f7-\\ua801\\ua803-\\ua805\\ua807-\\ua80a\\ua80c-\\ua822\\ua840-\\ua873\\ua882-\\ua8b3\\ua8f2-\\ua8f7\\ua8fb\\ua8fd\\ua90a-\\ua925\\ua930-\\ua946\\ua960-\\ua97c\\ua984-\\ua9b2\\ua9cf\\ua9e0-\\ua9e4\\ua9e6-\\ua9ef\\ua9fa-\\ua9fe\\uaa00-\\uaa28\\uaa40-\\uaa42\\uaa44-\\uaa4b\\uaa60-\\uaa76\\uaa7a\\uaa7e-\\uaaaf\\uaab1\\uaab5\\uaab6\\uaab9-\\uaabd\\uaac0\\uaac2\\uaadb-\\uaadd\\uaae0-\\uaaea\\uaaf2-\\uaaf4\\uab01-\\uab06\\uab09-\\uab0e\\uab11-\\uab16\\uab20-\\uab26\\uab28-\\uab2e\\uab30-\\uab5a\\uab5c-\\uab65\\uab70-\\uabe2\\uac00-\\ud7a3\\ud7b0-\\ud7c6\\ud7cb-\\ud7fb\\uf900-\\ufa6d\\ufa70-\\ufad9\\ufb00-\\ufb06\\ufb13-\\ufb17\\ufb1d\\ufb1f-\\ufb28\\ufb2a-\\ufb36\\ufb38-\\ufb3c\\ufb3e\\ufb40\\ufb41\\ufb43\\ufb44\\ufb46-\\ufbb1\\ufbd3-\\ufd3d\\ufd50-\\ufd8f\\ufd92-\\ufdc7\\ufdf0-\\ufdfb\\ufe70-\\ufe74\\ufe76-\\ufefc\\uff21-\\uff3a\\uff41-\\uff5a\\uff66-\\uffbe\\uffc2-\\uffc7\\uffca-\\uffcf\\uffd2-\\uffd7\\uffda-\\uffdc\\ud800\\udc00-\\ud800\\udc0b\\ud800\\udc0d-\\ud800\\udc26\\ud800\\udc28-\\ud800\\udc3a\\ud800\\udc3c\\ud800\\udc3d\\ud800\\udc3f-\\ud800\\udc4d\\ud800\\udc50-\\ud800\\udc5d\\ud800\\udc80-\\ud800\\udcfa\\ud800\\udd40-\\ud800\\udd74\\ud800\\ude80-\\ud800\\ude9c\\ud800\\udea0-\\ud800\\uded0\\ud800\\udf00-\\ud800\\udf1f\\ud800\\udf30-\\ud800\\udf4a\\ud800\\udf50-\\ud800\\udf75\\ud800\\udf80-\\ud800\\udf9d\\ud800\\udfa0-\\ud800\\udfc3\\ud800\\udfc8-\\ud800\\udfcf\\ud800\\udfd1-\\ud800\\udfd5\\ud801\\udc00-\\ud801\\udc9d\\ud801\\udcb0-\\ud801\\udcd3\\ud801\\udcd8-\\ud801\\udcfb\\ud801\\udd00-\\ud801\\udd27\\ud801\\udd30-\\ud801\\udd63\\ud801\\ude00-\\ud801\\udf36\\ud801\\udf40-\\ud801\\udf55\\ud801\\udf60-\\ud801\\udf67\\ud802\\udc00-\\ud802\\udc05\\ud802\\udc08\\ud802\\udc0a-\\ud802\\udc35\\ud802\\udc37\\ud802\\udc38\\ud802\\udc3c\\ud802\\udc3f-\\ud802\\udc55\\ud802\\udc60-\\ud802\\udc76\\ud802\\udc80-\\ud802\\udc9e\\ud802\\udce0-\\ud802\\udcf2\\ud802\\udcf4\\ud802\\udcf5\\ud802\\udd00-\\ud802\\udd15\\ud802\\udd20-\\ud802\\udd39\\ud802\\udd80-\\ud802\\uddb7\\ud802\\uddbe\\ud802\\uddbf\\ud802\\ude00\\ud802\\ude10-\\ud802\\ude13\\ud802\\ude15-\\ud802\\ude17\\ud802\\ude19-\\ud802\\ude33\\ud802\\ude60-\\ud802\\ude7c\\ud802\\ude80-\\ud802\\ude9c\\ud802\\udec0-\\ud802\\udec7\\ud802\\udec9-\\ud802\\udee4\\ud802\\udf00-\\ud802\\udf35\\ud802\\udf40-\\ud802\\udf55\\ud802\\udf60-\\ud802\\udf72\\ud802\\udf80-\\ud802\\udf91\\ud803\\udc00-\\ud803\\udc48\\ud803\\udc80-\\ud803\\udcb2\\ud803\\udcc0-\\ud803\\udcf2\\ud804\\udc03-\\ud804\\udc37\\ud804\\udc83-\\ud804\\udcaf\\ud804\\udcd0-\\ud804\\udce8\\ud804\\udd03-\\ud804\\udd26\\ud804\\udd50-\\ud804\\udd72\\ud804\\udd76\\ud804\\udd83-\\ud804\\uddb2\\ud804\\uddc1-\\ud804\\uddc4\\ud804\\uddda\\ud804\\udddc\\ud804\\ude00-\\ud804\\ude11\\ud804\\ude13-\\ud804\\ude2b\\ud804\\ude80-\\ud804\\ude86\\ud804\\ude88\\ud804\\ude8a-\\ud804\\ude8d\\ud804\\ude8f-\\ud804\\ude9d\\ud804\\ude9f-\\ud804\\udea8\\ud804\\udeb0-\\ud804\\udede\\ud804\\udf05-\\ud804\\udf0c\\ud804\\udf0f\\ud804\\udf10\\ud804\\udf13-\\ud804\\udf28\\ud804\\udf2a-\\ud804\\udf30\\ud804\\udf32\\ud804\\udf33\\ud804\\udf35-\\ud804\\udf39\\ud804\\udf3d\\ud804\\udf50\\ud804\\udf5d-\\ud804\\udf61\\ud805\\udc00-\\ud805\\udc34\\ud805\\udc47-\\ud805\\udc4a\\ud805\\udc80-\\ud805\\udcaf\\ud805\\udcc4\\ud805\\udcc5\\ud805\\udcc7\\ud805\\udd80-\\ud805\\uddae\\ud805\\uddd8-\\ud805\\udddb\\ud805\\ude00-\\ud805\\ude2f\\ud805\\ude44\\ud805\\ude80-\\ud805\\udeaa\\ud805\\udf00-\\ud805\\udf19\\ud806\\udca0-\\ud806\\udcdf\\ud806\\udcff\\ud806\\udec0-\\ud806\\udef8\\ud807\\udc00-\\ud807\\udc08\\ud807\\udc0a-\\ud807\\udc2e\\ud807\\udc40\\ud807\\udc72-\\ud807\\udc8f\\ud808\\udc00-\\ud808\\udf99\\ud809\\udc00-\\ud809\\udc6e\\ud809\\udc80-\\ud809\\udd43\\ud80c\\udc00-\\ud80d\\udc2e\\ud811\\udc00-\\ud811\\ude46\\ud81a\\udc00-\\ud81a\\ude38\\ud81a\\ude40-\\ud81a\\ude5e\\ud81a\\uded0-\\ud81a\\udeed\\ud81a\\udf00-\\ud81a\\udf2f\\ud81a\\udf40-\\ud81a\\udf43\\ud81a\\udf63-\\ud81a\\udf77\\ud81a\\udf7d-\\ud81a\\udf8f\\ud81b\\udf00-\\ud81b\\udf44\\ud81b\\udf50\\ud81b\\udf93-\\ud81b\\udf9f\\ud81b\\udfe0\\ud81c\\udc00-\\ud821\\udfec\\ud822\\udc00-\\ud822\\udef2\\ud82c\\udc00\\ud82c\\udc01\\ud82f\\udc00-\\ud82f\\udc6a\\ud82f\\udc70-\\ud82f\\udc7c\\ud82f\\udc80-\\ud82f\\udc88\\ud82f\\udc90-\\ud82f\\udc99\\ud835\\udc00-\\ud835\\udc54\\ud835\\udc56-\\ud835\\udc9c\\ud835\\udc9e\\ud835\\udc9f\\ud835\\udca2\\ud835\\udca5\\ud835\\udca6\\ud835\\udca9-\\ud835\\udcac\\ud835\\udcae-\\ud835\\udcb9\\ud835\\udcbb\\ud835\\udcbd-\\ud835\\udcc3\\ud835\\udcc5-\\ud835\\udd05\\ud835\\udd07-\\ud835\\udd0a\\ud835\\udd0d-\\ud835\\udd14\\ud835\\udd16-\\ud835\\udd1c\\ud835\\udd1e-\\ud835\\udd39\\ud835\\udd3b-\\ud835\\udd3e\\ud835\\udd40-\\ud835\\udd44\\ud835\\udd46\\ud835\\udd4a-\\ud835\\udd50\\ud835\\udd52-\\ud835\\udea5\\ud835\\udea8-\\ud835\\udec0\\ud835\\udec2-\\ud835\\udeda\\ud835\\udedc-\\ud835\\udefa\\ud835\\udefc-\\ud835\\udf14\\ud835\\udf16-\\ud835\\udf34\\ud835\\udf36-\\ud835\\udf4e\\ud835\\udf50-\\ud835\\udf6e\\ud835\\udf70-\\ud835\\udf88\\ud835\\udf8a-\\ud835\\udfa8\\ud835\\udfaa-\\ud835\\udfc2\\ud835\\udfc4-\\ud835\\udfcb\\ud83a\\udc00-\\ud83a\\udcc4\\ud83a\\udd00-\\ud83a\\udd43\\ud83b\\ude00-\\ud83b\\ude03\\ud83b\\ude05-\\ud83b\\ude1f\\ud83b\\ude21\\ud83b\\ude22\\ud83b\\ude24\\ud83b\\ude27\\ud83b\\ude29-\\ud83b\\ude32\\ud83b\\ude34-\\ud83b\\ude37\\ud83b\\ude39\\ud83b\\ude3b\\ud83b\\ude42\\ud83b\\ude47\\ud83b\\ude49\\ud83b\\ude4b\\ud83b\\ude4d-\\ud83b\\ude4f\\ud83b\\ude51\\ud83b\\ude52\\ud83b\\ude54\\ud83b\\ude57\\ud83b\\ude59\\ud83b\\ude5b\\ud83b\\ude5d\\ud83b\\ude5f\\ud83b\\ude61\\ud83b\\ude62\\ud83b\\ude64\\ud83b\\ude67-\\ud83b\\ude6a\\ud83b\\ude6c-\\ud83b\\ude72\\ud83b\\ude74-\\ud83b\\ude77\\ud83b\\ude79-\\ud83b\\ude7c\\ud83b\\ude7e\\ud83b\\ude80-\\ud83b\\ude89\\ud83b\\ude8b-\\ud83b\\ude9b\\ud83b\\udea1-\\ud83b\\udea3\\ud83b\\udea5-\\ud83b\\udea9\\ud83b\\udeab-\\ud83b\\udebb\\ud840\\udc00-\\ud869\\uded6\\ud869\\udf00-\\ud86d\\udf34\\ud86d\\udf40-\\ud86e\\udc1d\\ud86e\\udc20-\\ud873\\udea1\\ud87e\\udc00-\\ud87e\\ude1d]").asPredicate();


    static {
        for (int ch = 0; ch < 0x80; ++ch) {
            IDENTIFIER_START[ch] =
                    ch >= 0x61 && ch <= 0x7A ||  // a..z
                            ch >= 0x41 && ch <= 0x5A ||  // A..Z
                            ch == 0x24 || ch == 0x5F;    // $ (dollar) and _ (underscore)
        }
    }

    @SuppressWarnings("checkstyle:magicnumber")
    public static boolean isIdentifierStart(int ch) {
        if (ch < 0x80) {
            return IDENTIFIER_START[ch];
        }
        return isNonAsciiIdentifierStart.test(fromCodePoint(ch));
    }

    private static final boolean[] IDENTIFIER_PART = new boolean[0x80];
    // This regex is generated from scripts/make-unicode-regexen.js
    private static final Predicate<String> isNonAsciiIdentifierPart = Pattern.compile("[\\u00aa\\u00b5\\u00b7\\u00ba\\u00c0-\\u00d6\\u00d8-\\u00f6\\u00f8-\\u02c1\\u02c6-\\u02d1\\u02e0-\\u02e4\\u02ec\\u02ee\\u0300-\\u0374\\u0376\\u0377\\u037a-\\u037d\\u037f\\u0386-\\u038a\\u038c\\u038e-\\u03a1\\u03a3-\\u03f5\\u03f7-\\u0481\\u0483-\\u0487\\u048a-\\u052f\\u0531-\\u0556\\u0559\\u0561-\\u0587\\u0591-\\u05bd\\u05bf\\u05c1\\u05c2\\u05c4\\u05c5\\u05c7\\u05d0-\\u05ea\\u05f0-\\u05f2\\u0610-\\u061a\\u0620-\\u0669\\u066e-\\u06d3\\u06d5-\\u06dc\\u06df-\\u06e8\\u06ea-\\u06fc\\u06ff\\u0710-\\u074a\\u074d-\\u07b1\\u07c0-\\u07f5\\u07fa\\u0800-\\u082d\\u0840-\\u085b\\u08a0-\\u08b4\\u08b6-\\u08bd\\u08d4-\\u08e1\\u08e3-\\u0963\\u0966-\\u096f\\u0971-\\u0983\\u0985-\\u098c\\u098f\\u0990\\u0993-\\u09a8\\u09aa-\\u09b0\\u09b2\\u09b6-\\u09b9\\u09bc-\\u09c4\\u09c7\\u09c8\\u09cb-\\u09ce\\u09d7\\u09dc\\u09dd\\u09df-\\u09e3\\u09e6-\\u09f1\\u0a01-\\u0a03\\u0a05-\\u0a0a\\u0a0f\\u0a10\\u0a13-\\u0a28\\u0a2a-\\u0a30\\u0a32\\u0a33\\u0a35\\u0a36\\u0a38\\u0a39\\u0a3c\\u0a3e-\\u0a42\\u0a47\\u0a48\\u0a4b-\\u0a4d\\u0a51\\u0a59-\\u0a5c\\u0a5e\\u0a66-\\u0a75\\u0a81-\\u0a83\\u0a85-\\u0a8d\\u0a8f-\\u0a91\\u0a93-\\u0aa8\\u0aaa-\\u0ab0\\u0ab2\\u0ab3\\u0ab5-\\u0ab9\\u0abc-\\u0ac5\\u0ac7-\\u0ac9\\u0acb-\\u0acd\\u0ad0\\u0ae0-\\u0ae3\\u0ae6-\\u0aef\\u0af9\\u0b01-\\u0b03\\u0b05-\\u0b0c\\u0b0f\\u0b10\\u0b13-\\u0b28\\u0b2a-\\u0b30\\u0b32\\u0b33\\u0b35-\\u0b39\\u0b3c-\\u0b44\\u0b47\\u0b48\\u0b4b-\\u0b4d\\u0b56\\u0b57\\u0b5c\\u0b5d\\u0b5f-\\u0b63\\u0b66-\\u0b6f\\u0b71\\u0b82\\u0b83\\u0b85-\\u0b8a\\u0b8e-\\u0b90\\u0b92-\\u0b95\\u0b99\\u0b9a\\u0b9c\\u0b9e\\u0b9f\\u0ba3\\u0ba4\\u0ba8-\\u0baa\\u0bae-\\u0bb9\\u0bbe-\\u0bc2\\u0bc6-\\u0bc8\\u0bca-\\u0bcd\\u0bd0\\u0bd7\\u0be6-\\u0bef\\u0c00-\\u0c03\\u0c05-\\u0c0c\\u0c0e-\\u0c10\\u0c12-\\u0c28\\u0c2a-\\u0c39\\u0c3d-\\u0c44\\u0c46-\\u0c48\\u0c4a-\\u0c4d\\u0c55\\u0c56\\u0c58-\\u0c5a\\u0c60-\\u0c63\\u0c66-\\u0c6f\\u0c80-\\u0c83\\u0c85-\\u0c8c\\u0c8e-\\u0c90\\u0c92-\\u0ca8\\u0caa-\\u0cb3\\u0cb5-\\u0cb9\\u0cbc-\\u0cc4\\u0cc6-\\u0cc8\\u0cca-\\u0ccd\\u0cd5\\u0cd6\\u0cde\\u0ce0-\\u0ce3\\u0ce6-\\u0cef\\u0cf1\\u0cf2\\u0d01-\\u0d03\\u0d05-\\u0d0c\\u0d0e-\\u0d10\\u0d12-\\u0d3a\\u0d3d-\\u0d44\\u0d46-\\u0d48\\u0d4a-\\u0d4e\\u0d54-\\u0d57\\u0d5f-\\u0d63\\u0d66-\\u0d6f\\u0d7a-\\u0d7f\\u0d82\\u0d83\\u0d85-\\u0d96\\u0d9a-\\u0db1\\u0db3-\\u0dbb\\u0dbd\\u0dc0-\\u0dc6\\u0dca\\u0dcf-\\u0dd4\\u0dd6\\u0dd8-\\u0ddf\\u0de6-\\u0def\\u0df2\\u0df3\\u0e01-\\u0e3a\\u0e40-\\u0e4e\\u0e50-\\u0e59\\u0e81\\u0e82\\u0e84\\u0e87\\u0e88\\u0e8a\\u0e8d\\u0e94-\\u0e97\\u0e99-\\u0e9f\\u0ea1-\\u0ea3\\u0ea5\\u0ea7\\u0eaa\\u0eab\\u0ead-\\u0eb9\\u0ebb-\\u0ebd\\u0ec0-\\u0ec4\\u0ec6\\u0ec8-\\u0ecd\\u0ed0-\\u0ed9\\u0edc-\\u0edf\\u0f00\\u0f18\\u0f19\\u0f20-\\u0f29\\u0f35\\u0f37\\u0f39\\u0f3e-\\u0f47\\u0f49-\\u0f6c\\u0f71-\\u0f84\\u0f86-\\u0f97\\u0f99-\\u0fbc\\u0fc6\\u1000-\\u1049\\u1050-\\u109d\\u10a0-\\u10c5\\u10c7\\u10cd\\u10d0-\\u10fa\\u10fc-\\u1248\\u124a-\\u124d\\u1250-\\u1256\\u1258\\u125a-\\u125d\\u1260-\\u1288\\u128a-\\u128d\\u1290-\\u12b0\\u12b2-\\u12b5\\u12b8-\\u12be\\u12c0\\u12c2-\\u12c5\\u12c8-\\u12d6\\u12d8-\\u1310\\u1312-\\u1315\\u1318-\\u135a\\u135d-\\u135f\\u1369-\\u1371\\u1380-\\u138f\\u13a0-\\u13f5\\u13f8-\\u13fd\\u1401-\\u166c\\u166f-\\u167f\\u1681-\\u169a\\u16a0-\\u16ea\\u16ee-\\u16f8\\u1700-\\u170c\\u170e-\\u1714\\u1720-\\u1734\\u1740-\\u1753\\u1760-\\u176c\\u176e-\\u1770\\u1772\\u1773\\u1780-\\u17d3\\u17d7\\u17dc\\u17dd\\u17e0-\\u17e9\\u180b-\\u180d\\u1810-\\u1819\\u1820-\\u1877\\u1880-\\u18aa\\u18b0-\\u18f5\\u1900-\\u191e\\u1920-\\u192b\\u1930-\\u193b\\u1946-\\u196d\\u1970-\\u1974\\u1980-\\u19ab\\u19b0-\\u19c9\\u19d0-\\u19da\\u1a00-\\u1a1b\\u1a20-\\u1a5e\\u1a60-\\u1a7c\\u1a7f-\\u1a89\\u1a90-\\u1a99\\u1aa7\\u1ab0-\\u1abd\\u1b00-\\u1b4b\\u1b50-\\u1b59\\u1b6b-\\u1b73\\u1b80-\\u1bf3\\u1c00-\\u1c37\\u1c40-\\u1c49\\u1c4d-\\u1c7d\\u1c80-\\u1c88\\u1cd0-\\u1cd2\\u1cd4-\\u1cf6\\u1cf8\\u1cf9\\u1d00-\\u1df5\\u1dfb-\\u1f15\\u1f18-\\u1f1d\\u1f20-\\u1f45\\u1f48-\\u1f4d\\u1f50-\\u1f57\\u1f59\\u1f5b\\u1f5d\\u1f5f-\\u1f7d\\u1f80-\\u1fb4\\u1fb6-\\u1fbc\\u1fbe\\u1fc2-\\u1fc4\\u1fc6-\\u1fcc\\u1fd0-\\u1fd3\\u1fd6-\\u1fdb\\u1fe0-\\u1fec\\u1ff2-\\u1ff4\\u1ff6-\\u1ffc\\u200c\\u200d\\u203f\\u2040\\u2054\\u2071\\u207f\\u2090-\\u209c\\u20d0-\\u20dc\\u20e1\\u20e5-\\u20f0\\u2102\\u2107\\u210a-\\u2113\\u2115\\u2118-\\u211d\\u2124\\u2126\\u2128\\u212a-\\u2139\\u213c-\\u213f\\u2145-\\u2149\\u214e\\u2160-\\u2188\\u2c00-\\u2c2e\\u2c30-\\u2c5e\\u2c60-\\u2ce4\\u2ceb-\\u2cf3\\u2d00-\\u2d25\\u2d27\\u2d2d\\u2d30-\\u2d67\\u2d6f\\u2d7f-\\u2d96\\u2da0-\\u2da6\\u2da8-\\u2dae\\u2db0-\\u2db6\\u2db8-\\u2dbe\\u2dc0-\\u2dc6\\u2dc8-\\u2dce\\u2dd0-\\u2dd6\\u2dd8-\\u2dde\\u2de0-\\u2dff\\u3005-\\u3007\\u3021-\\u302f\\u3031-\\u3035\\u3038-\\u303c\\u3041-\\u3096\\u3099-\\u309f\\u30a1-\\u30fa\\u30fc-\\u30ff\\u3105-\\u312d\\u3131-\\u318e\\u31a0-\\u31ba\\u31f0-\\u31ff\\u3400-\\u4db5\\u4e00-\\u9fd5\\ua000-\\ua48c\\ua4d0-\\ua4fd\\ua500-\\ua60c\\ua610-\\ua62b\\ua640-\\ua66f\\ua674-\\ua67d\\ua67f-\\ua6f1\\ua717-\\ua71f\\ua722-\\ua788\\ua78b-\\ua7ae\\ua7b0-\\ua7b7\\ua7f7-\\ua827\\ua840-\\ua873\\ua880-\\ua8c5\\ua8d0-\\ua8d9\\ua8e0-\\ua8f7\\ua8fb\\ua8fd\\ua900-\\ua92d\\ua930-\\ua953\\ua960-\\ua97c\\ua980-\\ua9c0\\ua9cf-\\ua9d9\\ua9e0-\\ua9fe\\uaa00-\\uaa36\\uaa40-\\uaa4d\\uaa50-\\uaa59\\uaa60-\\uaa76\\uaa7a-\\uaac2\\uaadb-\\uaadd\\uaae0-\\uaaef\\uaaf2-\\uaaf6\\uab01-\\uab06\\uab09-\\uab0e\\uab11-\\uab16\\uab20-\\uab26\\uab28-\\uab2e\\uab30-\\uab5a\\uab5c-\\uab65\\uab70-\\uabea\\uabec\\uabed\\uabf0-\\uabf9\\uac00-\\ud7a3\\ud7b0-\\ud7c6\\ud7cb-\\ud7fb\\uf900-\\ufa6d\\ufa70-\\ufad9\\ufb00-\\ufb06\\ufb13-\\ufb17\\ufb1d-\\ufb28\\ufb2a-\\ufb36\\ufb38-\\ufb3c\\ufb3e\\ufb40\\ufb41\\ufb43\\ufb44\\ufb46-\\ufbb1\\ufbd3-\\ufd3d\\ufd50-\\ufd8f\\ufd92-\\ufdc7\\ufdf0-\\ufdfb\\ufe00-\\ufe0f\\ufe20-\\ufe2f\\ufe33\\ufe34\\ufe4d-\\ufe4f\\ufe70-\\ufe74\\ufe76-\\ufefc\\uff10-\\uff19\\uff21-\\uff3a\\uff3f\\uff41-\\uff5a\\uff66-\\uffbe\\uffc2-\\uffc7\\uffca-\\uffcf\\uffd2-\\uffd7\\uffda-\\uffdc\\ud800\\udc00-\\ud800\\udc0b\\ud800\\udc0d-\\ud800\\udc26\\ud800\\udc28-\\ud800\\udc3a\\ud800\\udc3c\\ud800\\udc3d\\ud800\\udc3f-\\ud800\\udc4d\\ud800\\udc50-\\ud800\\udc5d\\ud800\\udc80-\\ud800\\udcfa\\ud800\\udd40-\\ud800\\udd74\\ud800\\uddfd\\ud800\\ude80-\\ud800\\ude9c\\ud800\\udea0-\\ud800\\uded0\\ud800\\udee0\\ud800\\udf00-\\ud800\\udf1f\\ud800\\udf30-\\ud800\\udf4a\\ud800\\udf50-\\ud800\\udf7a\\ud800\\udf80-\\ud800\\udf9d\\ud800\\udfa0-\\ud800\\udfc3\\ud800\\udfc8-\\ud800\\udfcf\\ud800\\udfd1-\\ud800\\udfd5\\ud801\\udc00-\\ud801\\udc9d\\ud801\\udca0-\\ud801\\udca9\\ud801\\udcb0-\\ud801\\udcd3\\ud801\\udcd8-\\ud801\\udcfb\\ud801\\udd00-\\ud801\\udd27\\ud801\\udd30-\\ud801\\udd63\\ud801\\ude00-\\ud801\\udf36\\ud801\\udf40-\\ud801\\udf55\\ud801\\udf60-\\ud801\\udf67\\ud802\\udc00-\\ud802\\udc05\\ud802\\udc08\\ud802\\udc0a-\\ud802\\udc35\\ud802\\udc37\\ud802\\udc38\\ud802\\udc3c\\ud802\\udc3f-\\ud802\\udc55\\ud802\\udc60-\\ud802\\udc76\\ud802\\udc80-\\ud802\\udc9e\\ud802\\udce0-\\ud802\\udcf2\\ud802\\udcf4\\ud802\\udcf5\\ud802\\udd00-\\ud802\\udd15\\ud802\\udd20-\\ud802\\udd39\\ud802\\udd80-\\ud802\\uddb7\\ud802\\uddbe\\ud802\\uddbf\\ud802\\ude00-\\ud802\\ude03\\ud802\\ude05\\ud802\\ude06\\ud802\\ude0c-\\ud802\\ude13\\ud802\\ude15-\\ud802\\ude17\\ud802\\ude19-\\ud802\\ude33\\ud802\\ude38-\\ud802\\ude3a\\ud802\\ude3f\\ud802\\ude60-\\ud802\\ude7c\\ud802\\ude80-\\ud802\\ude9c\\ud802\\udec0-\\ud802\\udec7\\ud802\\udec9-\\ud802\\udee6\\ud802\\udf00-\\ud802\\udf35\\ud802\\udf40-\\ud802\\udf55\\ud802\\udf60-\\ud802\\udf72\\ud802\\udf80-\\ud802\\udf91\\ud803\\udc00-\\ud803\\udc48\\ud803\\udc80-\\ud803\\udcb2\\ud803\\udcc0-\\ud803\\udcf2\\ud804\\udc00-\\ud804\\udc46\\ud804\\udc66-\\ud804\\udc6f\\ud804\\udc7f-\\ud804\\udcba\\ud804\\udcd0-\\ud804\\udce8\\ud804\\udcf0-\\ud804\\udcf9\\ud804\\udd00-\\ud804\\udd34\\ud804\\udd36-\\ud804\\udd3f\\ud804\\udd50-\\ud804\\udd73\\ud804\\udd76\\ud804\\udd80-\\ud804\\uddc4\\ud804\\uddca-\\ud804\\uddcc\\ud804\\uddd0-\\ud804\\uddda\\ud804\\udddc\\ud804\\ude00-\\ud804\\ude11\\ud804\\ude13-\\ud804\\ude37\\ud804\\ude3e\\ud804\\ude80-\\ud804\\ude86\\ud804\\ude88\\ud804\\ude8a-\\ud804\\ude8d\\ud804\\ude8f-\\ud804\\ude9d\\ud804\\ude9f-\\ud804\\udea8\\ud804\\udeb0-\\ud804\\udeea\\ud804\\udef0-\\ud804\\udef9\\ud804\\udf00-\\ud804\\udf03\\ud804\\udf05-\\ud804\\udf0c\\ud804\\udf0f\\ud804\\udf10\\ud804\\udf13-\\ud804\\udf28\\ud804\\udf2a-\\ud804\\udf30\\ud804\\udf32\\ud804\\udf33\\ud804\\udf35-\\ud804\\udf39\\ud804\\udf3c-\\ud804\\udf44\\ud804\\udf47\\ud804\\udf48\\ud804\\udf4b-\\ud804\\udf4d\\ud804\\udf50\\ud804\\udf57\\ud804\\udf5d-\\ud804\\udf63\\ud804\\udf66-\\ud804\\udf6c\\ud804\\udf70-\\ud804\\udf74\\ud805\\udc00-\\ud805\\udc4a\\ud805\\udc50-\\ud805\\udc59\\ud805\\udc80-\\ud805\\udcc5\\ud805\\udcc7\\ud805\\udcd0-\\ud805\\udcd9\\ud805\\udd80-\\ud805\\uddb5\\ud805\\uddb8-\\ud805\\uddc0\\ud805\\uddd8-\\ud805\\udddd\\ud805\\ude00-\\ud805\\ude40\\ud805\\ude44\\ud805\\ude50-\\ud805\\ude59\\ud805\\ude80-\\ud805\\udeb7\\ud805\\udec0-\\ud805\\udec9\\ud805\\udf00-\\ud805\\udf19\\ud805\\udf1d-\\ud805\\udf2b\\ud805\\udf30-\\ud805\\udf39\\ud806\\udca0-\\ud806\\udce9\\ud806\\udcff\\ud806\\udec0-\\ud806\\udef8\\ud807\\udc00-\\ud807\\udc08\\ud807\\udc0a-\\ud807\\udc36\\ud807\\udc38-\\ud807\\udc40\\ud807\\udc50-\\ud807\\udc59\\ud807\\udc72-\\ud807\\udc8f\\ud807\\udc92-\\ud807\\udca7\\ud807\\udca9-\\ud807\\udcb6\\ud808\\udc00-\\ud808\\udf99\\ud809\\udc00-\\ud809\\udc6e\\ud809\\udc80-\\ud809\\udd43\\ud80c\\udc00-\\ud80d\\udc2e\\ud811\\udc00-\\ud811\\ude46\\ud81a\\udc00-\\ud81a\\ude38\\ud81a\\ude40-\\ud81a\\ude5e\\ud81a\\ude60-\\ud81a\\ude69\\ud81a\\uded0-\\ud81a\\udeed\\ud81a\\udef0-\\ud81a\\udef4\\ud81a\\udf00-\\ud81a\\udf36\\ud81a\\udf40-\\ud81a\\udf43\\ud81a\\udf50-\\ud81a\\udf59\\ud81a\\udf63-\\ud81a\\udf77\\ud81a\\udf7d-\\ud81a\\udf8f\\ud81b\\udf00-\\ud81b\\udf44\\ud81b\\udf50-\\ud81b\\udf7e\\ud81b\\udf8f-\\ud81b\\udf9f\\ud81b\\udfe0\\ud81c\\udc00-\\ud821\\udfec\\ud822\\udc00-\\ud822\\udef2\\ud82c\\udc00\\ud82c\\udc01\\ud82f\\udc00-\\ud82f\\udc6a\\ud82f\\udc70-\\ud82f\\udc7c\\ud82f\\udc80-\\ud82f\\udc88\\ud82f\\udc90-\\ud82f\\udc99\\ud82f\\udc9d\\ud82f\\udc9e\\ud834\\udd65-\\ud834\\udd69\\ud834\\udd6d-\\ud834\\udd72\\ud834\\udd7b-\\ud834\\udd82\\ud834\\udd85-\\ud834\\udd8b\\ud834\\uddaa-\\ud834\\uddad\\ud834\\ude42-\\ud834\\ude44\\ud835\\udc00-\\ud835\\udc54\\ud835\\udc56-\\ud835\\udc9c\\ud835\\udc9e\\ud835\\udc9f\\ud835\\udca2\\ud835\\udca5\\ud835\\udca6\\ud835\\udca9-\\ud835\\udcac\\ud835\\udcae-\\ud835\\udcb9\\ud835\\udcbb\\ud835\\udcbd-\\ud835\\udcc3\\ud835\\udcc5-\\ud835\\udd05\\ud835\\udd07-\\ud835\\udd0a\\ud835\\udd0d-\\ud835\\udd14\\ud835\\udd16-\\ud835\\udd1c\\ud835\\udd1e-\\ud835\\udd39\\ud835\\udd3b-\\ud835\\udd3e\\ud835\\udd40-\\ud835\\udd44\\ud835\\udd46\\ud835\\udd4a-\\ud835\\udd50\\ud835\\udd52-\\ud835\\udea5\\ud835\\udea8-\\ud835\\udec0\\ud835\\udec2-\\ud835\\udeda\\ud835\\udedc-\\ud835\\udefa\\ud835\\udefc-\\ud835\\udf14\\ud835\\udf16-\\ud835\\udf34\\ud835\\udf36-\\ud835\\udf4e\\ud835\\udf50-\\ud835\\udf6e\\ud835\\udf70-\\ud835\\udf88\\ud835\\udf8a-\\ud835\\udfa8\\ud835\\udfaa-\\ud835\\udfc2\\ud835\\udfc4-\\ud835\\udfcb\\ud835\\udfce-\\ud835\\udfff\\ud836\\ude00-\\ud836\\ude36\\ud836\\ude3b-\\ud836\\ude6c\\ud836\\ude75\\ud836\\ude84\\ud836\\ude9b-\\ud836\\ude9f\\ud836\\udea1-\\ud836\\udeaf\\ud838\\udc00-\\ud838\\udc06\\ud838\\udc08-\\ud838\\udc18\\ud838\\udc1b-\\ud838\\udc21\\ud838\\udc23\\ud838\\udc24\\ud838\\udc26-\\ud838\\udc2a\\ud83a\\udc00-\\ud83a\\udcc4\\ud83a\\udcd0-\\ud83a\\udcd6\\ud83a\\udd00-\\ud83a\\udd4a\\ud83a\\udd50-\\ud83a\\udd59\\ud83b\\ude00-\\ud83b\\ude03\\ud83b\\ude05-\\ud83b\\ude1f\\ud83b\\ude21\\ud83b\\ude22\\ud83b\\ude24\\ud83b\\ude27\\ud83b\\ude29-\\ud83b\\ude32\\ud83b\\ude34-\\ud83b\\ude37\\ud83b\\ude39\\ud83b\\ude3b\\ud83b\\ude42\\ud83b\\ude47\\ud83b\\ude49\\ud83b\\ude4b\\ud83b\\ude4d-\\ud83b\\ude4f\\ud83b\\ude51\\ud83b\\ude52\\ud83b\\ude54\\ud83b\\ude57\\ud83b\\ude59\\ud83b\\ude5b\\ud83b\\ude5d\\ud83b\\ude5f\\ud83b\\ude61\\ud83b\\ude62\\ud83b\\ude64\\ud83b\\ude67-\\ud83b\\ude6a\\ud83b\\ude6c-\\ud83b\\ude72\\ud83b\\ude74-\\ud83b\\ude77\\ud83b\\ude79-\\ud83b\\ude7c\\ud83b\\ude7e\\ud83b\\ude80-\\ud83b\\ude89\\ud83b\\ude8b-\\ud83b\\ude9b\\ud83b\\udea1-\\ud83b\\udea3\\ud83b\\udea5-\\ud83b\\udea9\\ud83b\\udeab-\\ud83b\\udebb\\ud840\\udc00-\\ud869\\uded6\\ud869\\udf00-\\ud86d\\udf34\\ud86d\\udf40-\\ud86e\\udc1d\\ud86e\\udc20-\\ud873\\udea1\\ud87e\\udc00-\\ud87e\\ude1d\\udb40\\udd00-\\udb40\\uddef]").asPredicate();

    static {
        for (int ch = 0; ch < 0x80; ++ch) {
            IDENTIFIER_PART[ch] =
                    ch >= 0x61 && ch <= 0x7A ||  // a..z
                            ch >= 0x41 && ch <= 0x5A ||  // A..Z
                            ch >= 0x30 && ch <= 0x39 ||  // 0..9
                            ch == 0x24 || ch == 0x5F;    // $ (dollar) and _ (underscore)
        }
    }

    @SuppressWarnings("checkstyle:magicnumber")
    public static boolean isIdentifierPart(int ch) {
        if (ch < 0x80) {
            return IDENTIFIER_PART[ch];
        }
        return isNonAsciiIdentifierPart.test(fromCodePoint(ch));
    }

    public static int getHexValue(char rune) {
        if ('0' <= rune && rune <= '9') {
            return rune - '0';
        }

        if ('a' <= rune && rune <= 'f') {
            return rune - 'a' + 10;
        }
        if ('A' <= rune && rune <= 'F') {
            return rune - 'A' + 10;
        }
        return -1;
    }

    public static boolean isValidNumber(@Nonnull String source) {
        int index = 0;
        int length = source.length();
        char[] chs = source.toCharArray();

        char ch = chs[index];
        if (ch == '0') {
            index++;
            if (index < length) {
                ch = chs[index];
                if (ch == 'x' || ch == 'X') {
                    return isValidHexLiteral(chs);
                } else if ('0' <= ch && ch <= '9') {
                    return isValidOctalLiteral(chs);
                }
            } else {
                return true;
            }
        } else if ('1' <= ch && ch <= '9') {
            ch = chs[index];
            while ('0' <= ch && ch <= '9') {
                index++;
                if (index == length) {
                    return true;
                }
                ch = chs[index];
            }
        } else if (ch != '.') {
            return false;
        }

        if (ch == '.') {
            index++;
            if (index == length) {
                return true;
            }

            ch = chs[index];
            while ('0' <= ch && ch <= '9') {
                index++;
                if (index == length) {
                    return true;
                }
                ch = chs[index];
            }
        }

        // EOF not reached here
        if (ch == 'e' || ch == 'E') {
            index++;
            if (index == length) {
                return false;
            }

            ch = chs[index];
            if (ch == '+' || ch == '-') {
                index++;
                if (index == length) {
                    return false;
                }
                ch = chs[index];
            }

            if ('0' <= ch && ch <= '9') {
                while ('0' <= ch && ch <= '9') {
                    index++;
                    if (index == length) {
                        return true;
                    }
                    ch = chs[index];
                }
            } else {
                return false;
            }
        }

        return index != length;
    }

    private static boolean isValidOctalLiteral(char[] chs) {
        for (int i = 1; i < chs.length; i++) {
            if (chs[i] < '0' || chs[i] > '7') {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidHexLiteral(char[] chs) {
        for (int i = 1; i < chs.length; i++) {
            if (getHexValue(chs[i]) < 0) {
                return false;
            }
        }
        return true;
    }

}
