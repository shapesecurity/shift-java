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

package com.shapesecurity.shift.js.visitor;

import com.shapesecurity.shift.functional.Thunk;
import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.Block;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.Directive;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Script;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.SwitchCase;
import com.shapesecurity.shift.js.ast.SwitchDefault;
import com.shapesecurity.shift.js.ast.VariableDeclaration;
import com.shapesecurity.shift.js.ast.VariableDeclarator;
import com.shapesecurity.shift.js.ast.directive.UnknownDirective;
import com.shapesecurity.shift.js.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.js.ast.expression.ArrayExpression;
import com.shapesecurity.shift.js.ast.expression.AssignmentExpression;
import com.shapesecurity.shift.js.ast.expression.BinaryExpression;
import com.shapesecurity.shift.js.ast.expression.CallExpression;
import com.shapesecurity.shift.js.ast.expression.ComputedMemberExpression;
import com.shapesecurity.shift.js.ast.expression.ConditionalExpression;
import com.shapesecurity.shift.js.ast.expression.FunctionExpression;
import com.shapesecurity.shift.js.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralNullExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralRegExpExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralStringExpression;
import com.shapesecurity.shift.js.ast.expression.NewExpression;
import com.shapesecurity.shift.js.ast.expression.ObjectExpression;
import com.shapesecurity.shift.js.ast.expression.PostfixExpression;
import com.shapesecurity.shift.js.ast.expression.PrefixExpression;
import com.shapesecurity.shift.js.ast.expression.StaticMemberExpression;
import com.shapesecurity.shift.js.ast.expression.ThisExpression;
import com.shapesecurity.shift.js.ast.property.DataProperty;
import com.shapesecurity.shift.js.ast.property.Getter;
import com.shapesecurity.shift.js.ast.property.ObjectProperty;
import com.shapesecurity.shift.js.ast.property.PropertyName;
import com.shapesecurity.shift.js.ast.property.Setter;
import com.shapesecurity.shift.js.ast.statement.BlockStatement;
import com.shapesecurity.shift.js.ast.statement.BreakStatement;
import com.shapesecurity.shift.js.ast.statement.ContinueStatement;
import com.shapesecurity.shift.js.ast.statement.DebuggerStatement;
import com.shapesecurity.shift.js.ast.statement.DoWhileStatement;
import com.shapesecurity.shift.js.ast.statement.EmptyStatement;
import com.shapesecurity.shift.js.ast.statement.ExpressionStatement;
import com.shapesecurity.shift.js.ast.statement.ForInStatement;
import com.shapesecurity.shift.js.ast.statement.ForStatement;
import com.shapesecurity.shift.js.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.js.ast.statement.IfStatement;
import com.shapesecurity.shift.js.ast.statement.LabeledStatement;
import com.shapesecurity.shift.js.ast.statement.ReturnStatement;
import com.shapesecurity.shift.js.ast.statement.SwitchStatement;
import com.shapesecurity.shift.js.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.shift.js.ast.statement.ThrowStatement;
import com.shapesecurity.shift.js.ast.statement.TryCatchStatement;
import com.shapesecurity.shift.js.ast.statement.TryFinallyStatement;
import com.shapesecurity.shift.js.ast.statement.VariableDeclarationStatement;
import com.shapesecurity.shift.js.ast.statement.WhileStatement;
import com.shapesecurity.shift.js.ast.statement.WithStatement;
import com.shapesecurity.shift.js.path.Branch;

import javax.annotation.Nonnull;

public class LazyCloner
    implements ReducerP<DirtyState<Script>, DirtyState<FunctionBody>, DirtyState<ObjectProperty>, DirtyState<PropertyName>, DirtyState<Identifier>, DirtyState<Expression>, DirtyState<Directive>, DirtyState<Statement>, DirtyState<Block>, DirtyState<VariableDeclarator>, DirtyState<VariableDeclaration>, DirtyState<SwitchCase>, DirtyState<SwitchDefault>, DirtyState<CatchClause>> {
  public static final LazyCloner INSTANCE = new LazyCloner();

  protected LazyCloner() {
  }

  private static <T> DirtyState<List<Maybe<T>>> lo(List<Maybe<DirtyState<T>>> elements) {
    return l(elements.map(LazyCloner::op));
  }

  private static <T> DirtyState<List<T>> l(@Nonnull List<DirtyState<T>> node) {
    return new DirtyState<>(node.map(tDirtyState -> tDirtyState.node), node.exists(tDirtyState -> tDirtyState.dirty));
  }

  private static <T> DirtyState<Maybe<T>> op(@Nonnull Maybe<DirtyState<T>> node) {
    if (node.isJust()) {
      DirtyState<T> s = node.just();
      return new DirtyState<>(Maybe.just(s.node), s.dirty);
    } else {
      return clean(Maybe.<T>nothing());
    }
  }

  public static <U> DirtyState<U> get(@Nonnull U def, @Nonnull DirtyState<Thunk<U>> s) {
    return s.dirty ? dirty(s.node.get()) : clean(def);
  }

  private static <T> DirtyState<T> dirty(@Nonnull T node) {
    return new DirtyState<>(node, true);
  }

  private static <T> DirtyState<T> clean(@Nonnull T node) {
    return new DirtyState<>(node, false);
  }

  private static <T> DirtyState<NonEmptyList<T>> l(@Nonnull NonEmptyList<DirtyState<T>> node) {
    return new DirtyState<>(node.map(tDirtyState -> tDirtyState.node), node.exists(tDirtyState -> tDirtyState.dirty));
  }

  @Nonnull
  @Override
  public DirtyState<Script> reduceScript(
      @Nonnull Script node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<FunctionBody> body) {
    if (body.dirty) {
      return dirty(new Script(body.node));
    }
    return clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Identifier> reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path) {
    return clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceIdentifierExpression(
      @Nonnull IdentifierExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Identifier> name) {
    if (name.dirty) {
      return dirty((Expression) new IdentifierExpression(node.identifier));
    }
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceThisExpression(@Nonnull ThisExpression node, @Nonnull List<Branch> path) {
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceLiteralBooleanExpression(
      @Nonnull LiteralBooleanExpression node,
      @Nonnull List<Branch> path) {
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceLiteralStringExpression(
      @Nonnull LiteralStringExpression node,
      @Nonnull List<Branch> path) {
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceLiteralRegexExpression(
      @Nonnull LiteralRegExpExpression node,
      @Nonnull List<Branch> path) {
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceLiteralNumericExpression(
      @Nonnull LiteralNumericExpression node,
      @Nonnull List<Branch> path) {
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceLiteralNullExpression(
      @Nonnull LiteralNullExpression node,
      @Nonnull List<Branch> path) {
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceFunctionExpression(
      @Nonnull FunctionExpression node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<DirtyState<Identifier>> id,
      @Nonnull List<DirtyState<Identifier>> params,
      @Nonnull DirtyState<FunctionBody> body) {
    DirtyState<Maybe<Identifier>> i = op(id);
    DirtyState<List<Identifier>> p = l(params);
    if (i.dirty || p.dirty || body.dirty) {
      return dirty((Expression) new FunctionExpression(i.node, p.node, body.node));
    }
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceStaticMemberExpression(
      @Nonnull StaticMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> object,
      @Nonnull DirtyState<Identifier> property) {
    if (object.dirty || property.dirty) {
      return dirty((Expression) new StaticMemberExpression(object.node, property.node));
    }
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceComputedMemberExpression(
      @Nonnull ComputedMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> object,
      @Nonnull final DirtyState<Expression> expression) {
    if (object.dirty || expression.dirty) {
      return dirty((Expression) new ComputedMemberExpression(object.node, expression.node));
    }
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceObjectExpression(
      @Nonnull ObjectExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<DirtyState<ObjectProperty>> properties) {
    DirtyState<List<ObjectProperty>> p = l(properties);
    if (p.dirty) {
      return dirty((Expression) new ObjectExpression(p.node));
    }
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceBinaryExpression(
      @Nonnull final BinaryExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> left,
      @Nonnull final DirtyState<Expression> right) {
    return get(node, left.bind(l -> right.bindLast(r -> new BinaryExpression(node.operator, l, r))));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceAssignmentExpression(
      @Nonnull AssignmentExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> binding,
      @Nonnull DirtyState<Expression> expression) {
    return get(node, binding.bind(l -> expression.bindLast(r -> new AssignmentExpression(node.operator, l, r))));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceArrayExpression(
      @Nonnull ArrayExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<Maybe<DirtyState<Expression>>> elements) {
    return LazyCloner.<Expression>get(node, lo(elements).bindLast(ArrayExpression::new));
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceNewExpression(
      @Nonnull NewExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> callee,
      @Nonnull final List<DirtyState<Expression>> arguments) {
    DirtyState<List<Expression>> args = l(arguments);
    if (callee.dirty || args.dirty) {
      return dirty((Expression) new NewExpression(callee.node, args.node));
    }
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceCallExpression(
      @Nonnull CallExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> callee,
      @Nonnull final List<DirtyState<Expression>> arguments) {
    DirtyState<List<Expression>> args = l(arguments);
    if (callee.dirty || args.dirty) {
      return dirty((Expression) new CallExpression(callee.node, args.node));
    }
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reducePostfixExpression(
      @Nonnull final PostfixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> operand) {
    if (operand.dirty) {
      return dirty((Expression) new PostfixExpression(node.operator, operand.node));
    }
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reducePrefixExpression(
      @Nonnull final PrefixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> operand) {
    if (operand.dirty) {
      return dirty((Expression) new PrefixExpression(node.operator, operand.node));
    }
    return clean((Expression) node);
  }

  @Nonnull
  @Override
  public DirtyState<Expression> reduceConditionalExpression(
      @Nonnull ConditionalExpression node,
      @Nonnull List<Branch> path,
      @Nonnull final DirtyState<Expression> test,
      @Nonnull final DirtyState<Expression> consequent,
      @Nonnull final DirtyState<Expression> alternate) {
    return get(node, test.bind(t -> consequent.bind(c -> alternate.bindLast(a -> new ConditionalExpression(t, c, a)))));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceFunctionDeclaration(
      @Nonnull FunctionDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Identifier> id,
      @Nonnull final List<DirtyState<Identifier>> params,
      @Nonnull final DirtyState<FunctionBody> body) {
    return get(node, id.bind(id1 -> l(params).bind(params1 -> body.bindLast(body1 -> new FunctionDeclaration(id1,
        params1, body1)))));
  }

  @Nonnull
  @Override
  public DirtyState<Directive> reduceUseStrictDirective(@Nonnull UseStrictDirective node, @Nonnull List<Branch> path) {
    return clean((Directive) node);
  }

  @Nonnull
  @Override
  public DirtyState<Directive> reduceUnknownDirective(@Nonnull UnknownDirective node, @Nonnull List<Branch> path) {
    return clean((Directive) node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceBlockStatement(
      @Nonnull BlockStatement node,
      @Nonnull List<Branch> path,
      @Nonnull final DirtyState<Block> block) {
    if (block.dirty) {
      return dirty((Statement) new BlockStatement(block.node));
    }
    return clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceBreakStatement(
      @Nonnull BreakStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<DirtyState<Identifier>> label) {
    if (label.isJust() && label.just().dirty) {
      return dirty((Statement) new BreakStatement(Maybe.just(label.just().node)));
    }
    return clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<CatchClause> reduceCatchClause(
      @Nonnull CatchClause node,
      @Nonnull List<Branch> path,
      @Nonnull final DirtyState<Identifier> param,
      @Nonnull final DirtyState<Block> body) {
    return get(node, param.bind(p -> body.bindLast(s -> new CatchClause(p, s))));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceContinueStatement(
      @Nonnull ContinueStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<DirtyState<Identifier>> label) {
    if (label.isJust() && label.just().dirty) {
      return dirty((Statement) new ContinueStatement(Maybe.just(label.just().node)));
    }
    return clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceDebuggerStatement(@Nonnull DebuggerStatement node, @Nonnull List<Branch> path) {
    return clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceDoWhileStatement(
      @Nonnull DoWhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Statement> body,
      @Nonnull final DirtyState<Expression> test) {
    return get(node, body.bind(body1 -> test.bindLast(test1 -> new DoWhileStatement(body1, test1))));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceEmptyStatement(@Nonnull EmptyStatement node, @Nonnull List<Branch> path) {
    return clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceExpressionStatement(
      @Nonnull ExpressionStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> expression) {
    if (expression.dirty) {
      return dirty((Statement) new ExpressionStatement(expression.node));
    }
    return clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceForInStatement(
      @Nonnull ForInStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Either<DirtyState<VariableDeclaration>, DirtyState<Expression>> left,
      @Nonnull final DirtyState<Expression> right,
      @Nonnull final DirtyState<Statement> body) {
    boolean leftDirty = left.either(x -> x.dirty, x -> x.dirty);
    Either<VariableDeclaration, Expression> leftNode = left.map(x -> x.node, x -> x.node);
    if (leftDirty || right.dirty || body.dirty) {
      return dirty((Statement) new ForInStatement(leftNode, right.node, body.node));
    }
    return clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceForStatement(
      @Nonnull ForStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<Either<DirtyState<VariableDeclaration>, DirtyState<Expression>>> init,
      @Nonnull final Maybe<DirtyState<Expression>> test,
      @Nonnull final Maybe<DirtyState<Expression>> update,
      @Nonnull final DirtyState<Statement> body) {
    boolean iDirty = init.map(x -> x.either(y -> y.dirty, y -> y.dirty)).orJust(false);
    Maybe<Either<VariableDeclaration, Expression>> iNode = init.map(x -> x.map(y -> y.node, y -> y.node));
    DirtyState<Maybe<Expression>> t = op(test), u = op(update);
    if (iDirty || t.dirty || u.dirty || body.dirty) {
      return dirty((Statement) new ForStatement(iNode, t.node, u.node, body.node));
    }
    return clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceIfStatement(
      @Nonnull IfStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> test,
      @Nonnull final DirtyState<Statement> consequent,
      @Nonnull final Maybe<DirtyState<Statement>> alternate) {
    return get(node, test.bind(test1 -> consequent.bind(consequent1 -> op(alternate).bindLast(
        alternate1 -> new IfStatement(test1, consequent1, alternate1)))));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceLabeledStatement(
      @Nonnull LabeledStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Identifier> label,
      @Nonnull final DirtyState<Statement> body) {
    return get(node, label.bind(label1 -> body.bindLast(body1 -> new LabeledStatement(label1, body1))));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceReturnStatement(
      @Nonnull ReturnStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<DirtyState<Expression>> argument) {
    if (argument.isNothing() || !argument.just().dirty) {
      return clean((Statement) node);
    }
    return DirtyState.dirty((Statement) new ReturnStatement(Maybe.just(argument.just().node)));
  }

  @Nonnull
  @Override
  public DirtyState<SwitchCase> reduceSwitchCase(
      @Nonnull SwitchCase node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> test,
      @Nonnull final List<DirtyState<Statement>> consequent) {
    return get(node, test.bind(test1 -> l(consequent).bindLast(consequent1 -> new SwitchCase(test1, consequent1))));
  }

  @Nonnull
  @Override
  public DirtyState<SwitchDefault> reduceSwitchDefault(
      @Nonnull SwitchDefault node,
      @Nonnull List<Branch> path,
      @Nonnull List<DirtyState<Statement>> consequent) {
    return get(node, l(consequent).bindLast(SwitchDefault::new));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceSwitchStatement(
      @Nonnull final SwitchStatement node,
      @Nonnull final List<Branch> path,
      @Nonnull DirtyState<Expression> discriminant,
      @Nonnull final List<DirtyState<SwitchCase>> cases) {
    return get(node, discriminant.bind(discriminant1 -> l(cases).bindLast(
        cases1 -> CloneReducer.INSTANCE.reduceSwitchStatement(node, path, discriminant1, cases1))));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceSwitchStatementWithDefault(
      @Nonnull SwitchStatementWithDefault node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> discriminant,
      @Nonnull List<DirtyState<SwitchCase>> cases,
      @Nonnull DirtyState<SwitchDefault> defaultCase,
      @Nonnull List<DirtyState<SwitchCase>> postDefaultCases) {
    DirtyState<List<SwitchCase>> cs = l(cases);
    DirtyState<List<SwitchCase>> pcs = l(postDefaultCases);

    if (discriminant.dirty || cs.dirty || defaultCase.dirty || pcs.dirty) {
      return dirty((Statement) new SwitchStatementWithDefault(discriminant.node, cs.node, defaultCase.node, pcs.node));
    }
    return clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceThrowStatement(
      @Nonnull ThrowStatement node,
      @Nonnull List<Branch> path,
      @Nonnull final DirtyState<Expression> argument) {
    return get(node, argument.bindLast(ThrowStatement::new));
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceTryCatchStatement(
      @Nonnull TryCatchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Block> block,
      @Nonnull DirtyState<CatchClause> catchClause) {
    if (block.dirty || catchClause.dirty) {
      return DirtyState.dirty(new TryCatchStatement(block.node, catchClause.node));
    }
    return DirtyState.clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceTryFinallyStatement(
      @Nonnull TryFinallyStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Block> block,
      @Nonnull Maybe<DirtyState<CatchClause>> catchClause,
      @Nonnull DirtyState<Block> finalizer) {
    DirtyState<Maybe<CatchClause>> op = op(catchClause);
    if (block.dirty || op.dirty || finalizer.dirty) {
      return dirty(new TryFinallyStatement(block.node, op.node, finalizer.node));
    }
    return clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceVariableDeclarationStatement(
      @Nonnull final VariableDeclarationStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<VariableDeclaration> declaration) {
    if (declaration.dirty) {
      return dirty((Statement) new VariableDeclarationStatement(declaration.node));
    }
    return clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<VariableDeclaration> reduceVariableDeclaration(
      @Nonnull VariableDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull NonEmptyList<DirtyState<VariableDeclarator>> declarators) {
    DirtyState<NonEmptyList<VariableDeclarator>> ds = l(declarators);
    if (ds.dirty) {
      return dirty(new VariableDeclaration(node.kind, ds.node));
    }
    return clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceWhileStatement(
      @Nonnull WhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> test,
      @Nonnull final DirtyState<Statement> body) {
    if (test.dirty || body.dirty) {
      return dirty((Statement) new WhileStatement(test.node, body.node));
    }
    return clean((Statement) node);
  }

  @Nonnull
  @Override
  public DirtyState<Statement> reduceWithStatement(
      @Nonnull WithStatement node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Expression> object,
      @Nonnull final DirtyState<Statement> body) {
    return get(node, object.bind(object1 -> body.bindLast(body1 -> new WithStatement(object1, body1))));
  }

  @Nonnull
  @Override
  public DirtyState<ObjectProperty> reduceDataProperty(
      @Nonnull DataProperty node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<PropertyName> key,
      @Nonnull final DirtyState<Expression> value) {
    if (key.dirty || value.dirty) {
      return dirty((ObjectProperty) new DataProperty(key.node, value.node));
    }
    return clean((ObjectProperty) node);
  }

  @Nonnull
  @Override
  public DirtyState<ObjectProperty> reduceGetter(
      @Nonnull Getter node,
      @Nonnull List<Branch> path,
      @Nonnull final DirtyState<PropertyName> key,
      @Nonnull final DirtyState<FunctionBody> body) {
    return get(node, key.bind(propertyName -> body.bindLast(programBody -> new Getter(propertyName, programBody))));
  }

  @Nonnull
  @Override
  public DirtyState<ObjectProperty> reduceSetter(
      @Nonnull Setter node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<PropertyName> key,
      @Nonnull final DirtyState<Identifier> parameter,
      @Nonnull final DirtyState<FunctionBody> body) {
    if (key.dirty || parameter.dirty || body.dirty) {
      return dirty((ObjectProperty) new Setter(key.node, parameter.node, body.node));
    }
    return clean((ObjectProperty) node);
  }

  @Nonnull
  @Override
  public DirtyState<PropertyName> reducePropertyName(@Nonnull PropertyName node, @Nonnull List<Branch> path) {
    return clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<FunctionBody> reduceFunctionBody(
      @Nonnull FunctionBody node,
      @Nonnull List<Branch> path,
      @Nonnull List<DirtyState<Directive>> directives,
      @Nonnull final List<DirtyState<Statement>> sourceElements) {
    DirtyState<List<Directive>> dirs = l(directives);
    DirtyState<List<Statement>> ses = l(sourceElements);
    if (dirs.dirty || ses.dirty) {
      return DirtyState.dirty(new FunctionBody(dirs.node, ses.node));
    }
    return clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<VariableDeclarator> reduceVariableDeclarator(
      @Nonnull VariableDeclarator node,
      @Nonnull List<Branch> path,
      @Nonnull DirtyState<Identifier> id,
      @Nonnull final Maybe<DirtyState<Expression>> init) {
    if (id.dirty) {
      return dirty(new VariableDeclarator(id.node, init.map(expr -> expr.node)));
    }
    if (init.isJust() && init.just().dirty) {
      return dirty(new VariableDeclarator(id.node, Maybe.just(init.just().node)));
    }
    return clean(node);
  }

  @Nonnull
  @Override
  public DirtyState<Block> reduceBlock(
      @Nonnull Block node,
      @Nonnull List<Branch> path,
      @Nonnull List<DirtyState<Statement>> statements) {
    DirtyState<List<Statement>> ds = l(statements);
    if (ds.dirty) {
      return DirtyState.dirty(new Block(ds.node));
    }
    return DirtyState.clean(node);
  }
}
