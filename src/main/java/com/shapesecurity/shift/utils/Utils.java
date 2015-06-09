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

import com.shapesecurity.functional.data.ImmutableList;

import java.util.HashSet;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("MagicNumber")
public final class Utils {
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

  // static only
  private Utils() {
  }

  public static boolean isRestrictedWord(@NotNull String name) {
    return "eval".equals(name) || "arguments".equals(name);
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

  public static boolean areUniqueNames(@NotNull ImmutableList<String> list) {
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

  public static boolean isStrictModeReservedWordES5(@NotNull String word) {
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
    return (ch == 0x20) || (ch == 0x09) || (ch == 0x0B) || (ch == 0x0C) ||
        (ch == 0xA0) || ch >= 0x1680 && (ch == 0x1680 || ch == 0x180E ||
        0x2000 <= ch && ch <= 0x200A ||
        ch == 0x202F || ch == 0x205F ||
        ch == 0x3000 || ch == 0xFEFF);
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

  public static boolean isValidNumber(@NotNull String source) {
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
