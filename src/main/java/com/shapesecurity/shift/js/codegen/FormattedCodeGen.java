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

package com.shapesecurity.shift.js.codegen;

import com.shapesecurity.shift.js.ast.Script;

import javax.annotation.Nonnull;

public class FormattedCodeGen extends CodeGen {
  public static final FormattedCodeGen INSTANCE = new FormattedCodeGen();

  protected FormattedCodeGen() {
    super();
  }

  public static String codeGen(@Nonnull Script script) {
    StringBuilder sb = new StringBuilder();
    TokenStream ts = new TokenStream(sb);
    script.reduce(INSTANCE).code.emit(ts, false);
    return sb.toString();
  }

  @Nonnull
  protected static CodeRep semi(@Nonnull CodeRep rep) {
    return new FormattedCodeRep.Semi();
  }

  @Nonnull
  @Override
  protected CodeRep brace(@Nonnull CodeRep rep) {
    return new FormattedCodeRep.Brace(rep);
  }

  @Nonnull
  @Override
  protected CodeRep semiOp() {
    return new FormattedCodeRep.SemiOp();
  }
}
