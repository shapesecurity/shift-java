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

package com.shapesecurity.laserbat.js.parser;

public interface ErrorMessages {
  String UNEXPECTED_TOKEN = "Unexpected token %s";
  String UNEXPECTED_ILLEGAL_TOKEN = "Unexpected token ILLEGAL";
  String UNEXPECTED_NUMBER = "Unexpected number";
  String UNEXPECTED_STRING = "Unexpected string";
  String UNEXPECTED_IDENTIFIER = "Unexpected identifier";
  String UNEXPECTED_RESERVED_WORD = "Unexpected reserved word";
  String UNEXPECTED_EOS = "Unexpected end of input";
  String NEWLINE_AFTER_THROW = "Illegal newline after throw";
  String INVALID_REGULAR_EXPRESSION = "Invalid regular expression";
  String UNTERMINATED_REG_EXP = "Invalid regular expression: missing /";
  String INVALID_LHS_IN_ASSIGNMENT = "Invalid left-hand side in assignment";
  String INVALID_LHS_IN_FOR_IN = "Invalid left-hand side in for-in";
  String INVALID_PROPERTY_NAME = "Property name in object literal must be identifier, string literal or number literal";
  String MULTIPLE_DEFAULTS_IN_SWITCH = "More than one default clause in switch statement";
  String NO_CATCH_OR_FINALLY = "Missing catch or finally after try";
  String UNKNOWN_LABEL = "Undefined label '%s'";
  String LABEL_REDECLARATION = "Label '%s' has already been declared";
  String ILLEGAL_CONTINUE = "Illegal continue statement";
  String ILLEGAL_BREAK = "Illegal break statement";
  String ILLEGAL_RETURN = "Illegal return statement";
  String STRICT_MODE_WITH = "Strict mode code may not include a with statement";
  String STRICT_CATCH_VARIABLE = "Catch variable may not be eval or arguments in strict mode";
  String STRICT_VAR_NAME = "Variable name may not be eval or arguments in strict mode";
  String STRICT_PARAM_NAME = "Parameter name eval or arguments is not allowed in strict mode";
  String STRICT_PARAM_DUPE = "Strict mode function may not have duplicate parameter names";
  String STRICT_FUNCTION_NAME = "Function name may not be eval or arguments in strict mode";
  String STRICT_OCTAL_LITERAL = "Octal literals are not allowed in strict mode.";
  String STRICT_DELETE = "Delete of an unqualified identifier in strict mode.";
  String STRICT_DUPLICATE_PROPERTY = "Duplicate data property in object literal not allowed in strict mode";
  String ACCESSOR_DATA_PROPERTY = "Object literal may not have data and accessor property with the same name";
  String ACCESSOR_GET_SET = "Object literal may not have multiple get/set accessors with the same name";
  String STRICT_LHS_ASSIGNMENT = "Assignment to eval or arguments is not allowed in strict mode";
  String STRICT_LHS_POSTFIX = "Postfix increment/decrement may not have eval or arguments operand in strict mode";
  String STRICT_LHS_PREFIX = "Prefix increment/decrement may not have eval or arguments operand in strict mode";
  String STRICT_RESERVED_WORD = "Use of future reserved word in strict mode";
}
