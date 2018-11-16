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

package com.shapesecurity.shift.es2017.parser;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.F2;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.BreakStatement;
import com.shapesecurity.shift.es2017.ast.ContinueStatement;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.LabeledStatement;
import com.shapesecurity.shift.es2017.ast.MemberExpression;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Super;
import com.shapesecurity.shift.es2017.utils.Utils;

interface ErrorMessages {

    // error messages in error.js
    String UNEXPECTED_TOKEN = "Unexpected token \"%s\""; // TODO actual string escaping
    String INVALID_TOKEN_CONTEXT = "\"%s\" may not be used as an identifier in this context";
    String UNEXPECTED_ILLEGAL_TOKEN = "Unexpected %s";
    String UNEXPECTED_NUMBER = "Unexpected number";
    String UNEXPECTED_STRING = "Unexpected string";
    String UNEXPECTED_IDENTIFIER = "Unexpected identifier";
    String UNEXPECTED_RESERVED_WORD = "Unexpected reserved word";
    String UNEXPECTED_TEMPLATE = "Unexpected template";
    String UNEXPECTED_EOS = "Unexpected end of input";
    String NEWLINE_AFTER_THROW = "Illegal newline after throw";
    String NEWLINE_AFTER_ARROW_PARAMS = "Illegal newline after arrow parameters";
    String UNTERMINATED_REGEXP = "Invalid regular expression: missing /";
    String INVALID_REGEXP_FLAGS = "Invalid regular expression flags";
    String INVALID_LHS_IN_ASSIGNMENT = "Invalid left-hand side in assignment";
    String INVALID_LHS_IN_FOR_IN = "Invalid left-hand side in for-in";
    String INVALID_LHS_IN_FOR_OF = "Invalid left-hand side in for-of";
    String MULTIPLE_DEFAULTS_IN_SWITCH = "More than one default clause in switch statement";
    String NO_CATCH_OR_FINALLY = "Missing catch or finally after try";
    String ILLEGAL_RETURN = "Illegal return statement";
    String ILLEGAL_ARROW_FUNCTION_PARAMS = "Illegal arrow function parameter list";
    String INVALID_VAR_INIT_FOR_IN = "Invalid variable declaration in for-in statement";
    String INVALID_VAR_INIT_FOR_OF = "Invalid variable declaration in for-of statement";
    String ILLEGAL_PROPERTY = "Illegal property initializer";
    String UNEXPECTED_ARROW = "Arrows may not appear in this position";
    String UNINITIALIZED_BINDINGPATTERN_IN_FOR_INIT = "Binding pattern appears without initializer in for statement init";
    String NO_AWAIT_IN_ASYNC_PARAMS = "Async arrow parameters may not contain \"await\"";

    // not in error.js, but already used in java version
    String STRICT_RESERVED_WORD = "Use of future reserved word in strict mode";
    String UNEXPECTED_OBJECT_BINDING = "Unexpected ObjectBinding in place of Expression";
    String INVALID_REST = "Invalid rest";
    String INVALID_STRICT_OCTAL = "Unexpected legacy octal escape sequence: \\";

    // not in error.js, not used
    String INVALID_REGULAR_EXPRESSION = "Invalid regular expression";
    String INVALID_PROPERTY_NAME = "Property name in object literal must be identifier, string literal or number literal";
    String UNKNOWN_LABEL = "Undefined label '%s'";
    String LABEL_REDECLARATION = "Label '%s' has already been declared";
    String ILLEGAL_CONTINUE = "Illegal continue statement";
    String ILLEGAL_BREAK = "Illegal break statement";
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

    F<Super, EarlyError> SUPERCALL_ERROR = node -> new EarlyError(node, "Calls to super must be in the \"constructor\" method of a class expression or class declaration that has a superclass");
    F<MemberExpression, EarlyError> SUPERPROPERTY_ERROR = node -> new EarlyError(node, "Member access on super must be in a method");
    F<BindingIdentifier, EarlyError> DUPLICATE_BINDING = node -> new EarlyError(node, "Duplicate binding " + Utils.escapeStringLiteral(node.name));
    F<ContinueStatement, EarlyError> FREE_CONTINUE = node -> new EarlyError(node, "Continue statement must be nested within an iteration statement");
    F<ContinueStatement, EarlyError> UNBOUND_CONTINUE = node -> new EarlyError(node, "Continue statement must be nested within an iteration statement with label " + Utils.escapeStringLiteral(node.label.fromJust()));
    F<BreakStatement, EarlyError> FREE_BREAK = node -> new EarlyError(node, "Break statement must be nested within an iteration statement or a switch statement");
    F<BreakStatement, EarlyError> UNBOUND_BREAK = node -> new EarlyError(node, "Break statement must be nested within a statement with label " + Utils.escapeStringLiteral(node.label.fromJust()));
    F<Node, EarlyError> DUPLICATE_CTOR = node -> new EarlyError(node, "Duplicate constructor method in class");
    F<BindingIdentifier, EarlyError> BINDING_IDENTIFIER_STRICT = node -> new EarlyError(node, "The identifier " + Utils.escapeStringLiteral(node.name) + " must not be in binding position in strict mode");
    F<AssignmentTargetIdentifier, EarlyError> TARGET_IDENTIFIER_STRICT = node -> new EarlyError(node, "The identifier " + Utils.escapeStringLiteral(node.name) + " must not be in binding position in strict mode");
    F<IdentifierExpression, EarlyError> IDENTIFIER_EXP_STRICT = node -> new EarlyError(node, "The identifier " + Utils.escapeStringLiteral(node.name) + " must not be in expression position in strict mode");
    F<Node, EarlyError> CTOR_SPECIAL = node -> new EarlyError(node, "Constructors cannot be async, generators, getters or setters");
    F<Node, EarlyError> PROTOTYPE_METHOD = node -> new EarlyError(node, "Static class methods cannot be named \"prototype\"");
    F<Node, EarlyError> DO_WHILE_LABELED_FN = node -> new EarlyError(node, "The body of a do-while statement must not be a labeled function declaration");
    F<Node, EarlyError> FOR_IN_LABELED_FN = node -> new EarlyError(node, "The body of a for-in statement must not be a labeled function declaration");
    F<Node, EarlyError> FOR_OF_LABELED_FN = node -> new EarlyError(node, "The body of a for-of statement must not be a labeled function declaration");
    F<Node, EarlyError> FOR_LABELED_FN = node -> new EarlyError(node, "The body of a for statement must not be a labeled function declaration");
    F<Node, EarlyError> WHILE_LABELED_FN = node -> new EarlyError(node, "The body of a while statement must not be a labeled function declaration");
    F<Node, EarlyError> CONST_WITHOUT_INIT = node -> new EarlyError(node, "Constant lexical declarations must have an initialiser");
    F<Node, EarlyError> CONSEQUENT_IS_LABELED_FN = node -> new EarlyError(node, "The consequent of an if statement must not be a labeled function declaration");
    F<Node, EarlyError> ALTERNATE_IS_LABELED_FN = node -> new EarlyError(node, "The alternate of an if statement must not be a labeled function declaration");
    F<Node, EarlyError> IF_FNDECL_STRICT = node -> new EarlyError(node, "FunctionDeclarations in IfStatements are disallowed in strict mode");
    F<Node, EarlyError> YIELD_LABEL = node -> new EarlyError(node, "The identifier \"yield\" must not be in label position in strict mode");
    F<LabeledStatement, EarlyError> DUPLICATE_LABEL = node -> new EarlyError(node, "Label " + Utils.escapeStringLiteral(node.label) + " has already been declared");
    F<LabeledStatement, EarlyError> FN_LABEL_STRICT = node -> new EarlyError(node, "Labeled FunctionDeclarations are disallowed in strict mode");
    F<Node, EarlyError> INVALID_REGEX_FLAG_MACHINE = node -> new EarlyError(node, INVALID_REGEXP_FLAGS);
    F2<Node, String, EarlyError> DUPLICATE_EXPORT = (node, str) -> new EarlyError(node, "Duplicate export " + Utils.escapeStringLiteral(str));
    F2<Node, String, EarlyError> UNDECLARED_EXPORT = (node, str) -> new EarlyError(node, "Exported binding " + Utils.escapeStringLiteral(str) + " is not declared");
    F<Node, EarlyError> NEW_TARGET_TOP = node -> new EarlyError(node, "new.target must be within function (but not arrow expression) code");
    F<Node, EarlyError> DUPLICATE_PROTO = node -> new EarlyError(node, "Duplicate __proto__ property in object literal not allowed");
    F<Node, EarlyError> DELETE_IDENTIFIER_EXP_STRICT = node -> new EarlyError(node, "Identifier expressions must not be deleted in strict mode");
    F<Node, EarlyError> UPDATE_NONSIMPLE = node -> new EarlyError(node, "Increment/decrement target must be an identifier or member expression");
    F<Node, EarlyError> LEXICAL_LET_BINDING = node -> new EarlyError(node, "Lexical declarations must not have a binding named \"let\"");
    F<Node, EarlyError> WITH_LABELED_FN = node -> new EarlyError(node, "The body of a with statement must not be a labeled function declaration");
    F<Node, EarlyError> WITH_STRICT = node -> new EarlyError(node, "Strict mode code must not include a with statement");
    F<Node, EarlyError> YIELD_IN_ARROW_BODY = node -> new EarlyError(node, "Concise arrow bodies must not contain yield expressions");
    F<Node, EarlyError> YIELD_IN_ARROW_PARAMS = node -> new EarlyError(node, "Arrow parameters must not contain yield expressions");
    F<Node, EarlyError> YIELD_IN_GENERATOR_PARAMS = node -> new EarlyError(node, "Generator parameters must not contain yield expressions");
    F<Node, EarlyError> COMPLEX_PARAMS_WITH_USE_STRICT = node -> new EarlyError(node, "Functions with non-simple parameter lists may not contain a \"use strict\" directive");
    F<Node, EarlyError> AWAIT_IN_ARROW_PARAMS = node -> new EarlyError(node, "Arrow parameters must not contain await expressions");
    F<Node, EarlyError> AWAIT_IN_ASYNC_PARAMS = node -> new EarlyError(node, "Async function parameters must not contain await expressions");

}
