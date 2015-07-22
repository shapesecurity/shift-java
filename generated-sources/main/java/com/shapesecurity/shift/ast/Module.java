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
import com.shapesecurity.functional.data.ImmutableList;

public class Module extends Node
{

  @NotNull
  public final ImmutableList<Directive> directives;

  @NotNull
  public final ImmutableList<ImportDeclarationExportDeclarationStatement> items;

  public Module (@NotNull SourceSpan loc, @NotNull ImmutableList<Directive> directives, @NotNull ImmutableList<ImportDeclarationExportDeclarationStatement> items)
  {
    super(loc);
    this.directives = directives;
    this.items = items;
  }

  public Module (@NotNull ImmutableList<Directive> directives, @NotNull ImmutableList<ImportDeclarationExportDeclarationStatement> items)
  {
    super();
    this.directives = directives;
    this.items = items;
  }

  @Override
  public boolean equals(Object object)
  {
    return object instanceof Module && this.directives.equals(((Module) object).directives) && this.items.equals(((Module) object).items);
  }

  @Override
  public int hashCode()
  {
    int code = HashCodeBuilder.put(0, "Module");
    code = HashCodeBuilder.put(code, this.directives);
    code = HashCodeBuilder.put(code, this.items);
    return code;
  }

  @NotNull
  public ImmutableList<Directive> getDirectives()
  {
    return this.directives;
  }

  @NotNull
  public ImmutableList<ImportDeclarationExportDeclarationStatement> getItems()
  {
    return this.items;
  }

  @NotNull
  public Module setDirectives(@NotNull ImmutableList<Directive> directives)
  {
    return new Module(directives, this.items);
  }

  @NotNull
  public Module setItems(@NotNull ImmutableList<ImportDeclarationExportDeclarationStatement> items)
  {
    return new Module(this.directives, items);
  }

}