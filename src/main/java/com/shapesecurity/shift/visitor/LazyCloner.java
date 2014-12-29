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

package com.shapesecurity.shift.visitor;

import com.shapesecurity.functional.Thunk;
import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyList;
import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.Directive;
import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.SwitchCase;
import com.shapesecurity.shift.ast.SwitchDefault;
import com.shapesecurity.shift.ast.VariableDeclaration;
import com.shapesecurity.shift.ast.VariableDeclarator;
import com.shapesecurity.shift.ast.directive.UnknownDirective;
import com.shapesecurity.shift.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.ast.expression.ArrayExpression;
import com.shapesecurity.shift.ast.expression.AssignmentExpression;
import com.shapesecurity.shift.ast.expression.BinaryExpression;
import com.shapesecurity.shift.ast.expression.CallExpression;
import com.shapesecurity.shift.ast.expression.ComputedMemberExpression;
import com.shapesecurity.shift.ast.expression.ConditionalExpression;
import com.shapesecurity.shift.ast.expression.FunctionExpression;
import com.shapesecurity.shift.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.shift.ast.expression.LiteralNullExpression;
import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.ast.expression.LiteralRegExpExpression;
import com.shapesecurity.shift.ast.expression.LiteralStringExpression;
import com.shapesecurity.shift.ast.expression.NewExpression;
import com.shapesecurity.shift.ast.expression.ObjectExpression;
import com.shapesecurity.shift.ast.expression.PostfixExpression;
import com.shapesecurity.shift.ast.expression.PrefixExpression;
import com.shapesecurity.shift.ast.expression.StaticMemberExpression;
import com.shapesecurity.shift.ast.expression.ThisExpression;
import com.shapesecurity.shift.ast.property.DataProperty;
import com.shapesecurity.shift.ast.property.Getter;
import com.shapesecurity.shift.ast.property.ObjectProperty;
import com.shapesecurity.shift.ast.property.PropertyName;
import com.shapesecurity.shift.ast.property.Setter;
import com.shapesecurity.shift.ast.statement.BlockStatement;
import com.shapesecurity.shift.ast.statement.BreakStatement;
import com.shapesecurity.shift.ast.statement.ContinueStatement;
import com.shapesecurity.shift.ast.statement.DebuggerStatement;
import com.shapesecurity.shift.ast.statement.DoWhileStatement;
import com.shapesecurity.shift.ast.statement.EmptyStatement;
import com.shapesecurity.shift.ast.statement.ExpressionStatement;
import com.shapesecurity.shift.ast.statement.ForInStatement;
import com.shapesecurity.shift.ast.statement.ForStatement;
import com.shapesecurity.shift.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.ast.statement.IfStatement;
import com.shapesecurity.shift.ast.statement.LabeledStatement;
import com.shapesecurity.shift.ast.statement.ReturnStatement;
import com.shapesecurity.shift.ast.statement.SwitchStatement;
import com.shapesecurity.shift.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.shift.ast.statement.ThrowStatement;
import com.shapesecurity.shift.ast.statement.TryCatchStatement;
import com.shapesecurity.shift.ast.statement.TryFinallyStatement;
import com.shapesecurity.shift.ast.statement.VariableDeclarationStatement;
import com.shapesecurity.shift.ast.statement.WhileStatement;
import com.shapesecurity.shift.ast.statement.WithStatement;
import com.shapesecurity.shift.path.Branch;

import org.jetbrains.annotations.NotNull;

public class LazyCloner
    implements ReducerP<DirtyState<Script>, DirtyState<FunctionBody>, DirtyState<ObjectProperty>, DirtyState<PropertyName>, DirtyState<Identifier>, DirtyState<Expression>, DirtyState<Directive>, DirtyState<Statement>, DirtyState<Block>, DirtyState<VariableDeclarator>, DirtyState<VariableDeclaration>, DirtyState<SwitchCase>, DirtyState<SwitchDefault>, DirtyState<CatchClause>> {
  public static final LazyCloner INSTANCE = new LazyCloner();

  protected LazyCloner() {
  }

  private static <T> DirtyState<List<Maybe<T>>> lo(List<Maybe<DirtyState<T>>> elements) {
    return l(elements.map(LazyCloner::op));
  }

  private static <T> DirtyState<List<T>> l(@NotNull List<DirtyState<T>> node) {
    return new DirtyState<>(node.map(tDirtyState -> tDirtyState.node), node.exists(tDirtyState -> tDirtyState.dirty));
  }

  private static <T> DirtyState<Maybe<T>> op(@NotNull Maybe<DirtyState<T>> node) {
    if (node.isJust()) {
      DirtyState<T> s = node.just();
      return new DirtyState<>(Maybe.just(s.node), s.dirty);
    } else {
      return clean(Maybe.<T>nothing());
    }
  }

  private static <U> DirtyState<U> get(@NotNull U def, @NotNull DirtyState<Thunk<U>> s) {
    return s.dirty ? dirty(s.node.get()) : clean(def);
  }

  private static <T> DirtyState<T> dirty(@NotNull T node) {
    return new DirtyState<>(node, true);
  }

  private static <T> DirtyState<T> clean(@NotNull T node) {
    return new DirtyState<>(node, false);
  }

  private static <T> DirtyState<NonEmptyList<T>> l(@NotNull NonEmptyList<DirtyState<T>> node) {
    return new DirtyState<>(node.map(tDirtyState -> tDirtyState.node), node.exists(tDirtyState -> tDirtyState.dirty));
  }

  @NotNull
  @Override
  public DirtyState<Script> reduceScript(
      @NotNull Script node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<FunctionBody> body) {
    if (body.dirty) {
      return dirty(new Script(body.node));
    }
    return clean(node);
  }

  @NotNull
  @Override
  public DirtyState<Identifier> reduceIdentifier(@NotNull Identifier node, @NotNull List<Branch> path) {
    return clean(node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceIdentifierExpression(
      @NotNull IdentifierExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Identifier> identifier) {
    if (identifier.dirty) {
      return dirty((Expression) new IdentifierExpression(node.identifier));
    }
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceThisExpression(@NotNull ThisExpression node, @NotNull List<Branch> path) {
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceLiteralBooleanExpression(
      @NotNull LiteralBooleanExpression node,
      @NotNull List<Branch> path) {
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceLiteralStringExpression(
      @NotNull LiteralStringExpression node,
      @NotNull List<Branch> path) {
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceLiteralRegExpExpression(
      @NotNull LiteralRegExpExpression node,
      @NotNull List<Branch> path) {
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceLiteralNumericExpression(
      @NotNull LiteralNumericExpression node,
      @NotNull List<Branch> path) {
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceLiteralNullExpression(
      @NotNull LiteralNullExpression node,
      @NotNull List<Branch> path) {
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull List<Branch> path,
      @NotNull Maybe<DirtyState<Identifier>> name,
      @NotNull List<DirtyState<Identifier>> parameters,
      @NotNull DirtyState<FunctionBody> body) {
    DirtyState<Maybe<Identifier>> i = op(name);
    DirtyState<List<Identifier>> p = l(parameters);
    if (i.dirty || p.dirty || body.dirty) {
      return dirty((Expression) new FunctionExpression(i.node, p.node, body.node));
    }
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceStaticMemberExpression(
      @NotNull StaticMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> object,
      @NotNull DirtyState<Identifier> property) {
    if (object.dirty || property.dirty) {
      return dirty((Expression) new StaticMemberExpression(object.node, property.node));
    }
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceComputedMemberExpression(
      @NotNull ComputedMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> object,
      @NotNull final DirtyState<Expression> expression) {
    if (object.dirty || expression.dirty) {
      return dirty((Expression) new ComputedMemberExpression(object.node, expression.node));
    }
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceObjectExpression(
      @NotNull ObjectExpression node,
      @NotNull List<Branch> path,
      @NotNull List<DirtyState<ObjectProperty>> properties) {
    DirtyState<List<ObjectProperty>> p = l(properties);
    if (p.dirty) {
      return dirty((Expression) new ObjectExpression(p.node));
    }
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceBinaryExpression(
      @NotNull final BinaryExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> left,
      @NotNull final DirtyState<Expression> right) {
    return get(node, left.bind(l -> right.bindLast(r -> new BinaryExpression(node.operator, l, r))));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceAssignmentExpression(
      @NotNull AssignmentExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> binding,
      @NotNull DirtyState<Expression> expression) {
    return get(node, binding.bind(l -> expression.bindLast(r -> new AssignmentExpression(node.operator, l, r))));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceArrayExpression(
      @NotNull ArrayExpression node,
      @NotNull List<Branch> path,
      @NotNull List<Maybe<DirtyState<Expression>>> elements) {
    return LazyCloner.<Expression>get(node, lo(elements).bindLast(ArrayExpression::new));
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceNewExpression(
      @NotNull NewExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> callee,
      @NotNull final List<DirtyState<Expression>> arguments) {
    DirtyState<List<Expression>> args = l(arguments);
    if (callee.dirty || args.dirty) {
      return dirty((Expression) new NewExpression(callee.node, args.node));
    }
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceCallExpression(
      @NotNull CallExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> callee,
      @NotNull final List<DirtyState<Expression>> arguments) {
    DirtyState<List<Expression>> args = l(arguments);
    if (callee.dirty || args.dirty) {
      return dirty((Expression) new CallExpression(callee.node, args.node));
    }
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reducePostfixExpression(
      @NotNull final PostfixExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> operand) {
    if (operand.dirty) {
      return dirty((Expression) new PostfixExpression(node.operator, operand.node));
    }
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reducePrefixExpression(
      @NotNull final PrefixExpression node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> operand) {
    if (operand.dirty) {
      return dirty((Expression) new PrefixExpression(node.operator, operand.node));
    }
    return clean((Expression) node);
  }

  @NotNull
  @Override
  public DirtyState<Expression> reduceConditionalExpression(
      @NotNull ConditionalExpression node,
      @NotNull List<Branch> path,
      @NotNull final DirtyState<Expression> test,
      @NotNull final DirtyState<Expression> consequent,
      @NotNull final DirtyState<Expression> alternate) {
    return get(node, test.bind(t -> consequent.bind(c -> alternate.bindLast(a -> new ConditionalExpression(t, c, a)))));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Identifier> name,
      @NotNull final List<DirtyState<Identifier>> params,
      @NotNull final DirtyState<FunctionBody> body) {
    return get(node, name.bind(id1 -> l(params).bind(params1 -> body.bindLast(body1 -> new FunctionDeclaration(id1,
        params1, body1)))));
  }

  @NotNull
  @Override
  public DirtyState<Directive> reduceUseStrictDirective(@NotNull UseStrictDirective node, @NotNull List<Branch> path) {
    return clean((Directive) node);
  }

  @NotNull
  @Override
  public DirtyState<Directive> reduceUnknownDirective(@NotNull UnknownDirective node, @NotNull List<Branch> path) {
    return clean((Directive) node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceBlockStatement(
      @NotNull BlockStatement node,
      @NotNull List<Branch> path,
      @NotNull final DirtyState<Block> block) {
    if (block.dirty) {
      return dirty((Statement) new BlockStatement(block.node));
    }
    return clean((Statement) node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceBreakStatement(
      @NotNull BreakStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<DirtyState<Identifier>> label) {
    if (label.isJust() && label.just().dirty) {
      return dirty((Statement) new BreakStatement(Maybe.just(label.just().node)));
    }
    return clean((Statement) node);
  }

  @NotNull
  @Override
  public DirtyState<CatchClause> reduceCatchClause(
      @NotNull CatchClause node,
      @NotNull List<Branch> path,
      @NotNull final DirtyState<Identifier> binding,
      @NotNull final DirtyState<Block> body) {
    return get(node, binding.bind(p -> body.bindLast(s -> new CatchClause(p, s))));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceContinueStatement(
      @NotNull ContinueStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<DirtyState<Identifier>> label) {
    if (label.isJust() && label.just().dirty) {
      return dirty((Statement) new ContinueStatement(Maybe.just(label.just().node)));
    }
    return clean((Statement) node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceDebuggerStatement(@NotNull DebuggerStatement node, @NotNull List<Branch> path) {
    return clean((Statement) node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceDoWhileStatement(
      @NotNull DoWhileStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Statement> body,
      @NotNull final DirtyState<Expression> test) {
    return get(node, body.bind(body1 -> test.bindLast(test1 -> new DoWhileStatement(body1, test1))));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceEmptyStatement(@NotNull EmptyStatement node, @NotNull List<Branch> path) {
    return clean((Statement) node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceExpressionStatement(
      @NotNull ExpressionStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> expression) {
    if (expression.dirty) {
      return dirty((Statement) new ExpressionStatement(expression.node));
    }
    return clean((Statement) node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull List<Branch> path,
      @NotNull Either<DirtyState<VariableDeclaration>, DirtyState<Expression>> left,
      @NotNull final DirtyState<Expression> right,
      @NotNull final DirtyState<Statement> body) {
    boolean leftDirty = left.either(x -> x.dirty, x -> x.dirty);
    Either<VariableDeclaration, Expression> leftNode = left.map(x -> x.node, x -> x.node);
    if (leftDirty || right.dirty || body.dirty) {
      return dirty((Statement) new ForInStatement(leftNode, right.node, body.node));
    }
    return clean((Statement) node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceForStatement(
      @NotNull ForStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<Either<DirtyState<VariableDeclaration>, DirtyState<Expression>>> init,
      @NotNull final Maybe<DirtyState<Expression>> test,
      @NotNull final Maybe<DirtyState<Expression>> update,
      @NotNull final DirtyState<Statement> body) {
    boolean iDirty = init.map(x -> x.either(y -> y.dirty, y -> y.dirty)).orJust(false);
    Maybe<Either<VariableDeclaration, Expression>> iNode = init.map(x -> x.map(y -> y.node, y -> y.node));
    DirtyState<Maybe<Expression>> t = op(test), u = op(update);
    if (iDirty || t.dirty || u.dirty || body.dirty) {
      return dirty((Statement) new ForStatement(iNode, t.node, u.node, body.node));
    }
    return clean((Statement) node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceIfStatement(
      @NotNull IfStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> test,
      @NotNull final DirtyState<Statement> consequent,
      @NotNull final Maybe<DirtyState<Statement>> alternate) {
    return get(node, test.bind(test1 -> consequent.bind(consequent1 -> op(alternate).bindLast(
        alternate1 -> new IfStatement(test1, consequent1, alternate1)))));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceLabeledStatement(
      @NotNull LabeledStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Identifier> label,
      @NotNull final DirtyState<Statement> body) {
    return get(node, label.bind(label1 -> body.bindLast(body1 -> new LabeledStatement(label1, body1))));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceReturnStatement(
      @NotNull ReturnStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<DirtyState<Expression>> expression) {
    if (expression.isNothing() || !expression.just().dirty) {
      return clean((Statement) node);
    }
    return DirtyState.dirty((Statement) new ReturnStatement(Maybe.just(expression.just().node)));
  }

  @NotNull
  @Override
  public DirtyState<SwitchCase> reduceSwitchCase(
      @NotNull SwitchCase node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> test,
      @NotNull final List<DirtyState<Statement>> consequent) {
    return get(node, test.bind(test1 -> l(consequent).bindLast(consequent1 -> new SwitchCase(test1, consequent1))));
  }

  @NotNull
  @Override
  public DirtyState<SwitchDefault> reduceSwitchDefault(
      @NotNull SwitchDefault node,
      @NotNull List<Branch> path,
      @NotNull List<DirtyState<Statement>> consequent) {
    return get(node, l(consequent).bindLast(SwitchDefault::new));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceSwitchStatement(
      @NotNull final SwitchStatement node,
      @NotNull final List<Branch> path,
      @NotNull DirtyState<Expression> discriminant,
      @NotNull final List<DirtyState<SwitchCase>> cases) {
    return get(node, discriminant.bind(discriminant1 -> l(cases).bindLast(
        cases1 -> CloneReducer.INSTANCE.reduceSwitchStatement(node, path, discriminant1, cases1))));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> discriminant,
      @NotNull List<DirtyState<SwitchCase>> preDefaultCases,
      @NotNull DirtyState<SwitchDefault> defaultCase,
      @NotNull List<DirtyState<SwitchCase>> postDefaultCases) {
    DirtyState<List<SwitchCase>> cs = l(preDefaultCases);
    DirtyState<List<SwitchCase>> pcs = l(postDefaultCases);

    if (discriminant.dirty || cs.dirty || defaultCase.dirty || pcs.dirty) {
      return dirty((Statement) new SwitchStatementWithDefault(discriminant.node, cs.node, defaultCase.node, pcs.node));
    }
    return clean((Statement) node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceThrowStatement(
      @NotNull ThrowStatement node,
      @NotNull List<Branch> path,
      @NotNull final DirtyState<Expression> expression) {
    return get(node, expression.bindLast(ThrowStatement::new));
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceTryCatchStatement(
      @NotNull TryCatchStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Block> block,
      @NotNull DirtyState<CatchClause> catchClause) {
    if (block.dirty || catchClause.dirty) {
      return DirtyState.dirty(new TryCatchStatement(block.node, catchClause.node));
    }
    return DirtyState.clean(node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceTryFinallyStatement(
      @NotNull TryFinallyStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Block> block,
      @NotNull Maybe<DirtyState<CatchClause>> catchClause,
      @NotNull DirtyState<Block> finalizer) {
    DirtyState<Maybe<CatchClause>> op = op(catchClause);
    if (block.dirty || op.dirty || finalizer.dirty) {
      return dirty(new TryFinallyStatement(block.node, op.node, finalizer.node));
    }
    return clean(node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceVariableDeclarationStatement(
      @NotNull final VariableDeclarationStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<VariableDeclaration> declaration) {
    if (declaration.dirty) {
      return dirty((Statement) new VariableDeclarationStatement(declaration.node));
    }
    return clean((Statement) node);
  }

  @NotNull
  @Override
  public DirtyState<VariableDeclaration> reduceVariableDeclaration(
      @NotNull VariableDeclaration node,
      @NotNull List<Branch> path,
      @NotNull NonEmptyList<DirtyState<VariableDeclarator>> declarators) {
    DirtyState<NonEmptyList<VariableDeclarator>> ds = l(declarators);
    if (ds.dirty) {
      return dirty(new VariableDeclaration(node.kind, ds.node));
    }
    return clean(node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceWhileStatement(
      @NotNull WhileStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> test,
      @NotNull final DirtyState<Statement> body) {
    if (test.dirty || body.dirty) {
      return dirty((Statement) new WhileStatement(test.node, body.node));
    }
    return clean((Statement) node);
  }

  @NotNull
  @Override
  public DirtyState<Statement> reduceWithStatement(
      @NotNull WithStatement node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Expression> object,
      @NotNull final DirtyState<Statement> body) {
    return get(node, object.bind(object1 -> body.bindLast(body1 -> new WithStatement(object1, body1))));
  }

  @NotNull
  @Override
  public DirtyState<ObjectProperty> reduceDataProperty(
      @NotNull DataProperty node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<PropertyName> name,
      @NotNull final DirtyState<Expression> value) {
    if (name.dirty || value.dirty) {
      return dirty((ObjectProperty) new DataProperty(name.node, value.node));
    }
    return clean((ObjectProperty) node);
  }

  @NotNull
  @Override
  public DirtyState<ObjectProperty> reduceGetter(
      @NotNull Getter node,
      @NotNull List<Branch> path,
      @NotNull final DirtyState<PropertyName> name,
      @NotNull final DirtyState<FunctionBody> body) {
    return get(node, name.bind(propertyName -> body.bindLast(programBody -> new Getter(propertyName, programBody))));
  }

  @NotNull
  @Override
  public DirtyState<ObjectProperty> reduceSetter(
      @NotNull Setter node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<PropertyName> name,
      @NotNull final DirtyState<Identifier> parameter,
      @NotNull final DirtyState<FunctionBody> body) {
    if (name.dirty || parameter.dirty || body.dirty) {
      return dirty((ObjectProperty) new Setter(name.node, parameter.node, body.node));
    }
    return clean((ObjectProperty) node);
  }

  @NotNull
  @Override
  public DirtyState<PropertyName> reducePropertyName(@NotNull PropertyName node, @NotNull List<Branch> path) {
    return clean(node);
  }

  @NotNull
  @Override
  public DirtyState<FunctionBody> reduceFunctionBody(
      @NotNull FunctionBody node,
      @NotNull List<Branch> path,
      @NotNull List<DirtyState<Directive>> directives,
      @NotNull final List<DirtyState<Statement>> statements) {
    DirtyState<List<Directive>> dirs = l(directives);
    DirtyState<List<Statement>> ses = l(statements);
    if (dirs.dirty || ses.dirty) {
      return DirtyState.dirty(new FunctionBody(dirs.node, ses.node));
    }
    return clean(node);
  }

  @NotNull
  @Override
  public DirtyState<VariableDeclarator> reduceVariableDeclarator(
      @NotNull VariableDeclarator node,
      @NotNull List<Branch> path,
      @NotNull DirtyState<Identifier> binding,
      @NotNull final Maybe<DirtyState<Expression>> init) {
    if (binding.dirty) {
      return dirty(new VariableDeclarator(binding.node, init.map(expr -> expr.node)));
    }
    if (init.isJust() && init.just().dirty) {
      return dirty(new VariableDeclarator(binding.node, Maybe.just(init.just().node)));
    }
    return clean(node);
  }

  @NotNull
  @Override
  public DirtyState<Block> reduceBlock(
      @NotNull Block node,
      @NotNull List<Branch> path,
      @NotNull List<DirtyState<Statement>> statements) {
    DirtyState<List<Statement>> ds = l(statements);
    if (ds.dirty) {
      return DirtyState.dirty(new Block(ds.node));
    }
    return DirtyState.clean(node);
  }
}
