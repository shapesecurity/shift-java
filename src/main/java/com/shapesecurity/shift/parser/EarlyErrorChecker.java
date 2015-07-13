//package com.shapesecurity.shift.parser;
//
//import com.shapesecurity.functional.data.Either;
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.shift.ast.*;
//import com.shapesecurity.shift.utils.Utils;
//import com.shapesecurity.shift.validator.EarlyErrorContext;
//import com.shapesecurity.shift.validator.ValidationError;
//import com.shapesecurity.shift.visitor.MonoidalReducer;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.HashSet;
//
//public class EarlyErrorChecker extends MonoidalReducer<EarlyErrorContext> {
//
//  public EarlyErrorChecker() {
//    super(EarlyErrorContext.MONOID);
//  }
//
//  public static ImmutableList<ValidationError> validate(Script node) {
//    return node.reduce(new EarlyErrorChecker()).errors.toList();
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceAssignmentExpression(
//    @NotNull AssignmentExpression node,
//    @NotNull EarlyErrorContext binding,
//    @NotNull EarlyErrorContext expression) {
//    EarlyErrorContext v = super.reduceAssignmentExpression(node, binding, expression);
//    if (node.binding instanceof IdentifierExpression) {
//      v = v.checkRestricted((IdentifierExpression) node.binding);
//    }
//    return v;
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceBreakStatement(
//    @NotNull BreakStatement node) {
//    EarlyErrorContext v = super.reduceBreakStatement(node);
//    return node.label.maybe(
//      v.addFreeBreakStatement(
//        new ValidationError(
//          node,
//          "break must be nested within switch or iteration statement")), v::addFreeJumpTarget);
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceCatchClause(
//    @NotNull CatchClause node,
//    @NotNull EarlyErrorContext binding,
//    @NotNull EarlyErrorContext body) {
//    EarlyErrorContext v = super.reduceCatchClause(node, binding, body);
//    return v.checkRestricted((IdentifierExpression) node.binding);
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceContinueStatement(
//    @NotNull ContinueStatement node,
//    @NotNull Maybe<EarlyErrorContext> label) {
//    final EarlyErrorContext v = super.reduceContinueStatement(node, label).addFreeContinueStatement(node);
//    return node.label.maybe(v, v::addFreeJumpTarget);
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceDoWhileStatement(
//    @NotNull DoWhileStatement node,
//    @NotNull EarlyErrorContext body,
//    @NotNull EarlyErrorContext test) {
//    return super.reduceDoWhileStatement(node, body, test).clearFreeContinueStatements()
//      .clearFreeBreakStatements();
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceForInStatement(
//    @NotNull ForInStatement node,
//    @NotNull Either<EarlyErrorContext, EarlyErrorContext> left,
//    @NotNull EarlyErrorContext right,
//    @NotNull EarlyErrorContext body) {
//    EarlyErrorContext v = super.reduceForInStatement(node, left, right, body).clearFreeBreakStatements()
//      .clearFreeContinueStatements();
//    if (node.left.isLeft() && !node.left.left().just().declarators.tail().isEmpty()) {
//      v = v.addError(
//        new ValidationError(
//          node.left.left().just(),
//          "VariableDeclarationStatement in ForInVarStatement contains more than one VariableDeclarator"));
//    }
//    return v;
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceForStatement(
//    @NotNull ForStatement node,
//    @NotNull Maybe<Either<EarlyErrorContext, EarlyErrorContext>> init,
//    @NotNull Maybe<EarlyErrorContext> test,
//    @NotNull Maybe<EarlyErrorContext> update,
//    @NotNull EarlyErrorContext body) {
//    return super.reduceForStatement(node, init, test, update, body).clearFreeBreakStatements()
//      .clearFreeContinueStatements();
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceFunctionBody(
//    @NotNull FunctionBody node,
//    @NotNull ImmutableList<EarlyErrorContext> directives,
//    @NotNull ImmutableList<EarlyErrorContext> statements) {
//    EarlyErrorContext v = super.reduceFunctionBody(node, directives, statements).checkFreeJumpTargets();
//    if (node.isStrict()) {
//      v = v.invalidateStrictErrors();
//    }
//    return v.invalidateFreeContinueAndBreakErrors();
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceFunctionDeclaration(
//    @NotNull FunctionDeclaration node,
//    @NotNull EarlyErrorContext name,
//    @NotNull ImmutableList<EarlyErrorContext> params,
//    @NotNull EarlyErrorContext programBody) {
//    EarlyErrorContext v = super.reduceFunctionDeclaration(node, name, params, programBody).clearUsedLabelNames()
//      .clearReturnStatements().clearUsedLabelNames();
//    if (!Utils.areUniqueNames(node.parameters)) {
//      v = v.addStrictError(new ValidationError(node, "FunctionDeclaration must have unique parameter names"));
//    }
//    v = node.parameters.foldLeft(EarlyErrorContext::checkRestricted, v.checkRestricted(node.name));
//    if (node.body.isStrict()) {
//      v = v.invalidateStrictErrors();
//    }
//    return v;
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceFunctionExpression(
//    @NotNull FunctionExpression node,
//    @NotNull Maybe<EarlyErrorContext> name,
//    @NotNull ImmutableList<EarlyErrorContext> parameters,
//    @NotNull EarlyErrorContext programBody) {
//    EarlyErrorContext v = super.reduceFunctionExpression(node, name, parameters, programBody)
//      .clearReturnStatements();
//    if (!Utils.areUniqueNames(node.params)) {
//      v = v.addStrictError(new ValidationError(node, "FunctionExpression parameter names must be unique"));
//    }
//    v = node.parameters.foldLeft(EarlyErrorContext::checkRestricted, node.name.map(v::checkRestricted).orJust(v));
//    if (node.body.isStrict()) {
//      v = v.invalidateStrictErrors();
//    }
//    return v;
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceGetter(@NotNull Getter node,
//                                        @NotNull EarlyErrorContext name, @NotNull EarlyErrorContext body) {
//    return super.reduceGetter(node, name, body).clearReturnStatements();
//  }
//
////  @NotNull
////  @Override
////  public EarlyErrorContext reduceIdentifier(@NotNull Identifier node) {
////    EarlyErrorContext v = new EarlyErrorContext();
////    if (!Utils.isValidIdentifierName(node.name)) {
////      v = v.addError(new ValidationError(node, "Identifier `name` must be a valid IdentifierName"));
////    }
////    return v;
////  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceIdentifierExpression(
//    @NotNull IdentifierExpression node,
//    @NotNull EarlyErrorContext identifier) {
//    return super.reduceIdentifierExpression(node, identifier).checkReserved(node.identifier);
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceLabeledStatement(
//    @NotNull LabeledStatement node,
//    @NotNull EarlyErrorContext label,
//    @NotNull EarlyErrorContext body) {
//    return super.reduceLabeledStatement(node, label, body).observeLabelName(node.label);
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceLiteralNumericExpression(
//    @NotNull LiteralNumericExpression node) {
//    EarlyErrorContext v = new EarlyErrorContext();
//    if (node.value < 0 || node.value == 0 && 1 / node.value < 0) {
//      v = v.addError(new ValidationError(node, "Numeric Literal node must be non-negative"));
//    } else if (Double.isNaN(node.value)) {
//      v = v.addError(new ValidationError(node, "Numeric Literal node must not be NaN"));
//    } else if (Double.isInfinite(node.value)) {
//      v = v.addError(new ValidationError(node, "Numeric Literal node must be finite"));
//    }
//    return v;
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceObjectExpression(
//    @NotNull final ObjectExpression node,
//    @NotNull ImmutableList<EarlyErrorContext> properties) {
//    EarlyErrorContext v = super.reduceObjectExpression(node, properties);
//    final HashSet<String> setKeys = new HashSet<>();
//    final HashSet<String> getKeys = new HashSet<>();
//    final HashSet<String> dataKeys = new HashSet<>();
//    for (ObjectProperty p : node.properties) {
//      String key = p.name.value;
//      switch (p.getKind()) {
//        case InitProperty:
//          if (dataKeys.contains(key)) {
//            v = v.addStrictError(
//              new ValidationError(
//                node,
//                "ObjectExpression must not have more that one data property with the same name"));
//          }
//          if (getKeys.contains(key)) {
//            v = v.addError(
//              new ValidationError(
//                node,
//                "ObjectExpression must not have data and getter properties with same name"));
//          }
//          if (setKeys.contains(key)) {
//            v = v.addError(
//              new ValidationError(
//                node,
//                "ObjectExpression must not have data and setter properties with same name"));
//          }
//          dataKeys.add(key);
//          break;
//        case GetterProperty:
//          if (getKeys.contains(key)) {
//            v = v.addError(
//              new ValidationError(
//                node,
//                "ObjectExpression must not have multiple getters with the same name"));
//          }
//          if (dataKeys.contains(key)) {
//            v = v.addError(
//              new ValidationError(
//                node,
//                "ObjectExpression must not have data and getter properties with the same name"));
//          }
//          getKeys.add(key);
//          break;
//        case SetterProperty:
//          if (setKeys.contains(key)) {
//            v = v.addError(
//              new ValidationError(
//                node,
//                "ObjectExpression must not have multiple setters with the same name"));
//          }
//          if (dataKeys.contains(key)) {
//            v = v.addError(
//              new ValidationError(
//                node,
//                "ObjectExpression must not have data and setter properties with the same name"));
//          }
//          setKeys.add(key);
//          break;
//        default:
//          break;
//      }
//    }
//    return v;
//  }
//
////  @NotNull
////  @Override
////  public EarlyErrorContext reducePrefixExpression(
////      @NotNull PrefixExpression node,
////      @NotNull EarlyErrorContext operand) {
////    EarlyErrorContext v = super.reducePrefixExpression(node, path, operand);
////    if (node.operator == PrefixOperator.Delete && node.operand instanceof IdentifierExpression) {
////      return v.addStrictError(
////          new ValidationError(
////              node,
////              "`delete` with unqualified identifier not allowed in strict mode"));
////    }
////    return v;
////  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reducePropertyName(@NotNull PropertyName node, @NotNull ImmutableList<Branch> path) {
//    EarlyErrorContext v = super.reducePropertyName(node, path);
//    switch (node.kind) {
//      case Identifier:
//        if (!Utils.isValidIdentifierName(node.value)) {
//          return v.addError(
//            new ValidationError(node, "PropertyName of kind 'identifier' must be valid identifier name."));
//        }
//        break;
//      case Number:
//        if (!Utils.isValidNumber(node.value)) {
//          return v.addError(new ValidationError(node, "PropertyName of kind 'number' must be a valid number literal."));
//        }
//        break;
//    }
//    return v;
//  }
//
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceScript(
//    @NotNull Script node,
//    @NotNull EarlyErrorContext body) {
//    return super.reduceScript(node, body).invalidateFreeReturnErrors();
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceSetter(
//    @NotNull Setter node,
//    @NotNull EarlyErrorContext name,
//    @NotNull EarlyErrorContext param,
//    @NotNull EarlyErrorContext body) {
//    return super.reduceSetter(node, name, param, body).checkRestricted(node.param).clearReturnStatements();
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceStaticMemberExpression(
//    @NotNull StaticMemberExpression node,
//    @NotNull EarlyErrorContext object,
//    @NotNull EarlyErrorContext property) {
//    return super.reduceStaticMemberExpression(node, object, property.clearIdentifierNameError());
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceSwitchStatement(
//    @NotNull SwitchStatement node,
//    @NotNull EarlyErrorContext discriminant,
//    @NotNull ImmutableList<EarlyErrorContext> cases) {
//    return super.reduceSwitchStatement(node, discriminant, cases).clearFreeBreakStatements();
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceSwitchStatementWithDefault(
//    @NotNull SwitchStatementWithDefault node,
//    @NotNull EarlyErrorContext discriminant,
//    @NotNull ImmutableList<EarlyErrorContext> preDefaultCases,
//    @NotNull EarlyErrorContext defaultCase,
//    @NotNull ImmutableList<EarlyErrorContext> postDefaultCases) {
//    return super.reduceSwitchStatementWithDefault(
//      node,
//      discriminant,
//      preDefaultCases,
//      defaultCase,
//      postDefaultCases)
//      .clearFreeBreakStatements();
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceVariableDeclarator(
//    @NotNull VariableDeclarator node,
//    @NotNull EarlyErrorContext binding,
//    @NotNull Maybe<EarlyErrorContext> init) {
//    return super.reduceVariableDeclarator(node, binding, init).checkRestricted(node.binding);
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceWhileStatement(
//    @NotNull WhileStatement node,
//    @NotNull EarlyErrorContext test,
//    @NotNull EarlyErrorContext body) {
//    return super.reduceWhileStatement(node, test, body).clearFreeBreakStatements().clearFreeContinueStatements();
//  }
//
//  @NotNull
//  @Override
//  public EarlyErrorContext reduceWithStatement(
//    @NotNull WithStatement node,
//    @NotNull EarlyErrorContext object,
//    @NotNull EarlyErrorContext body) {
//    return super.reduceWithStatement(node, object, body).addStrictError(
//      new ValidationError(node, "WithStatement not allowed in strict mode"));
//  }
//}
