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

package com.shapesecurity.shift.validator;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Token;
import com.shapesecurity.shift.parser.Tokenizer;
import com.shapesecurity.shift.parser.token.EOFToken;
import com.shapesecurity.shift.parser.token.StringLiteralToken;
import com.shapesecurity.shift.utils.Utils;
import com.shapesecurity.shift.visitor.Director;
import com.shapesecurity.shift.visitor.MonoidalReducer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validator extends MonoidalReducer<ValidationContext> {

  public Validator() {
    super(ValidationContext.MONOID);
  }

  private static boolean checkIsLiteralRegExpPattern(String pattern) {
    // copied from tokenizer for getting a regex token
    int index = 0;
    boolean terminated = false;
    boolean classMarker = false;
    while (index < pattern.length()) {
      char ch = pattern.charAt(index);
      if (ch == '\\') {
        index++;
        ch = pattern.charAt(index);
        if (Utils.isLineTerminator(ch)) {
          return false;
        }
        index++;
      } else if (Utils.isLineTerminator(ch)) {
        return false;
      } else {
        if (classMarker) {
          if (ch == ']') {
            classMarker = false;
          }
        } else {
          if (ch == '/') {
            terminated = true;
            index++;
            break;
          } else if (ch == '[') {
            classMarker = true;
          }
        }
        index++;
      }
    }

    if (!terminated) {
      return false;
    }

    while (index < pattern.length()) {
      char ch = pattern.charAt(index);
      if (ch == '\\') {
        return false;
      }
      if (!Utils.isIdentifierPart(ch)) {
        break;
      }
      index++;
    }
    return true;
  }

  public static boolean checkIsValidIdentifierName(String name) {
    return name.length() > 0 && Utils.isIdentifierStart(name.charAt(0)) && name.chars().allMatch(Utils::isIdentifierPart);
  }

  public static ImmutableList<ValidationError> validate(Script script) {
    List<ValidationError> errors = Director.reduceScript(new Validator(), script).errors;
//    System.out.println("size of errors: " + errors.size());
    return ImmutableList.from(Director.reduceScript(new Validator(), script).errors);
  }

  public static ImmutableList<ValidationError> validate(Module module) {
    List<ValidationError> errors = Director.reduceModule(new Validator(), module).errors;
//    System.out.println("size of errors: " + errors.size());
    return ImmutableList.from(Director.reduceModule(new Validator(), module).errors);
  }

  private boolean checkIsStringLiteral(String rawValue) {
    Tokenizer tokenizer;
    try {
      tokenizer = new Tokenizer("\'"+rawValue+"\'", false);
      Token token = tokenizer.lookahead;
      if (!(token instanceof StringLiteralToken)) {
        System.out.println("NOT STRING LITERAL");
        return false;
      }
      token = tokenizer.collectToken();
      if (!(token instanceof EOFToken)) {
        return false;
      }
    } catch (JsError jsError) {
      try {
        tokenizer = new Tokenizer("\""+rawValue+"\"", false);
        Token token = tokenizer.lookahead;
        if (!(token instanceof StringLiteralToken)) {
          System.out.println("NOT STRING LITERAL");
          return false;
        }
        token = tokenizer.collectToken();
        if (!(token instanceof EOFToken)) {
          return false;
        }
      } catch (JsError jsError1) {
        return false;
      }
    }
    return true;
  }

  private boolean hasOneConstructor(ImmutableList<ClassElement> classElements) {
    boolean foundConstructor = false;
    for (ClassElement classElement : classElements) {
      if (foundConstructor) {
        return false;
      }
      if (classElement.isStatic) {
        if (classElement.method.name instanceof StaticPropertyName) {
          if (((StaticPropertyName)classElement.method.name).value.equals("constructor")) {
            foundConstructor = true;
          }
        }
      }
    }
    return true;
  }

  private boolean isProblematicIfStatement(IfStatement node) {
    if (node.alternate.isNothing()) {
      return false;
    }
    Maybe<Statement> current = Maybe.just(node.consequent);
    do {
      Statement currentStmt = current.just();
      if (currentStmt instanceof IfStatement && ((IfStatement)currentStmt).alternate.isNothing()) {
        return true;
      }
      current = trailingStatement(currentStmt);
    } while(current.isJust());
    return false;
  }

  @NotNull
  @Override
  public ValidationContext reduceBindingIdentifier(@NotNull BindingIdentifier node) {
    ValidationContext s = super.reduceBindingIdentifier(node);
    if (!checkIsValidIdentifierName(node.name)) {
      if (node.name.equals("*default*")) {
        s.addBindingIdentifierCalledDefault(node);
      } else {
        s.addError(new ValidationError(node, "the name field of binding identifier must be a valid identifier name"));
      }
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceExportDefault(@NotNull ExportDefault node, @NotNull ValidationContext body) {
    ValidationContext s = super.reduceExportDefault(node, body);
    if (node.body instanceof FunctionDeclaration) {
      s.clearBindingIdentifiersCalledDefault();
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceBreakStatement(@NotNull BreakStatement node) {
    ValidationContext s = super.reduceBreakStatement(node);
    if (node.label.isJust() && !checkIsValidIdentifierName(node.label.just())) {
      s.addError(new ValidationError(node, "the label field of break statement exists and must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceCatchClause(@NotNull CatchClause node, @NotNull ValidationContext binding, @NotNull ValidationContext body) {
    ValidationContext s = super.reduceCatchClause(node, binding, body);
    if (node.binding instanceof MemberExpression) {
      s.addError(new ValidationError(node, "the binding field of CatchClause must not be a member expression"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull ValidationContext name, @NotNull Maybe<ValidationContext> _super, @NotNull ImmutableList<ValidationContext> elements) {
    ValidationContext s = super.reduceClassDeclaration(node, name, _super, elements);
    if (!hasOneConstructor(node.elements)) {
      s.addError(new ValidationError(node, "classes must not have more than one non-static method named 'constructor'"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<ValidationContext> name, @NotNull Maybe<ValidationContext> _super, @NotNull ImmutableList<ValidationContext> elements) {
    ValidationContext s = super.reduceClassExpression(node, name, _super, elements);
    if (!hasOneConstructor(node.elements)) {
      s.addError(new ValidationError(node, "classes must not have more than one non-static method named 'constructor'"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceContinueStatement(@NotNull ContinueStatement node) {
    ValidationContext s = super.reduceContinueStatement(node);
    if (node.label.isJust() && !checkIsValidIdentifierName(node.label.just())) {
      s.addError(new ValidationError(node, "the label field of continue statement exists and must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceDirective(@NotNull Directive node) {
    ValidationContext s = super.reduceDirective(node);
    if (!checkIsStringLiteral(node.rawValue)) {
      s.addError(new ValidationError(node, "the raw value field of directives must either be an empty string, or match the ES6 grammar production DoubleStringCharacter or SingleStringCharacter"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceExportSpecifier(@NotNull ExportSpecifier node) {
    ValidationContext s = super.reduceExportSpecifier(node);
    if (node.name.isJust() && !checkIsValidIdentifierName(node.name.just())) {
      s.addError(new ValidationError(node, "the name field of export specifier exists and must be a valid identifier name"));
    }
    if (!checkIsValidIdentifierName(node.exportedName)) {
      s.addError(new ValidationError(node, "the exported name field of export specifier must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceForInStatement(@NotNull ForInStatement node, @NotNull ValidationContext left,  @NotNull ValidationContext right, @NotNull ValidationContext body) {
    ValidationContext s = super.reduceForInStatement(node, left, right, body);
    if (node.left instanceof VariableDeclaration) {
      if (((VariableDeclaration) node.left).declarators.length != 1) {
        s.addError(new ValidationError(node, "VariableDeclaration in ForInStatement can only have one VariableDeclarator"));
      }
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceForOfStatement(@NotNull ForOfStatement node, @NotNull ValidationContext left,  @NotNull ValidationContext right, @NotNull ValidationContext body) {
    ValidationContext s = super.reduceForOfStatement(node, left, right, body);
    if (node.left instanceof VariableDeclaration) {
      if (((VariableDeclaration) node.left).declarators.length != 1) {
        s.addError(new ValidationError(node, "VariableDeclaration in ForOfStatement can only have one VariableDeclarator"));
      }
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<ValidationContext> items, @NotNull Maybe<ValidationContext> rest) {
    ValidationContext s = super.reduceFormalParameters(node, items, rest);
    node.items.foreach(x -> {
      if (x instanceof Binding) {
        if (x instanceof MemberExpression) {
          s.addError(new ValidationError(node, "the items field of formal parameters must not be member expressions"));
        }
      } else if (x instanceof BindingWithDefault) {
        if (((BindingWithDefault) x).binding instanceof MemberExpression) {
          s.addError(new ValidationError(node, "binding field of the items field of formal parameters must not be a member expression"));
        }
      }
    });
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceFunctionBody(@NotNull FunctionBody node, @NotNull ImmutableList<ValidationContext> directives, @NotNull ImmutableList<ValidationContext> statements
  ) {
    ValidationContext s = super.reduceFunctionBody(node, directives, statements);
    s.clearFreeReturnStatements();
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceIdentifierExpression(@NotNull IdentifierExpression node) {
    ValidationContext s = super.reduceIdentifierExpression(node);
    if (!checkIsValidIdentifierName(node.name)) {
      s.addError(new ValidationError(node, "the name field of identifier expression must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceIfStatement(@NotNull IfStatement node, @NotNull ValidationContext test, @NotNull ValidationContext consequent, @NotNull Maybe<ValidationContext> alternate) {
    ValidationContext s = super.reduceIfStatement(node, test, consequent, alternate);
    if (isProblematicIfStatement(node)) {
      s.addError(new ValidationError(node, "IfStatement with null 'alternate' must not be the 'consequent' of an IfStatement with a non-null 'alternate'"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull ValidationContext binding) {
    ValidationContext s = super.reduceImportSpecifier(node, binding);
    if (node.name.isJust() && !checkIsValidIdentifierName(node.name.just())) {
      s.addError(new ValidationError(node, "the name field of import specifier exists and must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull ValidationContext body) {
    ValidationContext s = super.reduceLabeledStatement(node, body);
    if (!checkIsValidIdentifierName(node.label)) {
      s.addError(new ValidationError(node, "the label field of labeled statement must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node) {
    ValidationContext s = super.reduceLiteralNumericExpression(node);
    if (node.value.isNaN()) {
      s.addError(new ValidationError(node, "the value field of literal numeric expression must not be NaN"));
    }
    if (node.value < 0) {
      s.addError(new ValidationError(node, "the value field of literal numeric expression must be non-negative"));
    }
    if (node.value.isInfinite()) {
      s.addError(new ValidationError(node, "the value field of literal numeric expression must be finite"));
    }
     return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
    ValidationContext s = super.reduceLiteralRegExpExpression(node);
    if (!checkIsLiteralRegExpPattern(node.pattern)) {
      s.addError(new ValidationError(node, "pattern field of literal regular expression expression must match the ES6 grammar production Pattern (21.2.1)"));
    }
    if (node.flags.length() > 0) {
      boolean hasValidFlags = node.flags.chars().allMatch(x -> x == 'g' || x == 'i' || x == 'm' || x == 'u' || x == 'y');
      if (!hasValidFlags) {
        s.addError(new ValidationError(node, "flags field of literal regular expression expression must not contain characters other than 'g', 'i', 'm', 'u', or 'y'"));
      }
    }
    Map<Integer, Boolean> charMap = new HashMap<>();
    node.flags.chars().forEach(x -> {
      if (charMap.containsKey(x)) {
        s.addError(new ValidationError(node, "flags field of literal regular expression expression must not contain duplicate flag characters"));
      } else {
        charMap.put(x, true);
      }
    });
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceReturnStatement(@NotNull ReturnStatement node, @NotNull Maybe<ValidationContext> expression
  ) {
    ValidationContext s = super.reduceReturnStatement(node, expression);
    s.addFreeReturnStatement(node);
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceScript(@NotNull Script node, @NotNull ImmutableList<ValidationContext> directives, @NotNull ImmutableList<ValidationContext> statements
  ) {
    ValidationContext s = super.reduceScript(node, directives, statements);
    s.enforceFreeReturnStatements(returnStatement -> new ValidationError(returnStatement, "return statements must be within a function body"));
    s.enforceBindingIdentifiersCalledDefault(bindingIdentifier -> new ValidationError(bindingIdentifier, "binding identifiers may only be called \"*default*\" within a function declaration"));
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceModule(@NotNull Module node, @NotNull ImmutableList<ValidationContext> directives, @NotNull ImmutableList<ValidationContext> items
  ) {
    ValidationContext s = super.reduceModule(node, directives, items);
    s.enforceFreeReturnStatements(returnStatement -> new ValidationError(returnStatement, "return statements must be within a function body"));
    s.enforceBindingIdentifiersCalledDefault(bindingIdentifier -> new ValidationError(bindingIdentifier, "binding identifiers may only be called \"*default*\" within a function declaration"));
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceSetter(@NotNull Setter node, @NotNull ValidationContext name, @NotNull ValidationContext parameter, @NotNull ValidationContext body) {
    ValidationContext s = super.reduceSetter(node, name, parameter, body);
    if (node.param instanceof Binding) {
      if (node.param instanceof MemberExpression) {
        s.addError(new ValidationError(node, "the param field of setter must not be a member expression"));
      }
    } else if (node.param instanceof BindingWithDefault) {
      if (((BindingWithDefault) node.param).binding instanceof MemberExpression) {
        s.addError(new ValidationError(node, "the binding field of the param field of setter must not be a member expression"));
      }
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceShorthandProperty(@NotNull ShorthandProperty node) {
    ValidationContext s = super.reduceShorthandProperty(node);
    if (!checkIsValidIdentifierName(node.name)) {
      s.addError(new ValidationError(node, "the name field of shorthand property must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull ValidationContext object) {
    ValidationContext s = super.reduceStaticMemberExpression(node, object);
    if (!checkIsValidIdentifierName(node.property)) {
      s.addError(new ValidationError(node, "the property field of static member expression must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceTemplateElement(@NotNull TemplateElement node) {
    ValidationContext s = super.reduceTemplateElement(node);
    if (!checkIsStringLiteral(node.rawValue)) {
      s.addError(new ValidationError(node, "the raw value field of template element must match the ES6 grammar production TemplateCharacters"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<ValidationContext> tag, @NotNull ImmutableList<ValidationContext> elements) {
    ValidationContext s = super.reduceTemplateExpression(node, tag, elements);
    if (elements.length > 0) {
      if (node.elements.length % 2 == 0) {
        s.addError(new ValidationError(node, "the elements field of template expression must be an alternating list of template element and expression, starting and ending with a template element"));
      }
      node.elements.mapWithIndex((i, x) -> {
        if (i % 2 == 0) {
          if (!(x instanceof TemplateElement)) {
            s.addError(new ValidationError(node, "the elements field of template expression must be an alternating list of template element and expression, starting and ending with a template element"));
          }
        } else {
          if (!(x instanceof Expression)) {
            s.addError(new ValidationError(node, "the elements field of template expression must be an alternating list of template element and expression, starting and ending with a template element"));
          }
        }
        return true;
      });
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<ValidationContext> declarators) {
    ValidationContext s = super.reduceVariableDeclaration(node, declarators);
    if (node.declarators.length == 0) {
      s.addError(new ValidationError(node, "the declarators field in variable declaration must not be an empty list"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceVariableDeclarationStatement(@NotNull VariableDeclarationStatement node, @NotNull ValidationContext declaration) {
    ValidationContext s = super.reduceVariableDeclarationStatement(node, declaration);
    if (node.declaration.kind.equals(VariableDeclarationKind.Const)) {
      node.declaration.declarators.foreach(x -> {
        if (x.getInit().isNothing()) {
          s.addError(new ValidationError(node, "VariableDeclarationStatements with a variable declaration of kind const cannot have a variable declarator with no initializer"));
        }
      });
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceVariableDeclarator(@NotNull VariableDeclarator node, @NotNull ValidationContext binding, @NotNull Maybe<ValidationContext> init) {
    ValidationContext s = super.reduceVariableDeclarator(node, binding, init);
    if (node.binding instanceof MemberExpression) {
      s.addError(new ValidationError(node, "the binding field of variable declarator must not be a member expression"));
    }
    return s;
  }

  @NotNull
  private Maybe<Statement> trailingStatement(Statement node) {
    if (node instanceof IfStatement) {
      return Maybe.just(((IfStatement) node).alternate.orJust(((IfStatement) node).consequent));
    } else if (node instanceof LabeledStatement) {
      return Maybe.just(((LabeledStatement) node).body);
    } else if (node instanceof IterationStatement) {
      return Maybe.just(((IterationStatement) node).body);
    }
    return Maybe.nothing();
  }
}
