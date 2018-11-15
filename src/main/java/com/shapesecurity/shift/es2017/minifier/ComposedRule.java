///*
// * Copyright 2014 Shape Security, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.shapesecurity.shift.minifier;
//
//import Block;
//import CatchClause;
//import Directive;
//import Expression;
//import FunctionBody;
//import com.shapesecurity.shift.ast.Identifier;
//import Script;
//import Statement;
//import SwitchCase;
//import SwitchDefault;
//import VariableDeclaration;
//import VariableDeclarator;
//import com.shapesecurity.shift.ast.directive.UnknownDirective;
//import com.shapesecurity.shift.ast.directive.UseStrictDirective;
//import com.shapesecurity.shift.ast.expression.ArrayExpression;
//import com.shapesecurity.shift.ast.expression.BinaryExpression;
//import com.shapesecurity.shift.ast.expression.CallExpression;
//import com.shapesecurity.shift.ast.expression.ComputedMemberExpression;
//import com.shapesecurity.shift.ast.expression.ConditionalExpression;
//import com.shapesecurity.shift.ast.expression.FunctionExpression;
//import com.shapesecurity.shift.ast.expression.IdentifierExpression;
//import com.shapesecurity.shift.ast.expression.LiteralBooleanExpression;
//import com.shapesecurity.shift.ast.expression.LiteralNullExpression;
//import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
//import com.shapesecurity.shift.ast.expression.LiteralRegExpExpression;
//import com.shapesecurity.shift.ast.expression.LiteralStringExpression;
//import com.shapesecurity.shift.ast.expression.NewExpression;
//import com.shapesecurity.shift.ast.expression.ObjectExpression;
//import com.shapesecurity.shift.ast.expression.PostfixExpression;
//import com.shapesecurity.shift.ast.expression.PrefixExpression;
//import com.shapesecurity.shift.ast.expression.StaticMemberExpression;
//import com.shapesecurity.shift.ast.expression.ThisExpression;
//import com.shapesecurity.shift.ast.property.DataProperty;
//import com.shapesecurity.shift.ast.property.Getter;
//import com.shapesecurity.shift.ast.property.ObjectProperty;
//import com.shapesecurity.shift.ast.property.PropertyName;
//import com.shapesecurity.shift.ast.property.Setter;
//import com.shapesecurity.shift.ast.statement.BlockStatement;
//import com.shapesecurity.shift.ast.statement.BreakStatement;
//import com.shapesecurity.shift.ast.statement.ContinueStatement;
//import com.shapesecurity.shift.ast.statement.DebuggerStatement;
//import com.shapesecurity.shift.ast.statement.DoWhileStatement;
//import com.shapesecurity.shift.ast.statement.EmptyStatement;
//import com.shapesecurity.shift.ast.statement.ExpressionStatement;
//import com.shapesecurity.shift.ast.statement.ForInStatement;
//import com.shapesecurity.shift.ast.statement.ForStatement;
//import com.shapesecurity.shift.ast.statement.FunctionDeclaration;
//import com.shapesecurity.shift.ast.statement.IfStatement;
//import com.shapesecurity.shift.ast.statement.LabeledStatement;
//import com.shapesecurity.shift.ast.statement.ReturnStatement;
//import com.shapesecurity.shift.ast.statement.SwitchStatement;
//import com.shapesecurity.shift.ast.statement.SwitchStatementWithDefault;
//import com.shapesecurity.shift.ast.statement.ThrowStatement;
//import com.shapesecurity.shift.ast.statement.TryCatchStatement;
//import com.shapesecurity.shift.ast.statement.TryFinallyStatement;
//import com.shapesecurity.shift.ast.statement.VariableDeclarationStatement;
//import com.shapesecurity.shift.ast.statement.WhileStatement;
//import com.shapesecurity.shift.ast.statement.WithStatement;
//import com.shapesecurity.shift.visitor.DirtyState;
//
//import javax.annotation.Nonnull;
//
//public class ComposedRule<T extends MinificationRule> extends MinificationRule {
//  @Nonnull
//  private final T[] rules;
//
//  public ComposedRule(@Nonnull T[] rules) {
//    super();
//    this.rules = rules;
//  }
//
//  @Nonnull
//  private DirtyState<Statement> t(@Nonnull Statement node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<Statement> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  private DirtyState<Directive> t(@Nonnull Directive node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<Directive> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  private DirtyState<Expression> t(@Nonnull Expression node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<Expression> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  private DirtyState<PropertyName> t(@Nonnull PropertyName node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<PropertyName> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  private DirtyState<ObjectProperty> t(@Nonnull ObjectProperty node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<ObjectProperty> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  private DirtyState<SwitchCase> t(@Nonnull SwitchCase node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<SwitchCase> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  private DirtyState<SwitchDefault> t(@Nonnull SwitchDefault node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<SwitchDefault> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<CatchClause> transform(@Nonnull CatchClause node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<CatchClause> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Block> transform(@Nonnull Block node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<Block> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<VariableDeclarator> transform(@Nonnull VariableDeclarator node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<VariableDeclarator> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Directive> transform(@Nonnull UnknownDirective node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Directive> transform(@Nonnull UseStrictDirective node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull ArrayExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull BinaryExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull CallExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull ComputedMemberExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull ConditionalExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull FunctionExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull IdentifierExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralBooleanExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralNullExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralNumericExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralRegExpExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralStringExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull NewExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull ObjectExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull PostfixExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull PrefixExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull StaticMemberExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull ThisExpression node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Identifier> transform(@Nonnull Identifier node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<Identifier> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<FunctionBody> transform(@Nonnull FunctionBody node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<FunctionBody> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Script> transform(@Nonnull Script node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<Script> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<PropertyName> transform(@Nonnull PropertyName node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<ObjectProperty> transform(@Nonnull Getter node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<ObjectProperty> transform(@Nonnull DataProperty node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<ObjectProperty> transform(@Nonnull Setter node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull BlockStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull BreakStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ContinueStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull DebuggerStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull DoWhileStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull EmptyStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ExpressionStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ForInStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ForStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull FunctionDeclaration node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull IfStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull LabeledStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ReturnStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull SwitchStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull SwitchStatementWithDefault node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ThrowStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull TryCatchStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull VariableDeclarationStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull WhileStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull WithStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<SwitchCase> transform(@Nonnull SwitchCase node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<SwitchDefault> transform(@Nonnull SwitchDefault node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull TryFinallyStatement node) {
//    return t(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<VariableDeclaration> transform(@Nonnull VariableDeclaration node) {
//    boolean dirty = false;
//    for (T rule : this.rules) {
//      DirtyState<VariableDeclaration> transform = rule.transform(node);
//      dirty = dirty || transform.dirty;
//      node = transform.node;
//    }
//    return new DirtyState<>(node, dirty);
//  }
//}
