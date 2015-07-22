// Generated by src/generate-spec-java.js 

/**
 * Copyright 2015 Shape Security, Inc.
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

package com.shapesecurity.shift.ast;

import org.jetbrains.annotations.NotNull;
import com.shapesecurity.functional.data.HashCodeBuilder;
import com.shapesecurity.shift.ast.operators.Precedence;

public class YieldGeneratorExpression extends Expression
{

  @NotNull
  public final Expression expression;

  public YieldGeneratorExpression (@NotNull Expression expression)
  {
    super();
    this.expression = expression;
  }

  @Override
  public boolean equals(Object object)
  {
    return object instanceof YieldGeneratorExpression && this.expression.equals(((YieldGeneratorExpression) object).expression);
  }

  @Override
  public int hashCode()
  {
    int code = HashCodeBuilder.put(0, "YieldGeneratorExpression");
    code = HashCodeBuilder.put(code, this.expression);
    return code;
  }

  @NotNull
  public Expression getExpression()
  {
    return this.expression;
  }

  @NotNull
  public YieldGeneratorExpression setExpression(@NotNull Expression expression)
  {
    return new YieldGeneratorExpression(expression);
  }

  @Override
  @NotNull
  public Precedence getPrecedence()
  {
    return Precedence.ASSIGNMENT;
  }

}