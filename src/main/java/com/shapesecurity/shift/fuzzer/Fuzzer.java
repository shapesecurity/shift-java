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


package com.shapesecurity.shift.fuzzer;

import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
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
import com.shapesecurity.shift.ast.expression.LiteralInfinityExpression;
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
import com.shapesecurity.shift.ast.operators.AssignmentOperator;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.PostfixOperator;
import com.shapesecurity.shift.ast.operators.PrefixOperator;
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
import com.shapesecurity.shift.ast.statement.IterationStatement;
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
import com.shapesecurity.shift.utils.D2A;
import com.shapesecurity.shift.utils.Utils;

import java.util.HashSet;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

public class Fuzzer {
  private static final String identifierStart = "_$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String identifierPart = identifierStart + "0123456789";
  private static final char[] identifierStartArr = identifierStart.toCharArray();
  private static final char[] identifierPartArr = identifierPart.toCharArray();

  private static final int MANY_BOUND = 5;
  private static final int MAX_IDENT_LENGTH = 15;
  private static final int MAX_STRING_LENGTH = 3;
  private static final double STRICT_MODE_PROBABILITY = 0.3;
  private static final double SPECIAL_IDENT_PROBABILITY = 0.5;

  @SafeVarargs
  private static <T> T[] array(T... arr) {
    return arr;
  }

  private static final String[] RESERVED_WORDS = new String[]{
      "false",
      "null",
      "true",
      "let",
      "if",
      "in",
      "do",
      "var",
      "for",
      "new",
      "try",
      "this",
      "else",
      "case",
      "void",
      "with",
      "enum",
      "while",
      "break",
      "catch",
      "throw",
      "const",
      "class",
      "super",
      "return",
      "typeof",
      "delete",
      "switch",
      "export",
      "import",
      "default",
      "finally",
      "extends",
      "function",
      "continue",
      "debugger",
      "instanceof",
  };

  private static final String[] STRICT_MODE_RESERVED_WORDS = new String[]{
      "implements",
      "interface",
      "package",
      "private",
      "protected",
      "public",
      "static",
      "yield",

      "false",
      "null",
      "true",
      "let",
      "if",
      "in",
      "do",
      "var",
      "for",
      "new",
      "try",
      "this",
      "else",
      "case",
      "void",
      "with",
      "enum",
      "while",
      "break",
      "catch",
      "throw",
      "const",
      "class",
      "super",
      "return",
      "typeof",
      "delete",
      "switch",
      "export",
      "import",
      "default",
      "finally",
      "extends",
      "function",
      "continue",
      "debugger",
      "instanceof",
  };

  private static final String[] RESTRICTED_WORDS = new String[]{
      "arguments",
      "eval",
  };

  @NotNull
  public static Script generate(@NotNull Random random, int depth) {
    return randomScript(new GenCtx(random), depth);
  }

  @NotNull
  private static String randomRegExpString(@NotNull GenCtx ctx, int depth) {
    return "/" + randomIdentifierString(ctx, depth) + "/";
  }

  @NotNull
  private static String randomString(@NotNull GenCtx ctx, int depth) {
    int length = ctx.random.nextInt(MAX_STRING_LENGTH);
    StringBuilder sb = new StringBuilder();
    ctx.random.ints(length, 20, 127).forEach((i) -> sb.append((char) i));
    return sb.toString();
  }

  @NotNull
  private static String randomIdentifierString(@NotNull GenCtx ctx, int depth) {
    StringBuilder result = new StringBuilder();
    result.append(identifierStartArr[ctx.random.nextInt(identifierStartArr.length)]);
    int length = ctx.random.nextInt(MAX_IDENT_LENGTH);
    for (int i = 0; i < length; i++) {
      result.append(identifierPartArr[ctx.random.nextInt(identifierPartArr.length)]);
    }
    return result.toString();
  }

  private static double randomNumber(@NotNull GenCtx ctx, int depth) {
    return Math.exp(ctx.random.nextGaussian());
  }

  private static interface Gen<T> {
    @NotNull
    T apply(@NotNull GenCtx ctx, int depth);
  }

  private static <T> Gen<T> choice(T[] arr) {
    return (ctx, depth) -> arr[ctx.random.nextInt(arr.length)];
  }

  @NotNull
  private static <T> Gen<ImmutableList<T>> many(final int bound, @NotNull Gen<T> gen) {
    return (ctx, depth) -> {
      if (depth <= 0) {
        return ImmutableList.nil();
      }
      int number = ctx.random.nextInt(bound);
      ImmutableList<T> result = ImmutableList.nil();
      for (int i = 0; i < number; i++) {
        result = result.cons(gen.apply(ctx, depth));
      }
      return result;
    };
  }

  @NotNull
  private static <T> Gen<ImmutableList<T>> many(@NotNull Gen<T> gen) {
    return many(MANY_BOUND, gen);
  }

  @NotNull
  private static <T> Gen<NonEmptyImmutableList<T>> many1(@NotNull Gen<T> gen) {
    return (ctx, depth) -> many(MANY_BOUND - 1,gen).apply(ctx, depth).cons(gen.apply(ctx, depth));
  }

  @NotNull
  private static <T> Gen<Maybe<T>> optional(@NotNull Gen<T> gen) {
    return (ctx, depth) -> {
      if (depth <= 0) {
        return Maybe.nothing();
      }
      if (ctx.random.nextBoolean()) {
        return Maybe.nothing();
      }
      return Maybe.just(gen.apply(ctx, depth));
    };
  }

  @NotNull
  private static <A, B> Gen<Either<A, B>> either(@NotNull Gen<A> gen1, @NotNull Gen<B> gen2) {
    return (ctx, depth) -> {
      if (ctx.random.nextBoolean()) {
        return Either.left(gen1.apply(ctx, depth));
      }
      return Either.right(gen2.apply(ctx, depth));
    };
  }

  @NotNull
  private static Script randomScript(@NotNull GenCtx ctx, int depth) {
    return new Script(randomFunctionBody(ctx, depth - 1));
  }

  @NotNull
  private static FunctionBody randomFunctionBody(@NotNull GenCtx ctx, int depth) {
    ImmutableList<Directive> directives = many(Fuzzer::randomDirective).apply(ctx, depth - 1);
    if (!ctx.inStrictMode && directives.exists(dir -> dir instanceof UseStrictDirective)) {
      ctx = ctx.enterStrictMode();
    }
    return new FunctionBody(directives, many(Fuzzer::randomStatement).apply(ctx, depth - 1));
  }

  @NotNull
  private static Identifier randomIdentifier(
      @NotNull GenCtx ctx,
      int depth,
      boolean allowReserved,
      boolean allowRestricted) {
    String name;
    boolean genarateSpecial = ctx.random.nextDouble() < SPECIAL_IDENT_PROBABILITY;
    if (genarateSpecial && allowReserved) {
      if (ctx.inStrictMode) {
        name = choice(STRICT_MODE_RESERVED_WORDS).apply(ctx, depth - 1);
      } else {
        name = choice(RESERVED_WORDS).apply(ctx, depth - 1);
      }
    } else if (genarateSpecial && allowRestricted) {
      name = choice(RESTRICTED_WORDS).apply(ctx, depth - 1);
    } else {
      boolean disallow;
      do {
        disallow = false;
        name = randomIdentifierString(ctx, depth - 1);
        for (String reservedWord : (ctx.inStrictMode ? STRICT_MODE_RESERVED_WORDS : RESERVED_WORDS)) {
          if (reservedWord.equals(name)) {
            disallow = true;
          }
        }
      } while (disallow);
    }
    return new Identifier(name);
  }

  private static final Gen<Expression>[] expressionGens = Fuzzer.<Gen<Expression>>array(
      Fuzzer::randomArrayExpression,
      Fuzzer::randomAssignmentExpression,
      Fuzzer::randomBinaryExpression,
      Fuzzer::randomCallExpression,
      Fuzzer::randomComputedMemberExpression,
      Fuzzer::randomConditionalExpression,
      Fuzzer::randomFunctionExpression,
      Fuzzer::randomIdentifierExpression,
      Fuzzer::randomLiteralBooleanExpression,
      Fuzzer::randomLiteralNullExpression,
      Fuzzer::randomLiteralInfinityExpression,
      Fuzzer::randomLiteralNumericExpression,
      Fuzzer::randomLiteralRegExpExpression,
      Fuzzer::randomLiteralStringExpression,
      Fuzzer::randomNewExpression,
      Fuzzer::randomObjectExpression,
      Fuzzer::randomPostfixExpression,
      Fuzzer::randomPrefixExpression,
      Fuzzer::randomStaticMemberExpression,
      Fuzzer::randomThisExpression
  );

  @NotNull
  private static Expression randomExpression(@NotNull GenCtx ctx, int depth) {
    return choice(expressionGens).apply(ctx, depth - 1).apply(ctx, depth - 1);
  }

  @NotNull
  private static Directive randomDirective(@NotNull GenCtx ctx, int depth) {
    if (ctx.random.nextDouble() < STRICT_MODE_PROBABILITY) {
      return randomUseStrictDirective(ctx, depth - 1);
    }
    return randomUnknownDirective(ctx, depth - 1);
  }

  // Special statements:
  // - 0 = BreakStatement
  // - 1 = ContinueStatement
  // - 2 = WithStatement
  // - 3 = ReturnStatement
  // - 4 = LabeledStatement

  private final static int kGenBreakStatement = 0;
  private final static int kGenContinueStatement = 1;
  private final static int kGenWithStatement = 2;
  private final static int kGenReturnStatement = 3;
  private final static int kGenLabeledStatement = 4;

  private final static Gen<Statement>[] nonIterationStatementGens = Fuzzer.<Gen<Statement>>array(
      Fuzzer::randomBlockStatement,
      Fuzzer::randomDebuggerStatement,
      Fuzzer::randomEmptyStatement,
      Fuzzer::randomExpressionStatement,
      Fuzzer::randomFunctionDeclaration,
      Fuzzer::randomIfStatement,
      Fuzzer::randomSwitchStatement,
      Fuzzer::randomSwitchStatementWithDefault,
      Fuzzer::randomThrowStatement,
      Fuzzer::randomTryCatchStatement,
      Fuzzer::randomTryFinallyStatement,
      Fuzzer::randomVariableDeclarationStatement
  );

  private final static Gen<IterationStatement>[] iterationStatementGens = Fuzzer.<Gen<IterationStatement>>array(
      Fuzzer::randomDoWhileStatement,
      Fuzzer::randomForInStatement,
      Fuzzer::randomForStatement,
      Fuzzer::randomWhileStatement
  );

  private final static int totalStatements = nonIterationStatementGens.length + iterationStatementGens.length + 5;

  @NotNull
  private static Statement randomStatement(@NotNull GenCtx ctx, int depth) {
    return randomStatementGeneric(ctx, depth, true);
  }

  @NotNull
  private static Statement randomStatementGeneric(@NotNull GenCtx ctx, int depth, boolean allowIteration) {
    if (depth <= 0) {
      switch (ctx.random.nextInt(ctx.inIteration ? 4 : 2)) {
      case 0:
        return new DebuggerStatement();
      case 1:
        return new EmptyStatement();
      case 2:
        return new BreakStatement(Maybe.nothing());
      default:
        return new ContinueStatement(Maybe.nothing());
      }
    }
    int n;
    int total = totalStatements - (allowIteration ? 0 : iterationStatementGens.length);
    do {
      n = ctx.random.nextInt(total);
      switch (n) {
      case kGenBreakStatement:
        if (ctx.labelsInFunctionBoundary.length == 0 && !ctx.inSwitch && !ctx.inIteration) {
          continue;
        }
        break;
      case kGenContinueStatement:
        if (!ctx.inIteration) {
          continue;
        }
        break;
      case kGenWithStatement:
        if (ctx.inStrictMode) {
          continue;
        }
        break;
      case kGenReturnStatement:
        if (!ctx.inFunctional) {
          continue;
        }
        break;
      }
      break;
    } while (true);

    switch (n) {
    case kGenBreakStatement:
      if ((!ctx.inSwitch && !ctx.inIteration) || ctx.labelsInFunctionBoundary.length > 0 && ctx.random.nextBoolean()) {
        // with label
        Maybe<Identifier> label = ctx.labelsInFunctionBoundary.index(
            ctx.random.nextInt(ctx.labelsInFunctionBoundary.length));
        return new BreakStatement(label);
      } else {
        return new BreakStatement(Maybe.nothing());
      }
    case kGenContinueStatement:
      if (ctx.iterationLabelsInFunctionBoundary.length > 0 && ctx.random.nextBoolean()) {
        // with label
        Maybe<Identifier> label = ctx.iterationLabelsInFunctionBoundary.index(
            ctx.random.nextInt(ctx.iterationLabelsInFunctionBoundary.length));
        return new ContinueStatement(label);
      } else {
        return new ContinueStatement(Maybe.nothing());
      }
    case kGenWithStatement:
      return new WithStatement(randomExpression(ctx, depth - 1), randomStatement(ctx, depth - 1));
    case kGenReturnStatement:
      if (ctx.random.nextBoolean()) {
        return new ReturnStatement(Maybe.just(randomExpression(ctx, depth - 1)));
      } else {
        return new ReturnStatement(Maybe.nothing());
      }
    case kGenLabeledStatement:
      Identifier label = randomIdentifier(ctx, depth - 1, false, true);
      while (ctx.labels.exists(label::equals)) {
        label = randomIdentifier(ctx, depth - 1, false, true);
      }
      int bodyN = ctx.random.nextInt(totalStatements);
      if (bodyN < iterationStatementGens.length) {
        // generate iteration statement
        IterationStatement body = iterationStatementGens[bodyN].apply(ctx.withIterationLabel(label), depth - 1);
        return new LabeledStatement(label, body);
      } else {
        Statement body = randomStatementGeneric(ctx.withLabel(label), depth - 1, false);
        return new LabeledStatement(label, body);
      }
    default:
      if (n < nonIterationStatementGens.length + 5) {
        return nonIterationStatementGens[n - 5].apply(ctx, depth - 1);
      } else {
        return iterationStatementGens[n - nonIterationStatementGens.length - 5].apply(ctx, depth - 1);
      }
    }
  }

  @NotNull
  private static Block randomBlock(@NotNull GenCtx ctx, int depth) {
    if (depth < 1) {
      return new Block(ImmutableList.nil());
    }
    return new Block(many(Fuzzer::randomStatement).apply(ctx.allowMissingElse(), depth - 1));
  }

  @NotNull
  private static VariableDeclarator randomVariableDeclarator(@NotNull GenCtx ctx, int depth) {
    return new VariableDeclarator(
        randomIdentifier(ctx, depth - 1, false, false),
        optional(Fuzzer::randomExpression).apply(ctx, depth - 1));
  }

  @NotNull
  private static VariableDeclaration randomVariableDeclaration1(@NotNull GenCtx ctx, int depth) {
    return new VariableDeclaration(
        ctx.inStrictMode ? VariableDeclaration.VariableDeclarationKind.Var :
            choice(new VariableDeclaration.VariableDeclarationKind[]{
                VariableDeclaration.VariableDeclarationKind.Var,
                VariableDeclaration.VariableDeclarationKind.Let}).apply(ctx, depth - 1),
        ImmutableList.list(randomVariableDeclarator(ctx, depth - 1)));
  }

  @NotNull
  private static VariableDeclaration randomVariableDeclaration(@NotNull GenCtx ctx, int depth) {
    return new VariableDeclaration(
        ctx.inStrictMode ? VariableDeclaration.VariableDeclarationKind.Var :
            choice(VariableDeclaration.VariableDeclarationKind.values()).apply(ctx, depth - 1),
        many1(Fuzzer::randomVariableDeclarator).apply(ctx, depth - 1));
  }

  @NotNull
  private static SwitchCase randomSwitchCase(@NotNull GenCtx ctx, int depth) {
    return new SwitchCase(randomExpression(ctx, depth - 1), many(Fuzzer::randomStatement).apply(ctx, depth - 1));
  }

  @NotNull
  private static SwitchDefault randomSwitchDefault(@NotNull GenCtx ctx, int depth) {
    return new SwitchDefault(many(Fuzzer::randomStatement).apply(ctx, depth - 1));
  }

  @NotNull
  private static CatchClause randomCatchClause(@NotNull GenCtx ctx, int depth) {
    return new CatchClause(randomIdentifier(ctx, depth - 1, false, false), randomBlock(ctx, depth - 1));
  }

  @NotNull
  private static UnknownDirective randomUnknownDirective(@NotNull GenCtx ctx, int depth) {
    String value = Utils.escapeStringLiteral(randomString(ctx, depth - 1));
    return new UnknownDirective(value.substring(1, value.length() - 1));
  }

  @NotNull
  private static UseStrictDirective randomUseStrictDirective(@NotNull GenCtx ctx, int depth) {
    return new UseStrictDirective();
  }

  @NotNull
  private static ArrayExpression randomArrayExpression(@NotNull GenCtx ctx, int depth) {
    return new ArrayExpression(many(optional(Fuzzer::randomExpression)).apply(ctx, depth - 1));
  }

  @NotNull
  private static AssignmentExpression randomAssignmentExpression(@NotNull GenCtx ctx, int depth) {
    Expression lhs;
    do {
      lhs = randomExpression(ctx, depth - 1);
    } while (ctx.inStrictMode && lhs instanceof IdentifierExpression &&
        Utils.isRestrictedWord(((IdentifierExpression) lhs).identifier.name));
    return new AssignmentExpression(
        choice(AssignmentOperator.values()).apply(ctx, depth - 1),
        lhs,
        randomExpression(ctx, depth - 1));
  }

  @NotNull
  private static BinaryExpression randomBinaryExpression(@NotNull GenCtx ctx, int depth) {
    return new BinaryExpression(
        choice(BinaryOperator.values()).apply(ctx, depth - 1),
        randomExpression(ctx, depth - 1),
        randomExpression(ctx, depth - 1));
  }

  @NotNull
  private static CallExpression randomCallExpression(@NotNull GenCtx ctx, int depth) {
    return new CallExpression(randomExpression(ctx, depth - 1), many(Fuzzer::randomExpression).apply(ctx, depth - 1));
  }

  @NotNull
  private static ComputedMemberExpression randomComputedMemberExpression(@NotNull GenCtx ctx, int depth) {
    return new ComputedMemberExpression(randomExpression(ctx, depth - 1), randomExpression(ctx, depth - 1));
  }

  @NotNull
  private static ConditionalExpression randomConditionalExpression(@NotNull GenCtx ctx, int depth) {
    return new ConditionalExpression(
        randomExpression(ctx, depth - 1),
        randomExpression(ctx, depth - 1),
        randomExpression(ctx, depth - 1));
  }

  @NotNull
  private static ImmutableList<Identifier> randomParameterList(@NotNull GenCtx ctx, int depth) {
    Gen<Identifier> gen = (c, d) -> randomIdentifier(c, d, false, false);
    if (ctx.inStrictMode) {
      int length = ctx.random.nextInt(MANY_BOUND);
      HashSet<String> names = new HashSet<>();
      ImmutableList<Identifier> result = ImmutableList.nil();
      for (int i = 0; i < length; i++) {
        Identifier identifier = gen.apply(ctx, depth);
        while (names.contains(identifier.name)) {
          identifier = gen.apply(ctx, depth);
        }
        names.add(identifier.name);
        result = result.cons(identifier);
      }
      return result;
    } else {
      return many(gen).apply(ctx, depth);
    }
  }

  @NotNull
  private static FunctionExpression randomFunctionExpression(@NotNull GenCtx ctx, int depth) {
    FunctionBody body = randomFunctionBody(ctx.enterFunctional(), depth - 1);
    if (body.isStrict()) {
      ctx = ctx.enterStrictMode();
    }

    return new FunctionExpression(
        optional((c, d) -> randomIdentifier(c, d, false, false)).apply(ctx, depth - 1),
        randomParameterList(ctx, depth - 1),
        body);
  }

  @NotNull
  private static IdentifierExpression randomIdentifierExpression(@NotNull GenCtx ctx, int depth) {
    // restricted word are filtered in assignment expression.
    return new IdentifierExpression(randomIdentifier(ctx, depth - 1, false, true));
  }

  @NotNull
  private static LiteralBooleanExpression randomLiteralBooleanExpression(@NotNull GenCtx ctx, int depth) {
    return new LiteralBooleanExpression(ctx.random.nextBoolean());
  }

  @NotNull
  private static LiteralNullExpression randomLiteralNullExpression(@NotNull GenCtx ctx, int depth) {
    return new LiteralNullExpression();
  }

  @NotNull
  private static LiteralNumericExpression randomLiteralNumericExpression(@NotNull GenCtx ctx, int depth) {
    return new LiteralNumericExpression(randomNumber(ctx, depth - 1));
  }

  @NotNull
  private static LiteralInfinityExpression randomLiteralInfinityExpression(@NotNull GenCtx ctx, int depth) {
    return new LiteralInfinityExpression();
  }

  @NotNull
  private static LiteralRegExpExpression randomLiteralRegExpExpression(@NotNull GenCtx ctx, int depth) {
    return new LiteralRegExpExpression(randomRegExpString(ctx, depth - 1));
  }

  @NotNull
  private static LiteralStringExpression randomLiteralStringExpression(@NotNull GenCtx ctx, int depth) {
    return new LiteralStringExpression(randomString(ctx, depth - 1));
  }

  @NotNull
  private static NewExpression randomNewExpression(@NotNull GenCtx ctx, int depth) {
    return new NewExpression(randomExpression(ctx, depth - 1), many(Fuzzer::randomExpression).apply(ctx, depth - 1));
  }

  @NotNull
  private static ObjectExpression randomObjectExpression(@NotNull GenCtx ctx, int depth) {
    int length = ctx.random.nextInt(MANY_BOUND);
    HashSet<String> names = new HashSet<>();
    for (int i = 0; i < length; i++) {
      String name;
      int kind = ctx.random.nextInt();
      switch (kind) {
      case 0:
        do {
          Identifier ident = randomIdentifier(ctx, depth - 1, true, true);
          if (names.contains(ident.name)) {
            continue;
          }
          name = ident.name;
          break;
        } while (true);
        break;
      case 1:
        do {
          double ident = randomNumber(ctx, depth - 1);
          if (names.contains(D2A.d2a(ident))) {
            continue;
          }
          name = D2A.d2a(ident);
          break;
        } while (true);
        break;
      default:
        do {
          String ident = randomString(ctx, depth - 1);
          if (names.contains(ident)) {
            continue;
          }
          name = ident;
          break;
        } while (true);
        break;
      }
      names.add(name);
    }
    ImmutableList<ObjectProperty> properties = ImmutableList.nil();
    for (String name : names) {
      // data, get, set, get/set, non-strict data/data
      switch (ctx.random.nextInt(ctx.inStrictMode ? 4 : 5)) {
      case 0:
        properties = properties.cons(randomDataProperty(ctx, depth - 1, name));
        break;
      case 1:
        properties = properties.cons(randomGetter(ctx, depth - 1, name));
        break;
      case 2:
        properties = properties.cons(randomSetter(ctx, depth - 1, name));
        break;
      case 3:
        properties = properties.cons(randomGetter(ctx, depth - 1, name));
        properties = properties.cons(randomSetter(ctx, depth - 1, name));
        break;
      default:
        properties = properties.cons(randomDataProperty(ctx, depth - 1, name));
        properties = properties.cons(randomDataProperty(ctx, depth - 1, name));
        break;
      }
    }
    return new ObjectExpression(properties);
  }

  @NotNull
  private static PostfixExpression randomPostfixExpression(@NotNull GenCtx ctx, int depth) {
    PostfixOperator operator = choice(PostfixOperator.values()).apply(ctx, depth - 1);
    Expression expression = randomExpression(ctx, depth - 1);
    if (ctx.inStrictMode) {
      switch (operator) {
      case Decrement:
      case Increment:
        while (expression instanceof IdentifierExpression &&
            Utils.isRestrictedWord(((IdentifierExpression) expression).identifier.name)) {
          expression = randomExpression(ctx, depth - 1);
        }
      }
    }
    return new PostfixExpression(
        operator,
        expression);
  }

  @NotNull
  private static PrefixExpression randomPrefixExpression(@NotNull GenCtx ctx, int depth) {
    PrefixOperator operator = choice(PrefixOperator.values()).apply(ctx, depth - 1);
    Expression expression = randomExpression(ctx, depth - 1);
    if (ctx.inStrictMode) {
      switch (operator) {
      case Decrement:
      case Increment:
        while (expression instanceof IdentifierExpression &&
            Utils.isRestrictedWord(((IdentifierExpression) expression).identifier.name)) {
          expression = randomExpression(ctx, depth - 1);
        }
        break;
      case Delete:
        if (expression instanceof IdentifierExpression) {
          if (depth < 3) {
            return new PrefixExpression(PrefixOperator.Delete, randomStaticMemberExpression(ctx, depth));
          } else {
            while (expression instanceof IdentifierExpression) {
              expression = randomExpression(ctx, depth - 1);
            }
          }
        }
      }
    }
    return new PrefixExpression(operator, expression);
  }

  @NotNull
  private static StaticMemberExpression randomStaticMemberExpression(@NotNull GenCtx ctx, int depth) {
    return new StaticMemberExpression(
        randomExpression(ctx, depth - 1),
        randomIdentifier(ctx, depth - 1, true, true));
  }

  @NotNull
  private static ThisExpression randomThisExpression(@NotNull GenCtx ctx, int depth) {
    return new ThisExpression();
  }

  @NotNull
  private static DataProperty randomDataProperty(@NotNull GenCtx ctx, int depth, @NotNull String name) {
    return new DataProperty(randomPropertyName(ctx, depth - 1, name), randomExpression(ctx, depth - 1));
  }

  @NotNull
  private static Getter randomGetter(@NotNull GenCtx ctx, int depth, @NotNull String name) {
    FunctionBody body = randomFunctionBody(ctx.enterFunctional(), depth - 1);
    if (body.isStrict()) {
      ctx = ctx.enterStrictMode();
    }
    return new Getter(randomPropertyName(ctx, depth - 1, name), body);
  }

  @NotNull
  private static Setter randomSetter(@NotNull GenCtx ctx, int depth, @NotNull String name) {
    FunctionBody body = randomFunctionBody(ctx.enterFunctional(), depth - 1);
    if (body.isStrict()) {
      ctx = ctx.enterStrictMode();
    }
    return new Setter(randomPropertyName(ctx, depth - 1, name), randomIdentifier(ctx, depth - 1, false, false), body);
  }

  @NotNull
  private static PropertyName randomPropertyName(@NotNull GenCtx ctx, int depth, @NotNull String name) {
    if (Utils.isValidIdentifierName(name)) {
      switch (ctx.random.nextInt(2)) {
      case 0:
        return new PropertyName(new Identifier(name));
      default:
        return new PropertyName(name);
      }
    } else {
      // Allow numbers.
      return new PropertyName(name);
    }
  }

  @NotNull
  private static BlockStatement randomBlockStatement(@NotNull GenCtx ctx, int depth) {
    return new BlockStatement(randomBlock(ctx.allowMissingElse(), depth - 1));
  }

  @NotNull
  private static DebuggerStatement randomDebuggerStatement(@NotNull GenCtx ctx, int depth) {
    return new DebuggerStatement();
  }

  @NotNull
  private static DoWhileStatement randomDoWhileStatement(@NotNull GenCtx ctx, int depth) {
    return new DoWhileStatement(randomStatement(ctx.enterIteration().allowMissingElse(), depth - 1), randomExpression(
        ctx,
        depth - 1));
  }

  @NotNull
  private static EmptyStatement randomEmptyStatement(@NotNull GenCtx ctx, int depth) {
    return new EmptyStatement();
  }

  @NotNull
  private static ExpressionStatement randomExpressionStatement(@NotNull GenCtx ctx, int depth) {
    return new ExpressionStatement(randomExpression(ctx, depth - 1));
  }

  @NotNull
  private static ForInStatement randomForInStatement(@NotNull GenCtx ctx, int depth) {
    return new ForInStatement(
        either(Fuzzer::randomVariableDeclaration1, Fuzzer::randomExpression).apply(ctx, depth - 1),
        randomExpression(ctx, depth - 1),
        randomStatement(ctx, depth - 1));
  }

  @NotNull
  private static ForStatement randomForStatement(@NotNull GenCtx ctx, int depth) {
    return new ForStatement(
        optional(either(Fuzzer::randomVariableDeclaration1, Fuzzer::randomExpression)).apply(ctx, depth - 1),
        optional(Fuzzer::randomExpression).apply(ctx, depth - 1),
        optional(Fuzzer::randomExpression).apply(ctx, depth - 1),
        randomStatement(ctx, depth - 1));
  }

  @NotNull
  private static FunctionDeclaration randomFunctionDeclaration(@NotNull GenCtx ctx, int depth) {
    FunctionBody functionBody = randomFunctionBody(ctx.enterFunctional().clearLabels(), depth - 1);
    if (functionBody.isStrict()) {
      ctx = ctx.enterStrictMode();
    }
    return new FunctionDeclaration(
        randomIdentifier(ctx, depth - 1, false, false),
        randomParameterList(ctx, depth - 1),
        functionBody);
  }

  @NotNull
  private static IfStatement randomIfStatement(@NotNull GenCtx ctx, int depth) {
    if (ctx.allowMissingElse) {
      boolean missElse = ctx.random.nextBoolean();
      if (missElse) {
        return new IfStatement(
            randomExpression(ctx, depth - 1),
            randomStatement(ctx, depth - 1),
            Maybe.nothing());
      }
    }
    return new IfStatement(
        randomExpression(ctx, depth - 1),
        randomStatement(ctx.forbidMissingElse(), depth - 1),
        Maybe.just(randomStatement(ctx, depth - 1)));
  }

  @NotNull
  private static SwitchStatement randomSwitchStatement(@NotNull GenCtx ctx, int depth) {
    ctx = ctx.allowMissingElse().enterSwitch();
    return new SwitchStatement(
        randomExpression(ctx, depth - 1),
        Fuzzer.many(Fuzzer::randomSwitchCase).apply(ctx, depth - 1));
  }

  @NotNull
  private static SwitchStatementWithDefault randomSwitchStatementWithDefault(@NotNull GenCtx ctx, int depth) {
    ctx = ctx.allowMissingElse().enterSwitch();
    return new SwitchStatementWithDefault(
        randomExpression(ctx, depth - 1),
        Fuzzer.many(Fuzzer::randomSwitchCase).apply(ctx, depth - 1),
        randomSwitchDefault(ctx, depth - 1),
        Fuzzer.many(Fuzzer::randomSwitchCase).apply(ctx, depth - 1));
  }

  @NotNull
  private static ThrowStatement randomThrowStatement(@NotNull GenCtx ctx, int depth) {
    return new ThrowStatement(randomExpression(ctx, depth - 1));
  }

  @NotNull
  private static TryCatchStatement randomTryCatchStatement(@NotNull GenCtx ctx, int depth) {
    return new TryCatchStatement(randomBlock(ctx, depth - 1), randomCatchClause(ctx, depth - 1));
  }

  @NotNull
  private static TryFinallyStatement randomTryFinallyStatement(@NotNull GenCtx ctx, int depth) {
    return new TryFinallyStatement(
        randomBlock(ctx, depth - 1),
        optional(Fuzzer::randomCatchClause).apply(ctx, depth - 1),
        randomBlock(ctx, depth - 1));
  }

  @NotNull
  private static VariableDeclarationStatement randomVariableDeclarationStatement(@NotNull GenCtx ctx, int depth) {
    return new VariableDeclarationStatement(randomVariableDeclaration(ctx, depth - 1));
  }

  @NotNull
  private static WhileStatement randomWhileStatement(@NotNull GenCtx ctx, int depth) {
    return new WhileStatement(randomExpression(ctx, depth - 1), randomStatement(ctx, depth - 1));
  }
}
