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
import com.shapesecurity.functional.data.Maybe;

public class ImportSpecifier extends Node
{

  @NotNull
  public final Maybe<String> name;

  @NotNull
  public final BindingIdentifier binding;

  public ImportSpecifier (@NotNull SourceSpan loc, @NotNull Maybe<String> name, @NotNull BindingIdentifier binding)
  {
    super(loc);
    this.name = name;
    this.binding = binding;
  }

  public ImportSpecifier (@NotNull Maybe<String> name, @NotNull BindingIdentifier binding)
  {
    super();
    this.name = name;
    this.binding = binding;
  }

  @Override
  public boolean equals(Object object)
  {
    return object instanceof ImportSpecifier && this.name.equals(((ImportSpecifier) object).name) && this.binding.equals(((ImportSpecifier) object).binding);
  }

  @Override
  public int hashCode()
  {
    int code = HashCodeBuilder.put(0, "ImportSpecifier");
    code = HashCodeBuilder.put(code, this.name);
    code = HashCodeBuilder.put(code, this.binding);
    return code;
  }

  @NotNull
  public Maybe<String> getName()
  {
    return this.name;
  }

  @NotNull
  public BindingIdentifier getBinding()
  {
    return this.binding;
  }

  @NotNull
  public ImportSpecifier setName(@NotNull Maybe<String> name)
  {
    return new ImportSpecifier(name, this.binding);
  }

  @NotNull
  public ImportSpecifier setBinding(@NotNull BindingIdentifier binding)
  {
    return new ImportSpecifier(this.name, binding);
  }

}