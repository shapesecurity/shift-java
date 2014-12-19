package com.shapesecurity.shift.js.benchmark;

import java.io.IOException;

import com.shapesecurity.shift.js.TestBase;
import com.shapesecurity.shift.js.ast.Script;
import com.shapesecurity.shift.js.codegen.CodeGen;
import com.shapesecurity.shift.js.minifier.Minifier;
import com.shapesecurity.shift.js.parser.JsError;
import com.shapesecurity.shift.js.parser.Parser;
import com.shapesecurity.shift.js.scope.ScopeAnalyzer;
import com.shapesecurity.shift.js.visitor.CloneReducer;
import com.shapesecurity.shift.js.visitor.LazyCloner;

import org.junit.Test;

public class BenchmarkTest extends TestBase {

  @Test
  public void benchmarkClone() throws IOException, JsError {
    String source = readLibrary("angular-1.2.5.js");
    Script program = Parser.parse(source);
    System.out.println("Cloner warm-up started.");
    for (int i = 0; i < 1000; i++) {
      program.reduce(CloneReducer.INSTANCE);
    }
    System.out.println("Cloner warm-up finished.");
    startProfiling();
    final int N = 500;
    long start = System.nanoTime();
    for (int i = 0; i < N; i++) {
      program.reduce(CloneReducer.INSTANCE);
    }
    stopProfiling();
    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
    System.out.printf("Cloner time: %.3fms\n", elapsed);
  }

  public void startProfiling() {

  }

  public void stopProfiling() {

  }

  @Test
  public void benchmarkLazyClone() throws IOException, JsError {
    String source = readLibrary("angular-1.2.5.js");
    Script program = Parser.parse(source);
    System.out.println("LazyCloner warm-up started.");
    for (int i = 0; i < 1000; i++) {
      program.reduce(LazyCloner.INSTANCE);
    }
    System.out.println("LazyCloner warm-up finished.");
    final int N = 500;
    long start = System.nanoTime();
    for (int i = 0; i < N; i++) {
      program.reduce(LazyCloner.INSTANCE);
    }
    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
    System.out.printf("LazyCloner time: %.3fms\n", elapsed);
  }


  @Test
  public void benchmarkScopeAnalysis() throws IOException, JsError {
    String source = readLibrary("angular-1.2.5.js");
    System.out.println("ScopeAnalysis warm-up started.");
    Script program = Parser.parse(source);
    for (int i = 0; i < 2000; i++) {
      ScopeAnalyzer.analyze(program);
    }
    System.out.println("ScopeAnalysis warm-up finished.");
    final int N = 500;
    long start = System.nanoTime();
    for (int i = 0; i < N; i++) {
      ScopeAnalyzer.analyze(program);
    }
    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
    System.out.printf("ScopeAnalysis time: %.3fms\n", elapsed);
  }

  @Test
  public void benchmarkMinifier() throws IOException, JsError {
    String source = readLibrary("angular-1.2.5.js");
    Script program = Parser.parse(source);
    System.out.println("Minifier warm-up started.");
    for (int i = 0; i < 1000; i++) {
      CodeGen.codeGen(Minifier.minify(program));
    }
    System.out.println("Minifier warm-up finished.");
    final int N = 500;
    long start = System.nanoTime();
    for (int i = 0; i < N; i++) {
      CodeGen.codeGen(Minifier.minify(program));
    }
    double elapsed = (System.nanoTime() - start) * 1e-6 / N;
    System.out.printf("Minifier time: %.3fms\n", elapsed);
  }
}
