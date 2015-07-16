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
//
//package com.shapesecurity.shift.path;
//
//import com.shapesecurity.functional.F;
//import com.shapesecurity.functional.F2;
//import com.shapesecurity.functional.data.Either;
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.functional.data.NonEmptyImmutableList;
//import com.shapesecurity.shift.ast.Block;
//import com.shapesecurity.shift.ast.CatchClause;
//import com.shapesecurity.shift.ast.Directive;
//import com.shapesecurity.shift.ast.EitherNode;
//import com.shapesecurity.shift.ast.Expression;
//import com.shapesecurity.shift.ast.FunctionBody;
//import com.shapesecurity.shift.ast.Identifier;
//import com.shapesecurity.shift.ast.ListNode;
//import com.shapesecurity.shift.ast.MaybeNode;
//import com.shapesecurity.shift.ast.Node;
//import com.shapesecurity.shift.ast.NonEmptyListNode;
//import com.shapesecurity.shift.ast.Script;
//import com.shapesecurity.shift.ast.Statement;
//import com.shapesecurity.shift.ast.SwitchCase;
//import com.shapesecurity.shift.ast.SwitchDefault;
//import com.shapesecurity.shift.ast.VariableDeclaration;
//import com.shapesecurity.shift.ast.VariableDeclarator;
//import com.shapesecurity.shift.ast.expression.ArrayExpression;
//import com.shapesecurity.shift.ast.expression.AssignmentExpression;
//import com.shapesecurity.shift.ast.expression.BinaryExpression;
//import com.shapesecurity.shift.ast.expression.CallExpression;
//import com.shapesecurity.shift.ast.expression.ComputedMemberExpression;
//import com.shapesecurity.shift.ast.expression.ConditionalExpression;
//import com.shapesecurity.shift.ast.expression.FunctionExpression;
//import com.shapesecurity.shift.ast.expression.IdentifierExpression;
//import com.shapesecurity.shift.ast.expression.NewExpression;
//import com.shapesecurity.shift.ast.expression.ObjectExpression;
//import com.shapesecurity.shift.ast.expression.PostfixExpression;
//import com.shapesecurity.shift.ast.expression.PrefixExpression;
//import com.shapesecurity.shift.ast.expression.StaticMemberExpression;
//import com.shapesecurity.shift.ast.property.DataProperty;
//import com.shapesecurity.shift.ast.property.Getter;
//import com.shapesecurity.shift.ast.property.ObjectProperty;
//import com.shapesecurity.shift.ast.property.PropertyName;
//import com.shapesecurity.shift.ast.property.Setter;
//import com.shapesecurity.shift.ast.statement.BlockStatement;
//import com.shapesecurity.shift.ast.statement.BreakStatement;
//import com.shapesecurity.shift.ast.statement.ContinueStatement;
//import com.shapesecurity.shift.ast.statement.DoWhileStatement;
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
//import com.shapesecurity.shift.ast.types.EitherType;
//import com.shapesecurity.shift.ast.types.GenType;
//import com.shapesecurity.shift.ast.types.ListType;
//import com.shapesecurity.shift.ast.types.MaybeType;
//import com.shapesecurity.shift.ast.types.NonEmptyListType;
//import com.shapesecurity.shift.ast.types.Type;
//
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//class TypedBranches {
//
//  private static <P, A, B> F<P, EitherNode<A, B>> either(F<P, Either<A, B>> f, GenType a, GenType b) {
//    return n -> new EitherNode<>(f.apply(n), a, b);
//  }
//
//  private static <P, A, B> F2<P, EitherNode<A, B>, P> either(F2<P, Either<A, B>, P> f) {
//    return (p, a) -> f.apply(p, a.either);
//  }
//
//  private static <P, A> F<P, MaybeNode<A>> maybe(F<P, Maybe<A>> f, GenType a) {
//    return n -> new MaybeNode<>(f.apply(n), MaybeType.from(a));
//  }
//
//  private static <P, A> F2<P, MaybeNode<A>, P> maybe(F2<P, Maybe<A>, P> f) {
//    return (p, a) -> f.apply(p, a.maybe);
//  }
//
//  private static <P, A> F<P, ListNode<A>> list(F<P, ImmutableList<A>> f, GenType a) {
//    return n -> new ListNode<>(f.apply(n), ListType.from(a));
//  }
//
//  private static <P, A> F2<P, ListNode<A>, P> list(F2<P, ImmutableList<A>, P> f) {
//    return (p, a) -> f.apply(p, a.list);
//  }
//
//  private static <P, A> F<P, NonEmptyListNode<A>> nel(F<P, NonEmptyImmutableList<A>> f, GenType a) {
//    return n -> new NonEmptyListNode<>(f.apply(n), NonEmptyListType.from(a));
//  }
//
//  private static <P, A> F2<P, NonEmptyListNode<A>, P> nel(F2<P, NonEmptyImmutableList<A>, P> f) {
//    return (p, a) -> f.apply(p, a.list);
//  }
//
//  private static <P extends Node, C extends Node> TypedBranch<P, C> def(
//      @NotNull GenType parentType,
//      @NotNull GenType childType,
//      @NotNull F<P, C> view,
//      @NotNull F2<P, C, P> set) {
//    return new TypedBranch<P, C>(parentType, childType) {
//
//      @Nullable
//      @Override
//      public C view(@NotNull P parent) {
//        return view.apply(parent);
//      }
//
//      @NotNull
//      @Override
//      public P set(@NotNull P parent, @NotNull C child) {
//        return set.apply(parent, child);
//      }
//    };
//  }
//
//  private static <P extends Node, C> TypedBranch<P, ListNode<C>> defl(
//      @NotNull GenType parentType,
//      @NotNull GenType childType,
//      @NotNull F<P, ImmutableList<C>> view,
//      @NotNull F2<P, ImmutableList<C>, P> set) {
//    F<P, ListNode<C>> lView = list(view, childType);
//    F2<P, ListNode<C>, P> lSet = list(set);
//    return new TypedBranch<P, ListNode<C>>(parentType, ListType.from(childType)) {
//
//      @Nullable
//      @Override
//      public ListNode<C> view(@NotNull P parent) {
//        return lView.apply(parent);
//      }
//
//      @NotNull
//      @Override
//      public P set(@NotNull P parent, @NotNull ListNode<C> child) {
//        return lSet.apply(parent, child);
//      }
//    };
//  }
//
//  private static <P extends Node, C> TypedBranch<P, NonEmptyListNode<C>> defnel(
//      @NotNull GenType parentType,
//      @NotNull GenType childType,
//      @NotNull F<P, NonEmptyImmutableList<C>> view,
//      @NotNull F2<P, NonEmptyImmutableList<C>, P> set) {
//    F<P, NonEmptyListNode<C>> nelView = nel(view, childType);
//    F2<P, NonEmptyListNode<C>, P> nelSet = nel(set);
//    return new TypedBranch<P, NonEmptyListNode<C>>(parentType, NonEmptyListType.from(childType)) {
//
//      @Nullable
//      @Override
//      public NonEmptyListNode<C> view(@NotNull P parent) {
//        return nelView.apply(parent);
//      }
//
//      @NotNull
//      @Override
//      public P set(@NotNull P parent, @NotNull NonEmptyListNode<C> child) {
//        return nelSet.apply(parent, child);
//      }
//    };
//  }
//
//  private static <P extends Node, C> TypedBranch<P, MaybeNode<C>> defm(
//      @NotNull GenType parentType,
//      @NotNull GenType childType,
//      @NotNull F<P, Maybe<C>> view,
//      @NotNull F2<P, Maybe<C>, P> set) {
//    F<P, MaybeNode<C>> mView = maybe(view, childType);
//    F2<P, MaybeNode<C>, P> mSet = maybe(set);
//    return new TypedBranch<P, MaybeNode<C>>(parentType, MaybeType.from(childType)) {
//
//      @Nullable
//      @Override
//      public MaybeNode<C> view(@NotNull P parent) {
//        return mView.apply(parent);
//      }
//
//      @NotNull
//      @Override
//      public P set(@NotNull P parent, @NotNull MaybeNode<C> child) {
//        return mSet.apply(parent, child);
//      }
//    };
//  }
//
//  private static <P extends Node, A, B> TypedBranch<P, EitherNode<A, B>> defe(
//      @NotNull GenType parentType,
//      @NotNull GenType a,
//      @NotNull GenType b,
//      @NotNull F<P, Either<A, B>> view,
//      @NotNull F2<P, Either<A, B>, P> set) {
//    F<P, EitherNode<A, B>> eView = either(view, a, b);
//    F2<P, EitherNode<A, B>, P> eSet = either(set);
//    return new TypedBranch<P, EitherNode<A, B>>(parentType, EitherType.from(a, b)) {
//
//      @Nullable
//      @Override
//      public EitherNode<A, B> view(@NotNull P parent) {
//        return eView.apply(parent);
//      }
//
//      @NotNull
//      @Override
//      public P set(@NotNull P parent, @NotNull EitherNode<A, B> child) {
//        return eSet.apply(parent, child);
//      }
//    };
//  }
//
//  public static final TypedBranch<IfStatement, MaybeNode<Statement>> IfStatement_alternate =
//      defm(
//          Type.IfStatement,
//          MaybeType.STATEMENT,
//          IfStatement::getAlternate,
//          IfStatement::setAlternate);
//
//
//  public static final TypedBranch<ConditionalExpression, Expression> ConditionalExpression_alternate =
//      def(
//          Type.ConditionalExpression,
//          Type.Expression,
//          ConditionalExpression::getAlternate,
//          ConditionalExpression::setAlternate);
//
//  public static final TypedBranch<CallExpression, ListNode<Expression>> CallExpression_arguments =
//      defl(
//          Type.CallExpression,
//          Type.Expression,
//          CallExpression::getArguments,
//          CallExpression::setArguments);
//
//  public static final TypedBranch<NewExpression, ListNode<Expression>> NewExpression_arguments =
//      defl(Type.NewExpression, Type.Expression, NewExpression::getArguments, NewExpression::setArguments);
//
//  public static final TypedBranch<AssignmentExpression, Expression> AssignmentExpression_binding =
//      def(
//          Type.AssignmentExpression,
//          Type.Expression,
//          AssignmentExpression::getBinding,
//          AssignmentExpression::setBinding);
//
//  public static final TypedBranch<CatchClause, Identifier> CatchClause_binding =
//      def(
//          Type.CatchClause,
//          Type.Identifier,
//          CatchClause::getBinding,
//          CatchClause::setBinding);
//
//  public static final TypedBranch<VariableDeclarator, Identifier> VariableDeclarator_binding =
//      def(
//          Type.VariableDeclarator,
//          Type.Identifier,
//          VariableDeclarator::getBinding,
//          VariableDeclarator::setBinding);
//
//  public static final TypedBranch<BlockStatement, Block> BlockStatement_block =
//      def(Type.BlockStatement, Type.Block, BlockStatement::getBlock, BlockStatement::setBlock);
//
//  public static final TypedBranch<Getter, FunctionBody> Getter_body =
//      def(Type.Getter, Type.FunctionBody, Getter::getBody, Getter::setBody);
//
//  public static final TypedBranch<Setter, FunctionBody> Setter_body =
//      def(Type.Setter, Type.FunctionBody, Setter::getBody, Setter::setBody);
//
//  public static final TypedBranch<FunctionDeclaration, FunctionBody> FunctionDeclaration_body =
//      def(
//          Type.FunctionDeclaration,
//          Type.FunctionBody,
//          FunctionDeclaration::getBody,
//          FunctionDeclaration::setBody);
//
//  public static final TypedBranch<FunctionExpression, FunctionBody> FunctionExpression_body =
//      def(
//          Type.FunctionExpression,
//          Type.FunctionBody,
//          FunctionExpression::getBody,
//          FunctionExpression::setBody);
//
//  public static final TypedBranch<DoWhileStatement, Statement> DoWhileStatement_body =
//      def(Type.DoWhileStatement, Type.Statement, DoWhileStatement::getBody, DoWhileStatement::setBody);
//
//  public static final TypedBranch<ForInStatement, Statement> ForInStatement_body =
//      def(Type.ForInStatement, Type.Statement, ForInStatement::getBody, ForInStatement::setBody);
//
//  public static final TypedBranch<ForStatement, Statement> ForStatement_body =
//      def(Type.ForStatement, Type.Statement, ForStatement::getBody, ForStatement::setBody);
//
//  public static final TypedBranch<WhileStatement, Statement> WhileStatement_body =
//      def(Type.WhileStatement, Type.Statement, WhileStatement::getBody, WhileStatement::setBody);
//
//  public static final TypedBranch<LabeledStatement, Statement> LabeledStatement_body =
//      def(Type.LabeledStatement, Type.Statement, LabeledStatement::getBody, LabeledStatement::setBody);
//
//  public static final TypedBranch<TryCatchStatement, Block> TryCatchStatement_body =
//      def(Type.TryCatchStatement, Type.Block, TryCatchStatement::getBody, TryCatchStatement::setBody);
//
//  public static final TypedBranch<TryFinallyStatement, Block> TryFinallyStatement_body =
//      def(
//          Type.TryFinallyStatement,
//          Type.Block,
//          TryFinallyStatement::getBody,
//          TryFinallyStatement::setBody);
//
//  public static final TypedBranch<WithStatement, Statement> WithStatement_body =
//      def(Type.WithStatement, Type.Statement, WithStatement::getBody, WithStatement::setBody);
//
//  public static final TypedBranch<CatchClause, Block> CatchClause_body =
//      def(Type.CatchClause, Type.Block, CatchClause::getBody, CatchClause::setBody);
//
//  public static final TypedBranch<Script, FunctionBody> Script_body =
//      def(Type.Script, Type.FunctionBody, Script::getBody, Script::setBody);
//
//  public static final TypedBranch<CallExpression, Expression> CallExpression_callee =
//      def(Type.CallExpression, Type.Expression, CallExpression::getCallee, CallExpression::setCallee);
//
//  public static final TypedBranch<NewExpression, Expression> NewExpression_callee =
//      def(Type.NewExpression, Type.Expression, NewExpression::getCallee, NewExpression::setCallee);
//
//  public static final TypedBranch<SwitchStatement, ListNode<SwitchCase>> SwitchStatement_cases =
//      defl(Type.SwitchStatement, Type.SwitchCase, SwitchStatement::getCases, SwitchStatement::setCases);
//
//  public static final TypedBranch<TryCatchStatement, CatchClause> TryCatchStatement_catchClause =
//      def(
//          Type.TryCatchStatement,
//          Type.CatchClause,
//          TryCatchStatement::getCatchClause,
//          TryCatchStatement::setCatchClause);
//
//  public static final TypedBranch<TryFinallyStatement, MaybeNode<CatchClause>> TryFinallyStatement_catchClause =
//      defm(
//          Type.TryFinallyStatement, Type.CatchClause,
//          TryFinallyStatement::getCatchClause,
//          TryFinallyStatement::setCatchClause);
//
//  public static final TypedBranch<ConditionalExpression, Expression> ConditionalExpression_consequent =
//      def(
//          Type.ConditionalExpression,
//          Type.Expression,
//          ConditionalExpression::getConsequent,
//          ConditionalExpression::setConsequent);
//
//  public static final TypedBranch<IfStatement, Statement> IfStatement_consequent =
//      def(
//          Type.IfStatement,
//          Type.Statement,
//          IfStatement::getConsequent,
//          IfStatement::setConsequent);
//
//  public static final TypedBranch<SwitchCase, ListNode<Statement>> SwitchCase_consequent =
//      defl(
//          Type.SwitchCase,
//          Type.Statement,
//          SwitchCase::getConsequent,
//          SwitchCase::setConsequent);
//
//  public static final TypedBranch<SwitchDefault, ListNode<Statement>> SwitchDefault_consequent =
//      defl(
//          Type.SwitchDefault,
//          Type.Statement,
//          SwitchDefault::getConsequent,
//          SwitchDefault::setConsequent);
//
//  public static final TypedBranch<VariableDeclarationStatement, VariableDeclaration>
//      VariableDeclarationStatement_declaration =
//      def(
//          Type.VariableDeclarationStatement,
//          Type.VariableDeclaration,
//          VariableDeclarationStatement::getDeclaration,
//          VariableDeclarationStatement::setDeclaration);
//
//  public static final TypedBranch<VariableDeclaration, NonEmptyListNode<VariableDeclarator>>
//      VariableDeclaration_declarators =
//      defnel(
//          Type.VariableDeclaration,
//          Type.VariableDeclarator,
//          VariableDeclaration::getDeclarators,
//          VariableDeclaration::setDeclarators);
//
//  public static final TypedBranch<SwitchStatementWithDefault, SwitchDefault> SwitchStatementWithDefault_defaultCase =
//      def(
//          Type.SwitchStatementWithDefault,
//          Type.SwitchDefault,
//          SwitchStatementWithDefault::getDefaultCase,
//          SwitchStatementWithDefault::setDefaultCase);
//
//  public static final TypedBranch<FunctionBody, ListNode<Directive>> FunctionBody_directives =
//      defl(
//          Type.FunctionBody,
//          Type.Directive,
//          FunctionBody::getDirectives,
//          FunctionBody::setDirectives
//      );
//
//  public static final TypedBranch<SwitchStatement, Expression> SwitchStatement_discriminant =
//      def(Type.SwitchStatement, Type.Expression, SwitchStatement::getDiscriminant, SwitchStatement::setDiscriminant);
//
//  public static final TypedBranch<SwitchStatementWithDefault, Expression> SwitchStatementWithDefault_discriminant =
//      def(
//          Type.SwitchStatementWithDefault,
//          Type.Expression,
//          SwitchStatementWithDefault::getDiscriminant,
//          SwitchStatementWithDefault::setDiscriminant);
//
//  public static final TypedBranch<ArrayExpression, ListNode<Maybe<Expression>>> ArrayExpression_elements =
//      defl(
//          Type.ArrayExpression,
//          MaybeType.EXPRESSION,
//          ArrayExpression::getElements,
//          ArrayExpression::setElements);
//
//  public static final TypedBranch<AssignmentExpression, Expression> AssignmentExpression_expression =
//      def(
//          Type.AssignmentExpression,
//          Type.Expression,
//          AssignmentExpression::getExpression,
//          AssignmentExpression::setExpression);
//
//  public static final TypedBranch<ComputedMemberExpression, Expression> ComputedMemberExpression_expression =
//      def(
//          Type.ComputedMemberExpression,
//          Type.Expression,
//          ComputedMemberExpression::getExpression,
//          ComputedMemberExpression::setExpression);
//
//  public static final TypedBranch<ExpressionStatement, Expression> ExpressionStatement_expression =
//      def(
//          Type.ExpressionStatement,
//          Type.Expression,
//          ExpressionStatement::getExpression,
//          ExpressionStatement::setExpression);
//
//  public static final TypedBranch<ReturnStatement, MaybeNode<Expression>> ReturnStatement_expression =
//      defm(
//          Type.ReturnStatement,
//          Type.Expression,
//          ReturnStatement::getExpression,
//          ReturnStatement::setExpression);
//
//  public static final TypedBranch<ThrowStatement, Expression> ThrowStatement_expression =
//      def(
//          Type.ThrowStatement,
//          Type.Expression,
//          ThrowStatement::getExpression,
//          ThrowStatement::setExpression);
//
//  public static final TypedBranch<TryFinallyStatement, Block> TryFinallyStatement_finalizer =
//      def(Type.TryFinallyStatement, Type.Block, TryFinallyStatement::getFinalizer, TryFinallyStatement::setFinalizer);
//
//  public static final TypedBranch<IdentifierExpression, Identifier> IdentifierExpression_identifier =
//      def(
//          Type.IdentifierExpression,
//          Type.Identifier,
//          IdentifierExpression::getIdentifier,
//          IdentifierExpression::setIdentifier);
//
//  public static final TypedBranch<ForStatement, MaybeNode<Either<VariableDeclaration, Expression>>> ForStatement_init =
//      defm(
//          Type.ForStatement,
//          EitherType.VARIABLEDECLARATION_EXPRESSION,
//          ForStatement::getInit,
//          ForStatement::setInit);
//
//  public static final TypedBranch<VariableDeclarator, MaybeNode<Expression>> VariableDeclarator_init =
//      defm(Type.VariableDeclarator, Type.Expression, VariableDeclarator::getInit, VariableDeclarator::setInit);
//
//  public static final TypedBranch<BreakStatement, MaybeNode<Identifier>> BreakStatement_label =
//      defm(Type.BreakStatement, Type.Identifier, BreakStatement::getLabel, BreakStatement::setLabel);
//
//  public static final TypedBranch<ContinueStatement, MaybeNode<Identifier>> ContinueStatement_label =
//      defm(Type.ContinueStatement, Type.Identifier, ContinueStatement::getLabel, ContinueStatement::setLabel);
//
//  public static final TypedBranch<LabeledStatement, Identifier> LabeledStatement_label =
//      def(
//          Type.LabeledStatement,
//          Type.Identifier,
//          LabeledStatement::getLabel,
//          LabeledStatement::setLabel);
//
//  public static final TypedBranch<BinaryExpression, Expression> BinaryExpression_left =
//      def(
//          Type.BinaryExpression,
//          Type.Expression,
//          BinaryExpression::getLeft,
//          BinaryExpression::setLeft);
//
//  public static final TypedBranch<ForInStatement, EitherNode<VariableDeclaration, Expression>> ForInStatement_left =
//      defe(
//          Type.ForInStatement, Type.VariableDeclaration, Type.Expression,
//          ForInStatement::getLeft,
//          ForInStatement::setLeft);
//
//  public static final TypedBranch<DataProperty, PropertyName> DataProperty_name =
//      def(
//          Type.DataProperty,
//          Type.PropertyName,
//          DataProperty::getName,
//          DataProperty::setName);
//
//  public static final TypedBranch<Getter, PropertyName> Getter_name =
//      def(
//          Type.Getter,
//          Type.PropertyName,
//          Getter::getName,
//          Getter::setName);
//
//  public static final TypedBranch<Setter, PropertyName> Setter_name =
//      def(
//          Type.Setter,
//          Type.PropertyName,
//          Setter::getName,
//          Setter::setName);
//
//  public static final TypedBranch<FunctionDeclaration, Identifier> FunctionDeclaration_name =
//      def(Type.FunctionDeclaration, Type.Identifier, FunctionDeclaration::getName, FunctionDeclaration::setName);
//
//  public static final TypedBranch<FunctionExpression, MaybeNode<Identifier>> FunctionExpression_name =
//      defm(Type.FunctionExpression, Type.Identifier, FunctionExpression::getName, FunctionExpression::setName);
//
//  public static final TypedBranch<StaticMemberExpression, Expression> StaticMemberExpression_object =
//      def(
//          Type.StaticMemberExpression,
//          Type.Expression,
//          StaticMemberExpression::getObject,
//          StaticMemberExpression::setObject);
//
//  public static final TypedBranch<ComputedMemberExpression, Expression> ComputedMemberExpression_object =
//      def(
//          Type.ComputedMemberExpression,
//          Type.Expression,
//          ComputedMemberExpression::getObject,
//          ComputedMemberExpression::setObject);
//
//  public static final TypedBranch<WithStatement, Expression> WithStatement_object =
//      def(
//          Type.WithStatement,
//          Type.Expression,
//          WithStatement::getObject,
//          WithStatement::setObject);
//
//  public static final TypedBranch<PrefixExpression, Expression> PrefixExpression_operand =
//      def(
//          Type.PrefixExpression,
//          Type.Expression,
//          PrefixExpression::getOperand,
//          PrefixExpression::setOperand);
//
//  public static final TypedBranch<PostfixExpression, Expression> PostfixExpression_operand =
//      def(
//          Type.PostfixExpression,
//          Type.Expression,
//          PostfixExpression::getOperand,
//          PostfixExpression::setOperand);
//
//  public static final TypedBranch<Setter, Identifier> Setter_parameter =
//      def(
//          Type.Setter,
//          Type.Identifier,
//          Setter::getParameter,
//          Setter::setParameter);
//
//  public static final TypedBranch<FunctionDeclaration, ListNode<Identifier>> FunctionDeclaration_parameters =
//      defl(
//          Type.FunctionDeclaration,
//          Type.Identifier,
//          FunctionDeclaration::getParameters,
//          FunctionDeclaration::setParameters);
//
//  public static final TypedBranch<FunctionExpression, ListNode<Identifier>> FunctionExpression_parameters =
//      defl(
//          Type.FunctionExpression,
//          Type.Identifier,
//          FunctionExpression::getParameters,
//          FunctionExpression::setParameters);
//
//  public static final TypedBranch<SwitchStatementWithDefault, ListNode<SwitchCase>>
//      SwitchStatementWithDefault_postDefaultCases =
//      defl(
//          Type.SwitchStatementWithDefault,
//          Type.SwitchCase,
//          SwitchStatementWithDefault::getPostDefaultCases,
//          SwitchStatementWithDefault::setPostDefaultCases);
//
//  public static final TypedBranch<SwitchStatementWithDefault, ListNode<SwitchCase>>
//      SwitchStatementWithDefault_preDefaultCases =
//      defl(
//          Type.SwitchStatementWithDefault,
//          Type.SwitchCase,
//          SwitchStatementWithDefault::getPreDefaultCases,
//          SwitchStatementWithDefault::setPreDefaultCases);
//
//  public static final TypedBranch<ObjectExpression, ListNode<ObjectProperty>> ObjectExpression_properties =
//      defl(
//          Type.ObjectExpression,
//          Type.ObjectProperty,
//          ObjectExpression::getProperties,
//          ObjectExpression::setProperties);
//
//  public static final TypedBranch<StaticMemberExpression, Identifier> StaticMemberExpression_property =
//      def(
//          Type.StaticMemberExpression,
//          Type.Identifier,
//          StaticMemberExpression::getProperty,
//          StaticMemberExpression::setProperty);
//
//  public static final TypedBranch<BinaryExpression, Expression> BinaryExpression_right =
//      def(
//          Type.BinaryExpression,
//          Type.Expression,
//          BinaryExpression::getRight,
//          BinaryExpression::setRight);
//
//  public static final TypedBranch<ForInStatement, Expression> ForInStatement_right =
//      def(
//          Type.ForInStatement,
//          Type.Expression,
//          ForInStatement::getRight,
//          ForInStatement::setRight);
//  public static final TypedBranch<FunctionBody, ListNode<Statement>> FunctionBody_statements =
//      defl(
//          Type.FunctionBody,
//          Type.Statement,
//          FunctionBody::getStatements,
//          FunctionBody::setStatements);
//
//  public static final TypedBranch<Block, ListNode<Statement>> Block_statements =
//      defl(Type.Block, Type.Statement, Block::getStatements, Block::setStatements);
//
//  public static final TypedBranch<ConditionalExpression, Expression> ConditionalExpression_test =
//      def(Type.ConditionalExpression, Type.Expression, ConditionalExpression::getTest, ConditionalExpression::setTest);
//
//  public static final TypedBranch<DoWhileStatement, Expression> DoWhileStatement_test =
//      def(
//          Type.DoWhileStatement,
//          Type.Expression,
//          DoWhileStatement::getTest,
//          DoWhileStatement::setTest);
//
//  public static final TypedBranch<ForStatement, MaybeNode<Expression>> ForStatement_test =
//      defm(
//          Type.ForStatement,
//          Type.Expression,
//          ForStatement::getTest,
//          ForStatement::setTest);
//
//  public static final TypedBranch<IfStatement, Expression> IfStatement_test =
//      def(
//          Type.IfStatement,
//          Type.Expression,
//          IfStatement::getTest,
//          IfStatement::setTest);
//
//  public static final TypedBranch<WhileStatement, Expression> WhileStatement_test =
//      def(
//          Type.WhileStatement,
//          Type.Expression,
//          WhileStatement::getTest,
//          WhileStatement::setTest);
//
//  public static final TypedBranch<SwitchCase, Expression> SwitchCase_test =
//      def(
//          Type.SwitchCase,
//          Type.Expression,
//          SwitchCase::getTest,
//          SwitchCase::setTest);
//
//  public static final TypedBranch<ForStatement, MaybeNode<Expression>> ForStatement_update =
//      defm(
//          Type.ForStatement,
//          Type.Expression,
//          ForStatement::getUpdate,
//          ForStatement::setUpdate);
//
//  public static final TypedBranch<DataProperty, Expression> DataProperty_value =
//      def(
//          Type.DataProperty,
//          Type.Expression,
//          DataProperty::getValue,
//          DataProperty::setValue);
//}
