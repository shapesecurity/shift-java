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

package com.shapesecurity.shift.utils;

import com.shapesecurity.functional.data.List;
import com.shapesecurity.shift.ast.Identifier;

import java.util.Arrays;
import java.util.HashSet;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("MagicNumber")
public final class Utils {
  private static final long WHITESPACE1 = 0x20;
  private static final long WHITESPACE2 = 0x09;
  private static final long WHITESPACE3 = 0x0B;
  private static final long WHITESPACE4 = 0x0C;
  private static final long WHITESPACE5 = 0xA0;
  private static final long WHITESPACE6 = 0x1680;
  private static final long WHITESPACE7 = 0x1680;
  private static final long WHITESPACE8 = 0x180E;
  private static final long WHITESPACE9 = 0x2000;
  private static final long WHITESPACE10 = 0x200A;
  private static final long WHITESPACE11 = 0x202F;
  private static final long WHITESPACE12 = 0x205F;
  private static final long WHITESPACE13 = 0x3000;
  private static final long WHITESPACE14 = 0xFEFF;
  // Lu, Ll, Lt, Lm, Lo, Nl,
  // Mn, Mc, Nd, Pc
  private final static int IDENT_PART_MASK = ((1 << Character.UPPERCASE_LETTER) |
      (1 << Character.LOWERCASE_LETTER) |
      (1 << Character.TITLECASE_LETTER) |
      (1 << Character.MODIFIER_LETTER) |
      (1 << Character.OTHER_LETTER) |
      (1 << Character.NON_SPACING_MARK) |
      (1 << Character.COMBINING_SPACING_MARK) |
      (1 << Character.DECIMAL_DIGIT_NUMBER) |
      (1 << Character.LETTER_NUMBER) |
      (1 << Character.CONNECTOR_PUNCTUATION));
  @NotNull
  private static final HashSet<String> STRICT_MODE_RESERVED_WORD_ES5 = new HashSet<>(Arrays.asList("implements",
      "interface", "package", "private", "protected", "public", "static", "yield", "false", "null", "true", "let", "if",
      "in", "do", "var", "for", "new", "try", "this", "else", "case", "void", "with", "enum", "while", "break", "catch",
      "throw", "const", "class", "super", "return", "typeof", "delete", "switch", "export", "import", "default",
      "finally", "extends", "function", "continue", "debugger", "instanceof"));

  // static only
  private Utils() {
  }

  public static boolean isRestrictedWord(@NotNull String name) {
    return "eval".equals(name) || "arguments".equals(name);
  }

  public static boolean isStrictModeReservedWordES5(@NotNull String name) {
    return STRICT_MODE_RESERVED_WORD_ES5.contains(name);
  }

  public static boolean isValidIdentifierName(@NotNull String name) {
    if (name.isEmpty()) {
      return false;
    }
    if (!isIdentifierStart(name.charAt(0))) {
      return false;
    }
    for (int i = 1; i < name.length(); i++) {
      if (!isIdentifierPart(name.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public static boolean areUniqueNames(@NotNull List<Identifier> list) {
    HashSet<String> paramSet = new HashSet<>();
    for (Identifier item : list) {
      if (paramSet.contains(item.name)) {
        return false;
      } else {
        paramSet.add(item.name);
      }
    }
    return true;
  }

  @NotNull
  @SuppressWarnings("checkstyle:magicnumber")
  public static String escapeStringLiteral(@NotNull String stringValue) {
    StringBuilder result = new StringBuilder();
    result.append('"');
    char[] chars = stringValue.toCharArray();
    for (char ch : chars) {
      switch (ch) {
      case '\b':
        result.append("\\b");
        break;
      case '\t':
        result.append("\\t");
        break;
      case '\n':
        result.append("\\n");
        break;
      case '\u000B':
        result.append("\\v");
        break;
      case '\u000C':
        result.append("\\f");
        break;
      case '\r':
        result.append("\\r");
        break;
      case '\"':
        result.append("\\\"");
        break;
      case '\\':
        result.append("\\\\");
        break;
      case '\u2028':
        result.append("\\u2028");
        break;
      case '\u2029':
        result.append("\\u2029");
        break;
      default:
        result.append(ch);
        break;
      }
    }
    result.append('"');
    return result.toString();
  }

  public static boolean isReservedWordES5(@NotNull String word) {
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

  public static boolean isStrictModeReservedWordES6(@NotNull String word) {
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

  public static boolean isDecimalDigit(char ch) {
    return '0' <= ch && ch <= '9';
  }

  // 7.3 Line Terminators
  public static boolean isLineTerminator(char ch) {
    return (ch == '\r') || (ch == '\n') || (ch == '\u2028') || (ch == '\u2029');
  }

  public static boolean isWhitespace(char ch) {
    return (ch == WHITESPACE1) || (ch == WHITESPACE2) || (ch == WHITESPACE3) || (ch == WHITESPACE4) ||
        (ch == WHITESPACE5) || ch >= WHITESPACE6 && (ch == WHITESPACE7 || ch == WHITESPACE8 ||
        WHITESPACE9 <= ch && ch <= WHITESPACE10 ||
        ch == WHITESPACE11 || ch == WHITESPACE12 ||
        ch == WHITESPACE13 || ch == WHITESPACE14);
  }

  @SuppressWarnings("checkstyle:magicnumber")
  public static boolean isIdentifierStart(char ch) {
    if (ch < 0x80) {
      return Character.isJavaIdentifierStart(ch);
    }

    // Lu, Ll, Lt, Lm, Lo, Nl
    return Character.isAlphabetic(ch);
  }

  @SuppressWarnings("checkstyle:magicnumber")
  public static boolean isIdentifierPart(char ch) {
    if (ch < 0x80) {
      return Character.isJavaIdentifierPart(ch);
    }
    return ((IDENT_PART_MASK >> Character.getType(ch)) & 1) != 0 || ch == 0x200C || ch == 0x200D;
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
}
